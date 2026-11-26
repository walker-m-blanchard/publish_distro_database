-- ============================================================
-- INSERT Statements
-- Project Report 2 - Part 3
-- ============================================================

-- 1. Employees
INSERT INTO Employees (EmployeeID, Name, IsStaff) VALUES
(1,  'Alice Johnson',   TRUE),   -- Manager
(2,  'Bob Smith',       TRUE),   -- Manager
(3,  'Eva Martinez',    TRUE),   -- Manager
(4,  'Grace Kim',       TRUE),   -- Manager
(5,  'Carol White',     TRUE),   -- Editor
(6,  'David Brown',     TRUE),   -- Editor
(7,  'Frank Lee',       TRUE),   -- Editor
(8,  'Henry Davis',     TRUE),   -- Editor
(9,  'Ivy Chen',        FALSE),  -- Author
(10, 'Jack Turner',     FALSE),  -- Author
(11, 'Karen Mills',     FALSE),  -- Author
(12, 'Leo Nguyen',      FALSE);  -- Author

-- 2. Managers
INSERT INTO Managers (EmployeeID) VALUES
(1),
(2),
(3),
(4);

-- 3. Editors
INSERT INTO Editors (EmployeeID) VALUES
(5),
(6),
(7),
(8);

-- 4. Authors
INSERT INTO Authors (EmployeeID) VALUES
(9),
(10),
(11),
(12);

-- 5. Publications
INSERT INTO Publications (PubID, PubTitle, PubType, Topic) VALUES
(1,  'Introduction to Algorithms',   'Book',         'Computer Science'),
(2,  'Nature Weekly',                'Periodical',   'Science'),
(3,  'Database Systems',             'Book',         'Computer Science'),
(4,  'Tech Trends Monthly',          'Periodical',   'Technology'),
(5,  NULL,                           'Book',         'Mathematics'),
(6,  'Health & Medicine Journal',    'Periodical',   'Medicine'),
(8,  'The Lonely Rabbit',            'Book',         'Fantasy'),
(9,  'Sports Weekly',                'Periodical',   'Sports');

-- 6. Edits
INSERT INTO Edits (EmployeeID, PubID) VALUES
(5, 1),
(5, 3),
(6, 2),
(7, 4),
(8, 5),
(8, 6);

-- 7. Books
INSERT INTO Books (PubID) VALUES
(1),
(3),
(5),
(8);

-- 8. PeriodicalPubs
INSERT INTO PeriodicalPubs (PubID, Periodicity) VALUES
(2, 'Weekly'),
(4, 'Monthly'),
(6, 'Quarterly'),
(9, 'Weekly');

-- 9. Editions
INSERT INTO Editions (ISBN, EditionNum, WrittenDate, PublishedDate, PubID) VALUES
('978-0-262-03384-8',   1,  '2020-01-15',   '2020-06-01',   1),
('978-0-262-03384-9',   2,  '2022-03-10',   '2022-09-15',   1),
('978-1-119-54902-3',   1,  '2019-07-20',   '2019-12-01',   3),
('978-1-119-54902-4',   2,  NULL,           NULL,           3),
('978-0-071-83221-0',   1,  '2021-05-05',   '2021-11-20',   5);

-- 10. Issues
INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID) VALUES
(1,  'Vol 1 Issue 1',           '2023-01-05',   '2023-01-07',   2),
(2,  'Vol 1 Issue 2',           '2023-01-12',   '2023-01-14',   2),
(3,  'January 2023 Edition',    '2023-01-20',   '2023-02-01',   4),
(4,  NULL,                      NULL,           NULL,           4),
(5,  'Spring 2023',             '2023-03-01',   '2023-04-01',   6),
(6,  'Summer 2023',             '2023-06-01',   '2023-07-01',   6);

-- 11. PublicationUnits
INSERT INTO PublicationUnits (PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID) VALUES
(1,  'Sorting Algorithms',          'Chapter',  '2020-01-10',   'Full text of sorting algorithms chapter.',     'Algorithms',       9),
(2,  'Graph Theory Basics',         'Chapter',  '2020-01-12',   'Full text of graph theory chapter.',           'Graph Theory',     10),
(3,  'Climate Change Overview',     'Article',  '2023-01-03',   'Full text of climate change article.',         'Environment',      11),
(4,  'AI in Healthcare',            'Article',  '2023-01-10',   'Full text of AI in healthcare article.',       'Technology',       12),
(5,  'Database Normalization',      'Chapter',  '2019-07-15',   'Full text of database normalization chapter.', 'Databases',        9),
(6,  'SQL Query Optimization',      'Chapter',  NULL,           NULL,                                           'Databases',        10),
(7,  'New Drug Discoveries',        'Article',  '2023-02-20',   'Full text of new drug discoveries article.',   'Medicine',         11),
(8,  'Machine Learning Trends',     'Article',  '2023-01-08',   'Full text of machine learning trends.',        'Technology',       12);

