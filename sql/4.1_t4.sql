-- ============================================================
-- Task 4: Reports
-- ============================================================

-- -------------------------------------------------------
-- Op 1: Number and total price of copies per distributor
-- -------------------------------------------------------
SELECT d.DistID, d.Name, COALESCE(o.ISBN, o.IssueID) AS 'ISBN/IssueID',
       SUM(o.NumCopy)                  AS TotalCopies,
       SUM(o.Price)                    AS TotalPrice
FROM Distributors d
JOIN Orders o ON d.DistID = o.DistID
GROUP BY d.DistID, d.Name, COALESCE(o.ISBN, o.IssueID);

-- -------------------------------------------------------
-- Op 2: Number and total price of copies per week
-- -------------------------------------------------------
-- Assumption: We report for the week of '2023-02-01' to '2023-02-07'.
SELECT COALESCE(o.ISBN, o.IssueID) AS 'ISBN/IssueID',
       SUM(o.NumCopy)                  AS TotalCopies,
       SUM(o.Price)                    AS TotalPrice
FROM Orders o
WHERE o.PlaceDate BETWEEN '2023-02-01' AND '2023-02-07'
GROUP BY COALESCE(o.ISBN, o.IssueID);

-- -------------------------------------------------------
-- Op 3: Number and total price of copies per month
-- -------------------------------------------------------
-- Assumption: We report for the month of February 2023.
SELECT COALESCE(o.ISBN, o.IssueID) AS 'ISBN/IssueID',
       YEAR(o.PlaceDate)               AS Year,
       MONTH(o.PlaceDate)              AS Month,
       SUM(o.NumCopy)                  AS TotalCopies,
       SUM(o.Price)                    AS TotalPrice
FROM Orders o
WHERE YEAR(o.PlaceDate) = 2023 AND MONTH(o.PlaceDate) = 2
GROUP BY YEAR(o.PlaceDate), MONTH(o.PlaceDate), COALESCE(o.ISBN, o.IssueID);

-- -------------------------------------------------------
-- Op 4: Total revenue of the publishing house
-- -------------------------------------------------------
-- Assumption: Total revenue = sum of all order prices minus shipping costs,
--             since shipping costs are expenses borne by the publishing house.
SELECT SUM(Price) - SUM(ShippingCost) AS TotalRevenue
FROM Orders;

-- -------------------------------------------------------
-- Op 5: Total expenses (shipping costs and salaries)
-- -------------------------------------------------------
-- Assumption: Shipping costs come from Orders.ShippingCost.
--             Salaries are payments made to staff employees
--             (PublicationUnitID IS NULL in EmployeePayments).
SELECT
    (SELECT COALESCE(SUM(o.ShippingCost), 0) FROM Orders o)         AS TotalShippingCost,
    (SELECT COALESCE(SUM(p.Amount), 0)
     FROM Payments p
     JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID
     WHERE ep.PublicationUnitID IS NULL)                             AS TotalSalaries,
    (SELECT COALESCE(SUM(o.ShippingCost), 0) FROM Orders o) +
    (SELECT COALESCE(SUM(p.Amount), 0)
     FROM Payments p
     JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID
     WHERE ep.PublicationUnitID IS NULL)                             AS TotalExpenses;

-- -------------------------------------------------------
-- Op 6: Calculate the total current number of distributors
-- -------------------------------------------------------
SELECT COUNT(*) AS TotalDistributors
FROM Distributors;

-- -------------------------------------------------------
-- Op 7: Total revenue (since inception) per city
-- -------------------------------------------------------
-- Assumption: Revenue per city = sum of order prices minus shipping costs
--             for all orders placed by distributors in that city.
SELECT d.City,
       SUM(o.Price) - SUM(o.ShippingCost)    AS TotalRevenue
FROM Distributors d
JOIN Orders o ON d.DistID = o.DistID
GROUP BY d.City;

-- -------------------------------------------------------
-- Op 8: Total revenue (since inception) per distributor
-- -------------------------------------------------------
-- Assumption: Revenue per distributor = sum of order prices minus shipping costs.
SELECT d.DistID, d.Name,
       SUM(o.Price) - SUM(o.ShippingCost)    AS TotalRevenue
FROM Distributors d
JOIN Orders o ON d.DistID = o.DistID
GROUP BY d.DistID, d.Name;
 
-- -------------------------------------------------------
-- Op 9: Total payments to editors and authors per month
-- -------------------------------------------------------
-- Assumption: We report payments for all months in 2023.
SELECT YEAR(ep.IssueDate)   AS Year,
       MONTH(ep.IssueDate)  AS Month,
       SUM(p.Amount)        AS TotalPayments
FROM Payments p
JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID
WHERE YEAR(ep.IssueDate) = 2023
GROUP BY YEAR(ep.IssueDate), MONTH(ep.IssueDate)
ORDER BY Year, Month;

-- -------------------------------------------------------
-- Op 10: Total payments per work type
--       (book authorship, article authorship, editorial work)
-- -------------------------------------------------------
-- Assumption: Work type is determined as follows:
--   - If PublicationUnitID is NULL -> 'Editorial Work' (staff editor/salary)
--   - If PublicationUnitType = 'Chapter' -> 'Chapter Authorship'
--   - If PublicationUnitType = 'Article' -> 'Article Authorship'
SELECT
    CASE
        WHEN ep.PublicationUnitID IS NULL       THEN 'Editorial Work'
        WHEN pu.PublicationUnitType = 'Chapter' THEN 'Chapter Authorship'
        WHEN pu.PublicationUnitType = 'Article' THEN 'Article Authorship'
        ELSE 'Other'
    END                         AS WorkType,
    SUM(p.Amount)               AS TotalPayments
FROM Payments p
JOIN EmployeePayments ep            ON p.PaymentID = ep.PaymentID
LEFT JOIN PublicationUnits pu       ON ep.PublicationUnitID = pu.PublicationUnitID
GROUP BY WorkType
ORDER BY WorkType;