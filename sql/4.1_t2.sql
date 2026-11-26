-- ============================================================
-- Task 2: Production of a Book Edition or Issue
-- ============================================================

-- -------------------------------------------------------
-- Op 1: Enter a new book edition
-- -------------------------------------------------------
-- Assumption: A new edition (3rd) of PubID = 1 is being entered with
--             ISBN = '978-0-262-03384-0', written date not yet finalized.
INSERT INTO Editions (ISBN, EditionNum, WrittenDate, PublishedDate, PubID)
VALUES ('978-0-262-03384-0', 3, NULL, NULL, 1);

-- -------------------------------------------------------
-- Op 2: Enter a new issue of a publication
-- -------------------------------------------------------
-- Assumption: A new issue of PeriodicalPub PubID = 2 is being entered
--             with IssueID = 7, title and dates not yet finalized.
INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (7, NULL, NULL, NULL, 2);

-- -------------------------------------------------------
-- Op 3: Update a book edition
-- -------------------------------------------------------
-- Assumption: ISBN = '978-0-262-03384-0' now has a confirmed written date
--             and published date.
UPDATE Editions
SET WrittenDate = '2024-01-10', PublishedDate = '2024-06-01'
WHERE ISBN = '978-0-262-03384-0';

-- -------------------------------------------------------
-- Op 4: Update a publication issue
-- -------------------------------------------------------
-- Assumption: IssueID = 7 now has a confirmed title and dates.
UPDATE Issues
SET IssueTitle = 'Vol 1 Issue 3', ProducedDate = '2023-01-19', PublishedDate = '2023-01-21'
WHERE IssueID = 7;

-- -------------------------------------------------------
-- Op 5: Delete a book edition
-- -------------------------------------------------------
-- Assumption: ISBN = '978-0-262-03384-0' is being removed from the system.
--             We must first remove any dependent rows in EditionChapter and Orders.
DELETE FROM EditionChapter
WHERE ISBN = '978-0-262-03384-0';

DELETE FROM Orders
WHERE ISBN = '978-0-262-03384-0';

DELETE FROM Editions
WHERE ISBN = '978-0-262-03384-0';

-- -------------------------------------------------------
-- Op 6: Delete a publication issue
-- -------------------------------------------------------
-- Assumption: IssueID = 7 is being removed from the system.
--             We must first remove any dependent rows in IssueArticle and Orders.
DELETE FROM IssueArticle
WHERE IssueID = 7;

DELETE FROM Orders
WHERE IssueID = 7;

DELETE FROM Issues
WHERE IssueID = 7;

-- -------------------------------------------------------
-- Op 7: Enter/update an article
--       (title, author name, topic, and date)
-- -------------------------------------------------------
-- Assumption: We are entering a new article with PublicationUnitID = 9,
--             authored by EmployeeID = 9 (Ivy Chen, an author).
INSERT INTO PublicationUnits (PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES (9, 'Deep Learning in Practice', 'Article', '2023-05-01', NULL, 'Technology', 9);

INSERT INTO Articles (PublicationUnitID)
VALUES (9);

-- Assumption: We update the topic and written date of PublicationUnitID = 9.
UPDATE PublicationUnits
SET Topic = 'Artificial Intelligence', WrittenDate = '2023-05-15'
WHERE PublicationUnitID = 9;

-- -------------------------------------------------------
-- Op 8: Enter/update a chapter
--       (title, author name, topic, and date)
-- -------------------------------------------------------
-- Assumption: We are entering a new chapter with PublicationUnitID = 10,
--             authored by EmployeeID = 11 (Karen Mills, an author).
INSERT INTO PublicationUnits (PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES (10, 'The Long Road', 'Chapter', '2024-12-14', NULL, 'Fantasy', 11);

INSERT INTO Chapters (PublicationUnitID)
VALUES (10);

-- Assumption: We update the title and author of PublicationUnitID = 10.
UPDATE PublicationUnits
SET PublicationUnitTitle = 'The Winding Road', EmployeeID = 12
WHERE PublicationUnitID = 10;

-- -------------------------------------------------------
-- Op 9: Enter/update text of an article
-- -------------------------------------------------------
-- Assumption: The full text of PublicationUnitID = 9 is now finalized.
UPDATE PublicationUnits
SET FullTextContent = 'Full text of deep learning in practice article.'
WHERE PublicationUnitID = 9;

-- -------------------------------------------------------
-- Op 10: Find books and articles by topic
-- -------------------------------------------------------
-- Assumption: We search for all publication units with topic = 'Technology'.
SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE pu.Topic = 'Technology';

-- -------------------------------------------------------
-- Op 11: Find books and articles by specific date range
-- -------------------------------------------------------
-- Assumption: We search for all publication units written between
--             '2023-01-01' and '2023-12-31'.
SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE pu.WrittenDate BETWEEN '2023-01-01' AND '2023-12-31';

-- -------------------------------------------------------
-- Op 12: Find books and articles by author name
-- -------------------------------------------------------
-- Assumption: We search for all publication units authored by 'Ivy Chen'
--             (EmployeeID = 9, an author).
SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE e.Name = 'Ivy Chen';

-- -------------------------------------------------------
-- Op 13: Enter payment for an author or editor
-- -------------------------------------------------------
-- Assumption: A new payment of $400 is issued to EmployeeID = 9 (Ivy Chen)
--             for PublicationUnitID = 9 on '2023-06-01'.
--             PaymentID = 9 is assigned.
INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (9, 400.00, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (9, '2023-06-01', 9, 9);

-- -------------------------------------------------------
-- Op 14: Update when a payment was claimed by its addressee
-- -------------------------------------------------------
-- Assumption: PaymentID = 3 has now been claimed on '2023-06-15'.
UPDATE Payments
SET ClaimDate = '2023-06-15'
WHERE PaymentID = 3;

-- -------------------------------------------------------
-- Op 15: List payments issued but not claimed within a date range
-- -------------------------------------------------------
-- Assumption: We look for unclaimed payments issued between
--             '2023-01-01' and '2023-12-31'.
SELECT p.PaymentID, p.Amount, ep.IssueDate, ep.EmployeeID, ep.PublicationUnitID
FROM Payments p
JOIN EmployeePayments ep ON p.PaymentID = ep.PaymentID
WHERE p.ClaimDate IS NULL
  AND ep.IssueDate BETWEEN '2023-01-01' AND '2023-12-31';

-- -------------------------------------------------------
-- Op 16: Compare two issues by listing their associated articles
-- -------------------------------------------------------
-- Assumption: We compare IssueID = 1 and IssueID = 3, retrieving
--             relevant article information for each issue in a single output.
SELECT 
    i.IssueID,
    i.IssueTitle,
    pu.PublicationUnitID,
    pu.PublicationUnitTitle,
    pu.Topic,
    pu.WrittenDate,
    e.Name AS AuthorName
FROM Issues i
JOIN IssueArticle a  ON i.IssueID = a.IssueID
JOIN PublicationUnits pu ON a.PublicationUnitID = pu.PublicationUnitID
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE i.IssueID IN (1, 3)
ORDER BY i.IssueID, pu.PublicationUnitID;