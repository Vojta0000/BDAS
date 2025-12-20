-- =============================================================
-- deleteScript.sql
-- Purpose: Delete all data from all tables without destroying them.
-- Order: Deletes child rows before parent rows to avoid FK violations.
-- =============================================================

-- 1) Tables with many dependencies
DELETE FROM Transaction;
DELETE FROM Account;
DELETE FROM Audit_log;
DELETE FROM Document;
DELETE FROM Login_record;
DELETE FROM Message;
DELETE FROM Client;
DELETE FROM Teller;

-- 2) Intermediate tables
DELETE FROM "User";
DELETE FROM Branch;

-- 3) Reference data (Combo-box tables)
DELETE FROM Role;
DELETE FROM Address;
DELETE FROM Transaction_type;

COMMIT;

-- =============================================================
-- End of deleteScript.sql
-- =============================================================
