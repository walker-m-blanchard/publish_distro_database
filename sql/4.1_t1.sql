-- ============================================================
-- Task 1: Editing and Publishing
-- ============================================================

-- Assumption: PubID values 1-6 and 8-9 already exist in the database.
-- Assumption: EmployeeID 5, 6, 7, 8 are editors.
-- Assumption: We are entering a new publication with PubID = 7.

-- -------------------------------------------------------
-- Op 1: Enter basic information on a new publication
-- -------------------------------------------------------
-- Assumption: The new publication is a book titled 'Artificial Intelligence:
--             A Modern Approach', with topic 'Computer Science', PubID = 7.
INSERT INTO Publications (PubID, PubTitle, PubType, Topic)
VALUES (7, 'Artificial Intelligence: A Modern Approach', 'Book', 'Computer Science');

INSERT INTO Books (PubID)
VALUES (7);

-- -------------------------------------------------------
-- Op 2: Update publication information
-- -------------------------------------------------------
-- Assumption: PubID = 5 previously had no title (NULL).
--             We now update it to 'Advanced Mathematics'.
UPDATE Publications
SET PubTitle = 'Advanced Mathematics'
WHERE PubID = 5;

-- -------------------------------------------------------
-- Op 3: Assign an editor to a publication
-- -------------------------------------------------------
-- Assumption: Editor with EmployeeID = 6 is being assigned to PubID = 7.
INSERT INTO Edits (EmployeeID, PubID)
VALUES (6, 7);

-- -------------------------------------------------------
-- Op 4: Remove an editor from a publication
-- -------------------------------------------------------
-- Assumption: Editor with EmployeeID = 6 is being removed from PubID = 2.
DELETE FROM Edits
WHERE EmployeeID = 6 AND PubID = 2;

-- -------------------------------------------------------
-- Op 5: View all publications assigned to a specific editor
-- -------------------------------------------------------
-- Assumption: We want to view all publications assigned to editor EmployeeID = 5.
SELECT p.PubID, p.PubTitle, p.PubType, p.Topic
FROM Publications p
JOIN Edits e ON p.PubID = e.PubID
WHERE e.EmployeeID = 5;

-- -------------------------------------------------------
-- Op 6: Add an article to a periodical issue (table of contents)
-- -------------------------------------------------------
-- Assumption: PublicationUnitID = 8 (an existing article) is being added
--             to IssueID = 2.
INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (8, 2);

-- -------------------------------------------------------
-- Op 7: Delete an article from a periodical issue (table of contents)
-- -------------------------------------------------------
-- Assumption: PublicationUnitID = 3 is being removed from IssueID = 2.
DELETE FROM IssueArticle
WHERE PublicationUnitID = 3 AND IssueID = 2;

-- -------------------------------------------------------
-- Op 8: Add a chapter to a book edition (table of contents)
-- -------------------------------------------------------
-- Assumption: PublicationUnitID = 2 (an existing chapter) is being added
--             to ISBN = '978-1-119-54902-3'.
INSERT INTO EditionChapter (PublicationUnitID, ISBN)
VALUES (2, '978-1-119-54902-3');

-- -------------------------------------------------------
-- Op 9: Delete a chapter from a book edition (table of contents)
-- -------------------------------------------------------
-- Assumption: PublicationUnitID = 1 is being removed from
--             ISBN = '978-0-262-03384-8'.
DELETE FROM EditionChapter
WHERE PublicationUnitID = 1 AND ISBN = '978-0-262-03384-8';