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
    PublicationUnitID   INT             NOT NULL,
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