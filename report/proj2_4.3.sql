-- ============================================================
-- 4.3: Correctness Proofs for Two SQL Queries with Joins
-- ============================================================


-- ===========================================================
-- QUERY A: Find publication units by author name
-- ===========================================================

-- (1) SQL Query
SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE e.Name = 'Frank Lee';

-- (2) Specification
-- Return the ID, title, type, topic, and written date of all publication
-- units authored by the employee named 'Frank Lee', along with the
-- author's name.

-- (3) Relational Algebra Expression
--
-- π PublicationUnitID, PublicationUnitTitle, PublicationUnitType, Topic, WrittenDate, Name (
--     σ Name = 'Frank Lee' (
--         PublicationUnits ⋈ (pu.EmployeeID = e.EmployeeID) Employees
--     )
-- )
--
-- That is:
-- Step 1: Natural join PublicationUnits and Employees on EmployeeID.
-- Step 2: Select tuples where Name = 'Frank Lee'.
-- Step 3: Project onto the desired attributes.

-- (4) Correctness Proof
--
-- Suppose pu is any tuple in the PublicationUnits relation, and e is any
-- tuple in the Employees relation, such that pu.EmployeeID = e.EmployeeID.
-- Each such combination (pu, e) pairs a publication unit with the employee
-- who authored it, since EmployeeID in PublicationUnits is a foreign key
-- referencing Employees. The WHERE clause then filters to retain only those
-- combinations where e.Name = 'Frank Lee', meaning only publication units
-- authored by the employee named Frank Lee are kept. For each such
-- combination (pu, e), the query returns the PublicationUnitID,
-- PublicationUnitTitle, PublicationUnitType, Topic, WrittenDate, and
-- e.Name as AuthorName. These values are precisely the identifying and
-- descriptive information of each publication unit, together with the
-- name of its author. But this is exactly what our query should return;
-- see the specification.


-- ===========================================================
-- QUERY B: Compare two issues by listing their associated articles
-- ===========================================================

-- (1) SQL Query
SELECT
    i.IssueID,
    i.IssueTitle,
    pu.PublicationUnitID,
    pu.PublicationUnitTitle,
    pu.Topic,
    pu.WrittenDate,
    e.Name AS AuthorName
FROM Issues i
JOIN IssueArticle a         ON i.IssueID = a.IssueID
JOIN PublicationUnits pu ON a.PublicationUnitID = pu.PublicationUnitID
JOIN Employees e        ON pu.EmployeeID = e.EmployeeID
WHERE i.IssueID IN (1, 3)
ORDER BY i.IssueID, pu.PublicationUnitID;

-- (2) Specification
-- For each of the two issues with IssueID = 1 and IssueID = 3, return
-- the issue ID and title, along with the ID, title, topic, written date,
-- and author name of every article associated with that issue. Results
-- are ordered by issue and then by publication unit ID so that articles
-- belonging to each issue are grouped together for easy comparison.

-- (3) Relational Algebra Expression
--
-- π i.IssueID, i.IssueTitle, pu.PublicationUnitID, pu.PublicationUnitTitle,
--   pu.Topic, pu.WrittenDate, e.Name (
--
--     σ IssueID ∈ {1, 3} (
--
--         Issues
--         ⋈ (i.IssueID = a.IssueID)
--         IssueArticle
--         ⋈ (a.PublicationUnitID = pu.PublicationUnitID)
--         PublicationUnits
--         ⋈ (pu.EmployeeID = e.EmployeeID)
--         Employees
--
--     )
-- )
--
-- That is:
-- Step 1: Join Issues with IssueArticle on IssueID to pair each issue
--         with its associated articles.
-- Step 2: Join the result with PublicationUnits on PublicationUnitID
--         to retrieve article details.
-- Step 3: Join the result with Employees on EmployeeID to retrieve
--         the author name.
-- Step 4: Select tuples where IssueID is 1 or 3.
-- Step 5: Project onto the desired attributes.

-- (4) Correctness Proof
--
-- Suppose i is any tuple in the Issues relation, a is any tuple in the
-- IssueArticle relation, pu is any tuple in the PublicationUnits relation,
-- and e is any tuple in the Employees relation, such that:
--   i.IssueID = a.IssueID,
--   a.PublicationUnitID = pu.PublicationUnitID, and
--   pu.EmployeeID = e.EmployeeID.
--
-- The join i ⋈ a pairs each issue with every article that belongs to it,
-- since IssueID in IssueArticle is a foreign key referencing Issues. The join
-- with pu then enriches each (issue, article) pair with the full details
-- of the publication unit (title, type, topic, written date), since
-- PublicationUnitID in IssueArticle is a foreign key referencing
-- PublicationUnits. The join with e then adds the author name, since
-- EmployeeID in PublicationUnits is a foreign key referencing Employees.
--
-- The WHERE clause filters to retain only those combinations where
-- i.IssueID = 1 or i.IssueID = 3, meaning only articles belonging to
-- these two specific issues are returned. The ORDER BY clause groups
-- results by IssueID first and then by PublicationUnitID, so that all
-- articles for issue 1 appear before those for issue 3, enabling direct
-- side-by-side comparison.
--
-- For each qualifying combination (i, a, pu, e), the query returns the
-- IssueID and IssueTitle to identify which issue the article belongs to,
-- and the PublicationUnitID, PublicationUnitTitle, Topic, WrittenDate,
-- and AuthorName to describe the article itself. But this is exactly
-- what our query should return; see the specification.