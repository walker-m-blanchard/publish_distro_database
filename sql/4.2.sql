-- We pick below two queries (from task2 operation 11 and task3 operation 7 in 4.1)

-- SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
--        pu.Topic, pu.WrittenDate, e.Name AS AuthorName
-- FROM PublicationUnits pu
-- JOIN Employees e ON pu.EmployeeID = e.EmployeeID
-- WHERE e.Name = 'Ivy Chen';

-- SELECT d.DistID, d.Name,
--        COALESCE(SUM(o.Price + o.ShippingCost), 0)     AS TotalBilled,
--        COALESCE(SUM(p.Amount), 0)                      AS TotalPaid
-- FROM Distributors d
-- LEFT JOIN Orders o          ON d.DistID = o.DistID
-- LEFT JOIN DistPayments dp   ON d.DistID = dp.DistID
-- LEFT JOIN Payments p        ON dp.PaymentID = p.PaymentID
-- GROUP BY d.DistID, d.Name
-- HAVING COALESCE(SUM(o.Price + o.ShippingCost), 0) <> COALESCE(SUM(p.Amount), 0);

-- EXPLAINs

EXPLAIN SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE e.Name = 'Ivy Chen';

EXPLAIN SELECT d.DistID, d.Name,
       COALESCE(SUM(o.Price + o.ShippingCost), 0) AS TotalBilled,
       COALESCE(SUM(p.Amount), 0) AS TotalPaid
FROM Distributors d
LEFT JOIN Orders o ON d.DistID = o.DistID
LEFT JOIN DistPayments dp ON d.DistID = dp.DistID
LEFT JOIN Payments p ON dp.PaymentID = p.PaymentID
GROUP BY d.DistID, d.Name
HAVING COALESCE(SUM(o.Price + o.ShippingCost), 0) <> COALESCE(SUM(p.Amount), 0);


-- Index Creations:

-- Index for Query A: speed up filtering by employee name
CREATE INDEX idx_employees_name ON Employees(Name);

-- Index for Query B: speed up GROUP BY and joins on Distributors
CREATE INDEX idx_distributors_distid ON Distributors(DistID);

-- RUN the above SQLs with terminal command rather than executing the file.
