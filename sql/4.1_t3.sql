-- ============================================================
-- Task 3: Distribution
-- ============================================================

-- -------------------------------------------------------
-- Op 1: Enter a new distributor
-- -------------------------------------------------------
-- Assumption: A new distributor 'PaperBack Plus' is being entered with
--             DistID = 7, category 'Retail', located in Chapel Hill.
INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance)
VALUES (7, 'PaperBack Plus', 'Retail', '919-444-5555', 'Laura Green', 'Chapel Hill', '200 Franklin St', 0.00);

-- -------------------------------------------------------
-- Op 2: Update distributor information
-- -------------------------------------------------------
-- Assumption: DistID = 7 has a new phone number and representative.
UPDATE Distributors
SET Phone = '919-444-9999', Representative = 'Mark Johnson'
WHERE DistID = 7;

-- -------------------------------------------------------
-- Op 3: Delete a distributor
-- -------------------------------------------------------
-- Assumption: DistID = 7 is being removed from the system.
--             We must first remove dependent rows in Orders and DistPayments.
DELETE FROM DistPayments
WHERE DistID = 7;

DELETE FROM Orders
WHERE DistID = 7;

DELETE FROM Distributors
WHERE DistID = 7;

-- -------------------------------------------------------
-- Op 4a: input an order from a distributor for a book edition
-- -------------------------------------------------------
-- Assumption: DistID = 1 places an order for ISBN = '978-1-119-54902-3',
--             100 copies at $15.00 each, shipping $200, placed on '2023-06-01',
--             planned print date '2023-06-10', delivery date '2023-06-20'.
--             OrderID = 8.
INSERT INTO Orders (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted,
                    PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES (8, 100, 1500.00, 200.00, '2023-06-01', FALSE,
        '2023-06-10', '2023-06-20', 1, '978-1-119-54902-3', NULL);

-- -------------------------------------------------------
-- Op 4b: input an order from a distributor for a periodical issue
-- -------------------------------------------------------
-- Assumption: DistID = 2 places an order for IssueID = 2,
--             80 copies at $5.00 each, shipping $50, placed on '2023-06-05',
--             planned print date '2023-06-12', delivery date '2023-06-22'.
--             OrderID = 9.
INSERT INTO Orders (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted,
                    PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES (9, 80, 400.00, 50.00, '2023-06-05', FALSE,
        '2023-06-12', '2023-06-22', 2, NULL, 2);

-- -------------------------------------------------------
-- Op 5: Bill distributor for an order
-- -------------------------------------------------------
-- Assumption: DistID = 1 is billed for OrderID = 8.
--             Total bill = Price only = 1500.00.
--             Shipping cost is borne by the publishing house, not the distributor.
UPDATE Distributors
SET Balance = Balance + (
    SELECT Price
    FROM Orders
    WHERE OrderID = 8
)
WHERE DistID = 1;

-- -------------------------------------------------------
-- Op 6: Receive a payment and update distributor balance
-- -------------------------------------------------------
-- Assumption: DistID = 1 sends a payment of $1500.00, PaymentID = 10.
--             We record the payment and reduce the distributor's balance.
INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (10, 1500.00, '2023-06-25');

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (10, 1);

UPDATE Distributors
SET Balance = Balance - 1500.00
WHERE DistID = 1;

-- -------------------------------------------------------
-- Op 7: Identify distributors whose total billed amount
--       does not match the sum of their recorded payments
-- -------------------------------------------------------
-- Assumption: Billed amount = sum of Price only for all orders per distributor.
--             Shipping cost is not billed to distributors.
--             Recorded payments = sum of amounts in DistPayments joined
--             with Payments per distributor.
SELECT d.DistID, d.Name,
       COALESCE(SUM(o.Price), 0)        AS TotalBilled,
       COALESCE(SUM(p.Amount), 0)       AS TotalPaid
FROM Distributors d
LEFT JOIN Orders o          ON d.DistID = o.DistID
LEFT JOIN DistPayments dp   ON d.DistID = dp.DistID
LEFT JOIN Payments p        ON dp.PaymentID = p.PaymentID
GROUP BY d.DistID, d.Name
HAVING COALESCE(SUM(o.Price), 0) <> COALESCE(SUM(p.Amount), 0);

-- -------------------------------------------------------
-- Op 8: List all distributors of a specific type in a given city
-- -------------------------------------------------------
-- Assumption: We look for all distributors of category 'Retail'
--             located in 'Raleigh'.
SELECT DistID, Name, Category, Phone, Representative, City, Street, Balance
FROM Distributors
WHERE Category = 'Retail' AND City = 'Raleigh';