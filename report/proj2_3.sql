-- ============================================================
-- CREATE TABLE Statements
-- Project Report 2 - Part 3
-- ============================================================

-- 1. Employees
CREATE TABLE Employees (
    EmployeeID  INT             NOT NULL,
    Name        VARCHAR(100)    NOT NULL,
    IsStaff     BOOLEAN         NOT NULL,
    PRIMARY KEY (EmployeeID)
);

-- 2. Managers
CREATE TABLE Managers (
    EmployeeID  INT     NOT NULL,
    PRIMARY KEY (EmployeeID),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

-- 3. Editors
CREATE TABLE Editors (
    EmployeeID  INT     NOT NULL,
    PRIMARY KEY (EmployeeID),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

-- 4. Authors
CREATE TABLE Authors (
    EmployeeID  INT     NOT NULL,
    PRIMARY KEY (EmployeeID),
    FOREIGN KEY (EmployeeID) REFERENCES Employees(EmployeeID)
);

-- 5. Publications
CREATE TABLE Publications (
    PubID       INT             NOT NULL,
    PubTitle    VARCHAR(200)    NULL,
    PubType     VARCHAR(50)     NOT NULL,
    Topic       VARCHAR(100)    NULL,
    PRIMARY KEY (PubID)
);

-- 6. Edits
CREATE TABLE Edits (
    EmployeeID  INT     NOT NULL,
    PubID       INT     NOT NULL,
    PRIMARY KEY (EmployeeID, PubID),
    FOREIGN KEY (EmployeeID) REFERENCES Editors(EmployeeID),
    FOREIGN KEY (PubID)      REFERENCES Publications(PubID)
);

-- 7. Books
CREATE TABLE Books (
    PubID   INT     NOT NULL,
    PRIMARY KEY (PubID),
    FOREIGN KEY (PubID) REFERENCES Publications(PubID)
);

-- 8. PeriodicalPubs
CREATE TABLE PeriodicalPubs (
    PubID       INT             NOT NULL,
    Periodicity VARCHAR(50)     NOT NULL,
    PRIMARY KEY (PubID),
    FOREIGN KEY (PubID) REFERENCES Publications(PubID)
);

-- 9. Editions
CREATE TABLE Editions (
    ISBN            VARCHAR(20)     NOT NULL,
    EditionNum      INT             NOT NULL,
    WrittenDate     DATE            NULL,
    PublishedDate   DATE            NULL,
    PubID           INT             NOT NULL,
    PRIMARY KEY (ISBN),
    FOREIGN KEY (PubID) REFERENCES Books(PubID)
);

-- 10. Issues
CREATE TABLE Issues (
    IssueID         INT             NOT NULL,
    IssueTitle      VARCHAR(200)    NULL,
    ProducedDate    DATE            NULL,
    PublishedDate   DATE            NULL,
    PubID           INT             NOT NULL,
    PRIMARY KEY (IssueID),
    FOREIGN KEY (PubID) REFERENCES PeriodicalPubs(PubID)
);

-- 11. PublicationUnits
CREATE TABLE PublicationUnits (
    PublicationUnitID       INT             NOT NULL,
    PublicationUnitTitle    VARCHAR(200),
    PublicationUnitType     VARCHAR(50)     NOT NULL,
    WrittenDate             DATE,
    FullTextContent         MEDIUMTEXT,
    Topic                   VARCHAR(100),
    EmployeeID              INT             NOT NULL,
    PRIMARY KEY (PublicationUnitID),
    FOREIGN KEY (EmployeeID) REFERENCES Authors(EmployeeID)
);

-- 12. Chapters
CREATE TABLE Chapters (
    PublicationUnitID   INT             NOT NULL,
    PRIMARY KEY (PublicationUnitID),
    FOREIGN KEY (PublicationUnitID) REFERENCES PublicationUnits(PublicationUnitID)
);

-- 13. Articles
CREATE TABLE Articles (
    PublicationUnitID   INT     NOT NULL,
    PRIMARY KEY (PublicationUnitID),
    FOREIGN KEY (PublicationUnitID) REFERENCES PublicationUnits(PublicationUnitID)
);

-- 14. EditionChapter
CREATE TABLE EditionChapter (
    PublicationUnitID   INT             NOT NULL,
    ISBN                VARCHAR(20)     NOT NULL,
    PRIMARY KEY (PublicationUnitID, ISBN),
    FOREIGN KEY (PublicationUnitID) REFERENCES Chapters(PublicationUnitID),
    FOREIGN KEY (ISBN)              REFERENCES Editions(ISBN)
);

-- 15. IssueArticle
CREATE TABLE IssueArticle (
    PublicationUnitID   INT     NOT NULL,
    IssueID             INT     NOT NULL,
    PRIMARY KEY (PublicationUnitID, IssueID),
    FOREIGN KEY (PublicationUnitID) REFERENCES Articles(PublicationUnitID),
    FOREIGN KEY (IssueID)           REFERENCES Issues(IssueID)
);

-- 16. Distributors
CREATE TABLE Distributors (
    DistID          INT             NOT NULL,
    Name            VARCHAR(100)    NOT NULL,
    Category        VARCHAR(50)     NOT NULL,
    Phone           VARCHAR(20)     NOT NULL,
    Representative  VARCHAR(100)    NOT NULL,
    City            VARCHAR(100)    NOT NULL,
    Street          VARCHAR(200)    NOT NULL,
    Balance         DECIMAL(10,2)   NOT NULL,
    PRIMARY KEY (DistID),
    UNIQUE (City, Street)
);

-- 17. Orders
CREATE TABLE Orders (
    OrderID         INT             NOT NULL,
    NumCopy         INT             NOT NULL,
    Price           DECIMAL(10,2)   NOT NULL,
    ShippingCost    DECIMAL(10,2)   NOT NULL,
    PlaceDate       DATE            NOT NULL,
    IsPrinted       BOOLEAN         NOT NULL,
    PlannedPrintDate DATE           NOT NULL,
    DeliveryDate    DATE            NOT NULL,
    DistID          INT             NOT NULL,
    ISBN            VARCHAR(20)     NULL,
    IssueID         INT             NULL,
    PRIMARY KEY (OrderID),
    FOREIGN KEY (DistID)    REFERENCES Distributors(DistID),
    FOREIGN KEY (ISBN)      REFERENCES Editions(ISBN),
    FOREIGN KEY (IssueID)   REFERENCES Issues(IssueID),
    -- Exactly one of ISBN or IssueID must be non-null
    CONSTRAINT chk_order_target CHECK (
        (ISBN IS NOT NULL AND IssueID IS NULL) OR
        (ISBN IS NULL AND IssueID IS NOT NULL)
    )
);

-- 18. Payments
CREATE TABLE Payments (
    PaymentID   INT             NOT NULL,
    Amount      DECIMAL(10,2)   NOT NULL,
    ClaimDate   DATE            NULL,
    PRIMARY KEY (PaymentID)
);

-- 19. EmployeePayments
CREATE TABLE EmployeePayments (
    PaymentID           INT     NOT NULL,
    IssueDate           DATE    NOT NULL,   -- Need to be earlier than ClaimDate in Payments
    EmployeeID          INT     NOT NULL,
    PublicationUnitID   INT     NULL,
    PRIMARY KEY (PaymentID),
    FOREIGN KEY (PaymentID)         REFERENCES Payments(PaymentID),
    FOREIGN KEY (EmployeeID)        REFERENCES Employees(EmployeeID),
    FOREIGN KEY (PublicationUnitID) REFERENCES PublicationUnits(PublicationUnitID)
);

-- 20. DistPayments
CREATE TABLE DistPayments (
    PaymentID   INT     NOT NULL,
    DistID      INT     NOT NULL,
    PRIMARY KEY (PaymentID),
    FOREIGN KEY (PaymentID) REFERENCES Payments(PaymentID),
    FOREIGN KEY (DistID)    REFERENCES Distributors(DistID)
);



-- ============================================================
-- SELECT * Queries
-- Project Report 2 - Part 3
-- ============================================================

SELECT * FROM Employees;

SELECT * FROM Managers;

SELECT * FROM Editors;

SELECT * FROM Authors;

SELECT * FROM Publications;

SELECT * FROM Edits;

SELECT * FROM Books;

SELECT * FROM PeriodicalPubs;

SELECT * FROM Editions;

SELECT * FROM Issues;

SELECT * FROM PublicationUnits;

SELECT * FROM Chapters;

SELECT * FROM Articles;

SELECT * FROM EditionChapter;

SELECT * FROM IssueArticle;

SELECT * FROM Distributors;

SELECT * FROM Orders;

SELECT * FROM Payments;

SELECT * FROM EmployeePayments;

SELECT * FROM DistPayments;



+------------+---------------+---------+
| EmployeeID | Name          | IsStaff |
+------------+---------------+---------+
|          1 | Alice Johnson |       1 |
|          2 | Bob Smith     |       1 |
|          3 | Carol White   |       0 |
|          4 | David Brown   |       0 |
|          5 | Eva Martinez  |       1 |
|          6 | Frank Lee     |       0 |
|          7 | Grace Kim     |       1 |
|          8 | Henry Davis   |       0 |
+------------+---------------+---------+


+------------+
| EmployeeID |
+------------+
|          1 |
|          2 |
|          5 |
|          7 |
+------------+


+------------+
| EmployeeID |
+------------+
|          1 |
|          2 |
|          5 |
|          7 |
+------------+


+------------+
| EmployeeID |
+------------+
|          3 |
|          4 |
|          6 |
|          8 |
+------------+


+-------+----------------------------+------------+------------------+
| PubID | PubTitle                   | PubType    | Topic            |
+-------+----------------------------+------------+------------------+
|     1 | Introduction to Algorithms | Book       | Computer Science |
|     2 | Nature Weekly              | Periodical | Science          |
|     3 | Database Systems           | Book       | Computer Science |
|     4 | Tech Trends Monthly        | Periodical | Technology       |
|     5 | NULL                       | Book       | Mathematics      |
|     6 | Health & Medicine Journal  | Periodical | Medicine         |
+-------+----------------------------+------------+------------------+


+------------+-------+
| EmployeeID | PubID |
+------------+-------+
|          1 |     1 |
|          1 |     3 |
|          2 |     2 |
|          5 |     4 |
|          7 |     5 |
|          7 |     6 |
+------------+-------+


+-------+
| PubID |
+-------+
|     1 |
|     3 |
|     5 |
+-------+


+-------+-------------+
| PubID | Periodicity |
+-------+-------------+
|     2 | Weekly      |
|     4 | Monthly     |
|     6 | Quarterly   |
+-------+-------------+


+-------------------+------------+-------------+---------------+-------+
| ISBN              | EditionNum | WrittenDate | PublishedDate | PubID |
+-------------------+------------+-------------+---------------+-------+
| 978-0-071-83221-0 |          1 | 2021-05-05  | 2021-11-20    |     5 |
| 978-0-262-03384-8 |          1 | 2020-01-15  | 2020-06-01    |     1 |
| 978-0-262-03384-9 |          2 | 2022-03-10  | 2022-09-15    |     1 |
| 978-1-119-54902-3 |          1 | 2019-07-20  | 2019-12-01    |     3 |
| 978-1-119-54902-4 |          2 | NULL        | NULL          |     3 |
+-------------------+------------+-------------+---------------+-------+


+---------+----------------------+--------------+---------------+-------+
| IssueID | IssueTitle           | ProducedDate | PublishedDate | PubID |
+---------+----------------------+--------------+---------------+-------+
|       1 | Vol 1 Issue 1        | 2023-01-05   | 2023-01-07    |     2 |
|       2 | Vol 1 Issue 2        | 2023-01-12   | 2023-01-14    |     2 |
|       3 | January 2023 Edition | 2023-01-20   | 2023-02-01    |     4 |
|       4 | NULL                 | NULL         | NULL          |     4 |
|       5 | Spring 2023          | 2023-03-01   | 2023-04-01    |     6 |
|       6 | Summer 2023          | 2023-06-01   | 2023-07-01    |     6 |
+---------+----------------------+--------------+---------------+-------+


+-------------------+-------------------------+---------------------+-------------+----------------------------------------------+--------------+------------+
| PublicationUnitID | PublicationUnitTitle    | PublicationUnitType | WrittenDate | FullTextContent                              | Topic        | EmployeeID |
+-------------------+-------------------------+---------------------+-------------+----------------------------------------------+--------------+------------+
|                 1 | Sorting Algorithms      | Chapter             | 2020-01-10  | Full text of sorting algorithms chapter.     | Algorithms   |          3 |
|                 2 | Graph Theory Basics     | Chapter             | 2020-01-12  | Full text of graph theory chapter.           | Graph Theory |          4 |
|                 3 | Climate Change Overview | Article             | 2023-01-03  | Full text of climate change article.         | Environment  |          6 |
|                 4 | AI in Healthcare        | Article             | 2023-01-10  | Full text of AI in healthcare article.       | Technology   |          8 |
|                 5 | Database Normalization  | Chapter             | 2019-07-15  | Full text of database normalization chapter. | Databases    |          3 |
|                 6 | SQL Query Optimization  | Chapter             | NULL        | NULL                                         | Databases    |          4 |
|                 7 | New Drug Discoveries    | Article             | 2023-02-20  | Full text of new drug discoveries article.   | Medicine     |          6 |
|                 8 | Machine Learning Trends | Article             | 2023-01-08  | Full text of machine learning trends.        | Technology   |          8 |
+-------------------+-------------------------+---------------------+-------------+----------------------------------------------+--------------+------------+


+-------------------+
| PublicationUnitID |
+-------------------+
|                 1 |
|                 2 |
|                 5 |
|                 6 |
+-------------------+


+-------------------+
| PublicationUnitID |
+-------------------+
|                 3 |
|                 4 |
|                 7 |
|                 8 |
+-------------------+


+-------------------+-------------------+
| PublicationUnitID | ISBN              |
+-------------------+-------------------+
|                 1 | 978-0-262-03384-8 |
|                 1 | 978-0-262-03384-9 |
|                 2 | 978-0-262-03384-8 |
|                 5 | 978-1-119-54902-3 |
|                 6 | 978-1-119-54902-4 |
+-------------------+-------------------+


+-------------------+---------+
| PublicationUnitID | IssueID |
+-------------------+---------+
|                 3 |       1 |
|                 3 |       2 |
|                 4 |       1 |
|                 4 |       3 |
|                 7 |       5 |
|                 8 |       3 |
+-------------------+---------+


+--------+----------------+-----------+--------------+----------------+------------+------------------+---------+
| DistID | Name           | Category  | Phone        | Representative | City       | Street           | Balance |
+--------+----------------+-----------+--------------+----------------+------------+------------------+---------+
|      1 | BookWorld Inc. | Retail    | 919-111-2222 | Tom Hardy      | Raleigh    | 123 Main St      | 1500.00 |
|      2 | EduPress Ltd.  | Academic  | 919-333-4444 | Sara Connor    | Durham     | 456 Elm Ave      | 2300.50 |
|      3 | MediaHub Co.   | Wholesale | 704-555-6666 | Jake Long      | Charlotte  | 789 Oak Blvd     |  500.00 |
|      4 | LibraryNet     | Academic  | 910-777-8888 | Nina Patel     | Wilmington | 321 Pine Rd      |    0.00 |
|      5 | BookWorld Inc. | Retail    | 919-999-0000 | Chris Evans    | Raleigh    | 999 Secondary St |  750.00 |
|      6 | QuickDist LLC  | Wholesale | 336-222-3333 | Amy Wang       | Greensboro | 100 Market St    | 1200.00 |
+--------+----------------+-----------+--------------+----------------+------------+------------------+---------+


+---------+---------+---------+--------------+------------+-----------+------------------+--------------+--------+-------------------+---------+
| OrderID | NumCopy | Price   | ShippingCost | PlaceDate  | IsPrinted | PlannedPrintDate | DeliveryDate | DistID | ISBN              | IssueID |
+---------+---------+---------+--------------+------------+-----------+------------------+--------------+--------+-------------------+---------+
|       1 |     100 | 1500.00 |       200.00 | 2023-02-01 |         1 | 2023-02-10       | 2023-02-20   |      1 | 978-0-262-03384-8 |    NULL |
|       2 |      50 |  800.00 |       100.00 | 2023-02-05 |         1 | 2023-02-15       | 2023-02-25   |      2 | 978-1-119-54902-3 |    NULL |
|       3 |     200 |  400.00 |       150.00 | 2023-02-10 |         0 | 2023-03-01       | 2023-03-15   |      3 | NULL              |       1 |
|       4 |      75 |  600.00 |        80.00 | 2023-02-15 |         1 | 2023-02-20       | 2023-03-01   |      4 | NULL              |       3 |
|       5 |     120 | 1800.00 |       250.00 | 2023-03-01 |         1 | 2023-03-10       | 2023-03-20   |      1 | 978-0-262-03384-9 |    NULL |
|       6 |      60 |  900.00 |       120.00 | 2023-03-05 |         0 | 2023-03-20       | 2023-04-01   |      5 | NULL              |       5 |
|       7 |      80 | 1200.00 |       180.00 | 2023-03-10 |         1 | 2023-03-15       | 2023-03-25   |      6 | 978-0-071-83221-0 |    NULL |
+---------+---------+---------+--------------+------------+-----------+------------------+--------------+--------+-------------------+---------+


+-----------+---------+------------+
| PaymentID | Amount  | ClaimDate  |
+-----------+---------+------------+
|         1 |  500.00 | 2023-03-01 |
|         2 |  750.00 | 2023-03-15 |
|         3 |  300.00 | NULL       |
|         4 | 1200.00 | 2023-04-01 |
|         5 |  450.00 | NULL       |
|         6 |  600.00 | 2023-04-10 |
|         7 |  850.00 | NULL       |
|         8 | 1000.00 | 2023-05-01 |
+-----------+---------+------------+


+-----------+------------+------------+-------------------+
| PaymentID | IssueDate  | EmployeeID | PublicationUnitID |
+-----------+------------+------------+-------------------+
|         1 | 2023-02-28 |          3 |                 1 |
|         2 | 2023-03-10 |          4 |                 2 |
|         3 | 2023-03-20 |          6 |                 3 |
|         4 | 2023-03-25 |          8 |                 4 |
|         5 | 2023-04-01 |          3 |                 5 |
|         6 | 2023-04-05 |          1 |              NULL |
|         7 | 2023-04-10 |          2 |              NULL |
+-----------+------------+------------+-------------------+


+-----------+--------+
| PaymentID | DistID |
+-----------+--------+
|         8 |      1 |
+-----------+--------+
