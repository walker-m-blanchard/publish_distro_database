-- ============================================================
-- 4.2: EXPLAIN Analysis and Index Creation
-- ============================================================

-- ===========================================================
-- QUERY A: Find books and articles by author name
-- ===========================================================

-- (1) SQL Query
SELECT pu.PublicationUnitID, pu.PublicationUnitTitle, pu.PublicationUnitType,
       pu.Topic, pu.WrittenDate, e.Name AS AuthorName
FROM PublicationUnits pu
JOIN Employees e ON pu.EmployeeID = e.EmployeeID
WHERE e.Name = 'Frank Lee';

-- (2) EXPLAIN before index (shows full table scan on PublicationUnits)
-- +----+-------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
-- | id | select_type | table | type   | possible_keys | key     | key_len | ref                 | rows | Extra       |
-- +----+-------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
-- |  1 | SIMPLE      | pu    | ALL    | EmployeeID    | NULL    | NULL    | NULL                | 9    |             |
-- |  1 | SIMPLE      | e     | eq_ref | PRIMARY       | PRIMARY | 4       | lli66.pu.EmployeeID | 1    | Using where |
-- +----+-------------+-------+--------+---------------+---------+---------+---------------------+------+-------------+
--
-- Observation: 'pu' (PublicationUnits) has type=ALL, meaning a full table scan.
-- MariaDB scans all rows in PublicationUnits because there is no index on
-- Employees.Name to quickly locate matching employees before joining.

-- (3) Index Creation
CREATE INDEX idx_employees_name ON Employees(Name);

-- (4) EXPLAIN after index (full table scan replaced)
-- +----+-------------+-------+------+----------------------------+--------------------+---------+--------------------+------+--------------------------+
-- | id | select_type | table | type | possible_keys              | key                | key_len | ref                | rows | Extra                    |
-- +----+-------------+-------+------+----------------------------+--------------------+---------+--------------------+------+--------------------------+
-- |  1 | SIMPLE      | e     | ref  | PRIMARY,idx_employees_name | idx_employees_name | 102     | const              | 1    | Using where; Using index |
-- |  1 | SIMPLE      | pu    | ref  | EmployeeID                 | EmployeeID         | 4       | lli66.e.EmployeeID | 1    |                          |
-- +----+-------------+-------+------+----------------------------+--------------------+---------+--------------------+------+--------------------------+
--
-- Observation: After creating idx_employees_name, MariaDB now uses the index
-- to look up 'Frank Lee' directly in Employees (type=ref), then joins to
-- PublicationUnits via the EmployeeID FK index (type=ref). No full table scan.


-- ===========================================================
-- QUERY B: Identify distributors with mismatched billing
-- ===========================================================

-- (1) SQL Query
SELECT d.DistID, d.Name,
       COALESCE(SUM(o.Price + o.ShippingCost), 0) AS TotalBilled,
       COALESCE(SUM(p.Amount), 0)                  AS TotalPaid
FROM Distributors d
LEFT JOIN Orders o        ON d.DistID = o.DistID
LEFT JOIN DistPayments dp ON d.DistID = dp.DistID
LEFT JOIN Payments p      ON dp.PaymentID = p.PaymentID
GROUP BY d.DistID, d.Name
HAVING COALESCE(SUM(o.Price + o.ShippingCost), 0) <> COALESCE(SUM(p.Amount), 0);

-- (2) EXPLAIN before index (shows full table scan on Distributors)
-- +----+-------------+-------+--------+---------------+---------+---------+--------------------+------+---------------------------------+
-- | id | select_type | table | type   | possible_keys | key     | key_len | ref                | rows | Extra                           |
-- +----+-------------+-------+--------+---------------+---------+---------+--------------------+------+---------------------------------+
-- |  1 | SIMPLE      | d     | ALL    | NULL          | NULL    | NULL    | NULL               | 6    | Using temporary; Using filesort |
-- |  1 | SIMPLE      | o     | ref    | DistID        | DistID  | 4       | lli66.d.DistID     | 1    |                                 |
-- |  1 | SIMPLE      | dp    | ref    | DistID        | DistID  | 4       | lli66.d.DistID     | 1    | Using index                     |
-- |  1 | SIMPLE      | p     | eq_ref | PRIMARY       | PRIMARY | 4       | lli66.dp.PaymentID | 1    | Using where                     |
-- +----+-------------+-------+--------+---------------+---------+---------+--------------------+------+---------------------------------+
--
-- Observation: 'd' (Distributors) has type=ALL, meaning a full table scan.
-- Although DistID is the primary key, MariaDB cannot use it to avoid scanning
-- all rows when performing GROUP BY (DistID, Name) with no covering index.

-- (3) Index Creation
CREATE INDEX idx_distributors_name ON Distributors(Name);

-- (4) EXPLAIN after index (full table scan replaced)
-- +----+-------------+-------+--------+---------------+-----------------------+---------+--------------------+------+----------------------------------------------+
-- | id | select_type | table | type   | possible_keys | key                   | key_len | ref                | rows | Extra                                        |
-- +----+-------------+-------+--------+---------------+-----------------------+---------+--------------------+------+----------------------------------------------+
-- |  1 | SIMPLE      | d     | index  | NULL          | idx_distributors_name | 102     | NULL               | 6    | Using index; Using temporary; Using filesort |
-- |  1 | SIMPLE      | o     | ref    | DistID        | DistID                | 4       | lli66.d.DistID     | 1    |                                              |
-- |  1 | SIMPLE      | dp    | ref    | DistID        | DistID                | 4       | lli66.d.DistID     | 1    | Using index                                  |
-- |  1 | SIMPLE      | p     | eq_ref | PRIMARY       | PRIMARY               | 4       | lli66.dp.PaymentID | 1    | Using where                                  |
-- +----+-------------+-------+--------+---------------+-----------------------+---------+--------------------+------+----------------------------------------------+
--
-- Observation: After creating idx_distributors_name, MariaDB now uses the
-- index to traverse Distributors (type=index instead of ALL), meaning it
-- reads the index structure rather than doing a raw full table scan.
-- The 'Using index' note confirms the index covers the GROUP BY columns.