-- 12. Chapters
INSERT INTO Chapters (PublicationUnitID) VALUES
(1),
(2),
(5),
(6);

-- 13. Articles
INSERT INTO Articles (PublicationUnitID) VALUES
(3),
(4),
(8),
(7);

-- 14. EditionChapter
INSERT INTO EditionChapter (PublicationUnitID, ISBN) VALUES
(1, '978-0-262-03384-8'),
(2, '978-0-262-03384-8'),
(1, '978-0-262-03384-9'),
(5, '978-1-119-54902-3'),
(6, '978-1-119-54902-4');

-- 15. IssueArticle
INSERT INTO IssueArticle (PublicationUnitID, IssueID) VALUES
(3, 1),
(4, 1),
(3, 2),
(8, 3),
(7, 5),
(4, 3);

-- 16. Distributors
INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance) VALUES
(1, 'BookWorld Inc.',       'Retail',       '919-111-2222', 'Tom Hardy',    'Raleigh',      '123 Main St',      1500.00),
(2, 'EduPress Ltd.',        'Academic',     '919-333-4444', 'Sara Connor',  'Durham',       '456 Elm Ave',      2300.50),
(3, 'MediaHub Co.',         'Wholesale',    '704-555-6666', 'Jake Long',    'Charlotte',    '789 Oak Blvd',     500.00),
(4, 'LibraryNet',           'Academic',     '910-777-8888', 'Nina Patel',   'Wilmington',   '321 Pine Rd',      0.00),
(5, 'BookWorld Inc.',       'Retail',       '919-999-0000', 'Chris Evans',  'Raleigh',      '999 Secondary St', 750.00),
(6, 'QuickDist LLC',        'Wholesale',    '336-222-3333', 'Amy Wang',     'Greensboro',   '100 Market St',    1200.00);

-- 17. Orders
INSERT INTO Orders (OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID) VALUES
(1,  100,    1500.00,    200.00, '2023-02-01',   TRUE,   '2023-02-10',   '2023-02-20',   1,  '978-0-262-03384-8',    NULL),
(2,  50,     800.00,     100.00, '2023-02-05',   TRUE,   '2023-02-15',   '2023-02-25',   2,  '978-1-119-54902-3',    NULL),
(3,  200,    400.00,     150.00, '2023-02-10',   FALSE,  '2023-03-01',   '2023-03-15',   3,  NULL,                   1),
(4,  75,     600.00,     80.00,  '2023-02-15',   TRUE,   '2023-02-20',   '2023-03-01',   4,  NULL,                   3),
(5,  120,    1800.00,    250.00, '2023-03-01',   TRUE,   '2023-03-10',   '2023-03-20',   1,  '978-0-262-03384-9',    NULL),
(6,  60,     900.00,     120.00, '2023-03-05',   FALSE,  '2023-03-20',   '2023-04-01',   5,  NULL,                   5),
(7,  80,     1200.00,    180.00, '2023-03-10',   TRUE,   '2023-03-15',   '2023-03-25',   6,  '978-0-071-83221-0',    NULL);

-- 18. Payments
INSERT INTO Payments (PaymentID, Amount, ClaimDate) VALUES
(1,  500.00,     '2023-03-01'),
(2,  750.00,     '2023-03-15'),
(3,  300.00,     NULL),
(4,  1200.00,    '2023-04-01'),
(5,  450.00,     NULL),
(6,  600.00,     '2023-04-10'),
(7,  850.00,     NULL),
(8,  1000.00,    '2023-05-01'),
(11,  400.00,    '2024-12-15'),
(12,  200.00,    '2024-3-05'),
(13,  300.00,    '2024-07-29');

-- 19. EmployeePayments
INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID) VALUES
(1,  '2023-02-28',  9,  1),   -- Author payment for chapter
(2,  '2023-03-10',  10, 2),   -- Author payment for chapter
(3,  '2023-03-20',  11, 3),   -- Author payment for article
(4,  '2023-03-25',  12, 4),   -- Author payment for article
(5,  '2023-04-01',  9,  5),   -- Author payment for chapter
(6,  '2023-04-05',  5,  NULL), -- Staff editor salary
(7,  '2023-04-10',  6,  NULL); -- Staff editor salary

-- 20. DistPayments
INSERT INTO DistPayments (PaymentID, DistID) VALUES
(8,  1),
(11,  2),
(12,  3),
(13,  4);