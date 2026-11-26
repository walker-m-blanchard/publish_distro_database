-- ==========================================
-- DEMO DATA
-- Revised to match current schema
-- ==========================================

-- ==========================================
-- EMPLOYEES
-- ==========================================
INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (1, 'Olivia Bennett', TRUE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (2, 'Daniel Whitmore', TRUE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (3, 'Alice Morgan', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (4, 'Sarah Lee', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (5, 'Michael Brown', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (6, 'Emily Zhang', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (7, 'Daniel Kim', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (8, 'Laura Martinez', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (9, 'Robert Singh', FALSE);

INSERT INTO Employees (EmployeeID, Name, IsStaff)
VALUES (10, 'Helena Strauss', TRUE);

-- ==========================================
-- EDITORS
-- ==========================================
INSERT INTO Editors (EmployeeID)
VALUES (1);

INSERT INTO Editors (EmployeeID)
VALUES (2);

INSERT INTO Editors (EmployeeID)
VALUES (10);

-- ==========================================
-- AUTHORS
-- ==========================================
INSERT INTO Authors (EmployeeID)
VALUES (3);

INSERT INTO Authors (EmployeeID)
VALUES (4);

INSERT INTO Authors (EmployeeID)
VALUES (5);

INSERT INTO Authors (EmployeeID)
VALUES (6);

INSERT INTO Authors (EmployeeID)
VALUES (7);

INSERT INTO Authors (EmployeeID)
VALUES (8);

INSERT INTO Authors (EmployeeID)
VALUES (9);

-- ==========================================
-- PUBLICATIONS
-- ==========================================
INSERT INTO Publications (PubID, PubTitle, PubType, Topic)
VALUES (1, 'Foundations of Distributed Databases', 'Book', 'Databases');

INSERT INTO Publications (PubID, PubTitle, PubType, Topic)
VALUES (2, 'Tech Weekly', 'Magazine', 'Technology');

INSERT INTO Publications (PubID, PubTitle, PubType, Topic)
VALUES (3, 'Global Affairs Review', 'Journal', 'News');

-- ==========================================
-- EDITS
-- EmployeeID must be INT, not employee name
-- Olivia Bennett = 1
-- Daniel Whitmore = 2
-- Helena Strauss = 10
-- ==========================================
INSERT INTO Edits (EmployeeID, PubID)
VALUES (1, 1);

INSERT INTO Edits (EmployeeID, PubID)
VALUES (10, 2);

INSERT INTO Edits (EmployeeID, PubID)
VALUES (1, 2);

INSERT INTO Edits (EmployeeID, PubID)
VALUES (2, 3);

-- ==========================================
-- BOOKS
-- ==========================================
INSERT INTO Books (PubID)
VALUES (1);

-- ==========================================
-- PERIODICAL PUBS
-- ==========================================
INSERT INTO PeriodicalPubs (PubID, Periodicity)
VALUES (2, 'Weekly');

INSERT INTO PeriodicalPubs (PubID, Periodicity)
VALUES (3, 'Monthly');

-- ==========================================
-- EDITIONS
-- ==========================================
INSERT INTO Editions (ISBN, EditionNum, WrittenDate, PublishedDate, PubID)
VALUES ('9781458300001', 1, NULL, '2025-03-15', 1);

INSERT INTO Editions (ISBN, EditionNum, WrittenDate, PublishedDate, PubID)
VALUES ('9781458300002', 2, NULL, '2026-02-01', 1);

-- ==========================================
-- ISSUES
-- ==========================================
INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (1, NULL, NULL, '2026-02-01', 2);

INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (2, NULL, NULL, '2026-02-08', 2);

INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (3, NULL, NULL, '2026-02-15', 2);

INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (4, NULL, NULL, '2026-01-01', 3);

INSERT INTO Issues (IssueID, IssueTitle, ProducedDate, PublishedDate, PubID)
VALUES (5, NULL, NULL, '2026-02-01', 3);

-- ==========================================
-- PUBLICATION UNITS
-- ==========================================
INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        1,
        'Ch1 Distributed Transactions',
        'Chapter',
        '2025-03-15',
        'Modern distributed database systems coordinate data across multiple sites, often separated by large geographic distances. Ensuring that a transaction commits consistently at every participating node is one of the central challenges of distributed computing.',
        'Distributed Systems',
        3
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        2,
        'Ch1 Distributed Transactions (Edition 2)',
        'Chapter',
        '2026-02-01',
        'As distributed infrastructures scale to cloud environments, transaction coordination becomes increasingly complex. Newer consensus-based approaches build upon traditional commit protocols to improve fault tolerance and availability.',
        'Distributed Systems',
        3
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        3,
        'Ch2 Data Replication',
        'Chapter',
        '2026-02-01',
        'Data replication allows distributed systems to improve reliability and performance by maintaining multiple copies of data. Different replication strategies, however, introduce trade-offs between consistency, latency, and availability.',
        'Data Management',
        3
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        4,
        'AI in 2026',
        'Article',
        '2026-01-20',
        'Artificial intelligence systems in 2026 operate at unprecedented scale, integrating multimodal inputs and real-time decision making. Rapid advances in model efficiency have made AI deployment feasible across industries.',
        'Artificial Intelligence',
        4
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        5,
        'Quantum Computing Basics',
        'Article',
        '2026-01-25',
        'Quantum computing departs fundamentally from classical computation by leveraging superposition and entanglement. Even small quantum systems demonstrate behaviors that have no classical equivalent.',
        'Quantum Computing',
        5
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        6,
        'Data Privacy in Practice',
        'Article',
        '2026-01-20',
        'Organizations increasingly face the challenge of protecting user data while maintaining usability and performance. Regulatory frameworks now require concrete safeguards and transparent data-handling practices.',
        'Cybersecurity',
        6
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        7,
        'Edge AI Applications',
        'Article',
        '2026-02-01',
        'Deploying AI models on edge devices reduces latency and enhances privacy by processing data locally. Advances in hardware acceleration have made on-device inference more practical than ever before.',
        'Artificial Intelligence',
        7
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        8,
        'Election Trends Worldwide',
        'Article',
        '2025-12-15',
        'Recent elections across multiple regions reveal shifting voter priorities and evolving campaign strategies. Digital platforms now play a decisive role in shaping public discourse.',
        'International Politics',
        8
    );

INSERT INTO PublicationUnits
(PublicationUnitID, PublicationUnitTitle, PublicationUnitType, WrittenDate, FullTextContent, Topic, EmployeeID)
VALUES
    (
        9,
        'Energy Policy Updates',
        'Article',
        '2026-01-05',
        'Global energy markets continue to respond to geopolitical pressures and climate commitments. Policymakers are balancing economic growth with long-term sustainability goals.',
        'Public Policy',
        9
    );

-- ==========================================
-- CHAPTERS
-- ==========================================
INSERT INTO Chapters (PublicationUnitID)
VALUES (1);

INSERT INTO Chapters (PublicationUnitID)
VALUES (2);

INSERT INTO Chapters (PublicationUnitID)
VALUES (3);

-- ==========================================
-- ARTICLES
-- ==========================================
INSERT INTO Articles (PublicationUnitID)
VALUES (4);

INSERT INTO Articles (PublicationUnitID)
VALUES (5);

INSERT INTO Articles (PublicationUnitID)
VALUES (6);

INSERT INTO Articles (PublicationUnitID)
VALUES (7);

INSERT INTO Articles (PublicationUnitID)
VALUES (8);

INSERT INTO Articles (PublicationUnitID)
VALUES (9);

-- ==========================================
-- EDITION CHAPTER
-- ==========================================
INSERT INTO EditionChapter (PublicationUnitID, ISBN)
VALUES (1, '9781458300001');

INSERT INTO EditionChapter (PublicationUnitID, ISBN)
VALUES (2, '9781458300002');

INSERT INTO EditionChapter (PublicationUnitID, ISBN)
VALUES (3, '9781458300002');

-- ==========================================
-- ISSUE ARTICLE
-- ==========================================
INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (4, 1);

INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (5, 2);

INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (6, 2);

INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (7, 3);

INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (8, 4);

INSERT INTO IssueArticle (PublicationUnitID, IssueID)
VALUES (9, 5);

-- ==========================================
-- DISTRIBUTORS
-- ==========================================
/* Optional ZIP / balance-as-of values from demo sheet are omitted
   because current schema only stores City, Street, and Balance. */

INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance)
VALUES
    (1, 'Triangle Books', 'Bookstore', '(919) 555-2101', 'Laura Thompson', 'Raleigh, NC', '142 Hillsborough Street', 4850.00);

INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance)
VALUES
    (2, 'Eastern Academic Wholesale', 'Wholesale Distributor', '(704) 555-7782', 'Mark Reynolds', 'Charlotte, NC', '850 Logistics Parkway', 12300.00);

INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance)
VALUES
    (3, 'Wake County Public Library', 'Library', '(919) 555-4433', 'Emily Carter', 'Raleigh, NC', '336 Fayetteville Street', 0.00);

INSERT INTO Distributors (DistID, Name, Category, Phone, Representative, City, Street, Balance)
VALUES
    (4, 'Capitol City Books', 'Bookstore', '(919) 555-6610', 'Daniel Wright', 'Durham, NC', '91 Market Square', 2175.50);

-- ==========================================
-- ORDERS
-- Price field in current schema appears to store total order price,
-- matching your existing demo inserts.
-- ==========================================
INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (1, 90, 1350.00, 160.00, '2026-01-03', TRUE, '2026-03-01', '2026-04-01', 4, NULL, 4);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (2, 75, 1125.00, 140.00, '2026-02-03', TRUE, '2026-03-01', '2026-04-01', 4, NULL, 5);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (3, 650, 780.00, 750.00, '2026-02-03', TRUE, '2026-03-01', '2026-04-01', 2, NULL, 1);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (4, 650, 7800.00, 750.00, '2026-02-11', TRUE, '2026-03-01', '2026-04-01', 2, NULL, 2);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (5, 500, 6000.00, 600.00, '2026-02-17', TRUE, '2026-03-01', '2026-04-01', 2, NULL, 3);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (6, 120, 10200.00, 350.00, '2026-02-10', TRUE, '2026-03-01', '2026-04-01', 1, '9781458300002', NULL);

INSERT INTO Orders
(OrderID, NumCopy, Price, ShippingCost, PlaceDate, IsPrinted, PlannedPrintDate, DeliveryDate, DistID, ISBN, IssueID)
VALUES
    (7, 40, 3400.00, 120.00, '2026-02-12', TRUE, '2026-03-01', '2026-04-01', 3, '9781458300002', NULL);

-- ==========================================
-- PAYMENTS
-- ==========================================
INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (1, 2500.00, '2025-04-05');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (2, 1000.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (3, 2000.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (4, 1200.00, '2026-02-03');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (5, 1100.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (6, 1150.00, '2026-02-02');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (7, 1300.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (8, 1250.00, '2026-01-05');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (9, 1180.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (10, 4000.00, '2025-04-10');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (11, 4000.00, '2026-02-10');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (12, 2500.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (13, 2000.00, '2026-03-05');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (14, 3000.00, '2026-02-07');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (15, 3000.00, NULL);

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (16, 15000.00, '2026-02-20');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (17, 26000.00, '2026-02-15');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (18, 5100.00, '2026-02-20');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (19, 3520.00, '2026-02-18');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (20, 3600.00, '2026-01-20');

INSERT INTO Payments (PaymentID, Amount, ClaimDate)
VALUES (21, 1299.50, '2026-02-10');

-- ==========================================
-- EMPLOYEE PAYMENTS
-- Editors are paid with PublicationUnitID = NULL
-- ==========================================
INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (1, '2025-03-20', 3, 1);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (2, '2026-02-05', 3, 2);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (3, '2026-02-10', 3, 3);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (4, '2026-01-28', 4, 4);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (5, '2026-01-30', 5, 5);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (6, '2026-01-25', 6, 6);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (7, '2026-02-05', 7, 7);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (8, '2025-12-20', 8, 8);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (9, '2026-01-10', 9, 9);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (10, '2025-03-15', 1, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (11, '2026-02-01', 1, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (12, '2026-02-28', 1, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (13, '2026-02-28', 10, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (14, '2026-01-31', 2, NULL);

INSERT INTO EmployeePayments (PaymentID, IssueDate, EmployeeID, PublicationUnitID)
VALUES (15, '2026-02-28', 2, NULL);

-- ==========================================
-- DISTRIBUTOR PAYMENTS
-- ==========================================
INSERT INTO DistPayments (PaymentID, DistID)
VALUES (16, 1);

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (17, 2);

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (18, 2);

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (19, 3);

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (20, 4);

INSERT INTO DistPayments (PaymentID, DistID)
VALUES (21, 4);