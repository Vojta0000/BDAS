-- ==========================================
-- Database Views for UI controllers
-- Purpose: Replace inline/naked SQL joins in Java controllers with stable DB views
-- Notes:
--  - No ORDER BY inside views; ordering is handled in controllers
--  - Column aliases are chosen to match existing controller expectations
-- ==========================================

-- Roles
CREATE OR REPLACE VIEW v_role_list AS
SELECT r.ROLE_ID, r.ROLE_NAME, r.ROLE_DESCRIPTION
FROM ROLE r;

-- Addresses
CREATE OR REPLACE VIEW v_address_list AS
SELECT a.ADDRESS_ID, a.COUNTRY, a.STATE, a.CITY, a.STREET, a.HOUSE_NUMBER, a.ZIP_CODE
FROM ADDRESS a;

-- Transaction types
CREATE OR REPLACE VIEW v_transaction_type_list AS
SELECT tt.TRANSACTION_TYPE_ID, tt.TRANSACTION_TYPE_NAME, tt.TRANSACTION_TYPE_DESCRIPTION
FROM TRANSACTION_TYPE tt;

-- Logins + User
CREATE OR REPLACE VIEW v_login_overview AS
SELECT l.LOGIN_ID,
       l.LOGIN_IP_ADDRESS,
       l.LOGIN_TIME,
       u.NAME AS USER_NAME,
       u.SURNAME AS USER_SURNAME,
       u.USER_ID
FROM LOGIN_RECORD l
JOIN "User" u ON u.USER_ID = l.USER_ID;

-- Audit + User
CREATE OR REPLACE VIEW v_audit_overview AS
SELECT a.AUDIT_ID,
       a.CHANGE_TYPE,
       a.CHANGE_TIME,
       u.NAME AS USER_NAME,
       u.SURNAME AS USER_SURNAME,
       u.USER_ID
FROM AUDIT_LOG a
JOIN "User" u ON u.USER_ID = a.USER_ID;

-- Documents + User
CREATE OR REPLACE VIEW v_document_overview AS
SELECT d.DOCUMENT_ID,
       d.FILE_NAME,
       d.FILE_EXTENSION,
       u.NAME AS USER_NAME,
       u.SURNAME AS USER_SURNAME,
       u.USER_ID
FROM DOCUMENT d
JOIN "User" u ON u.USER_ID = d.USER_ID;

-- Simple document lists
CREATE OR REPLACE VIEW v_document_list_by_user AS
SELECT d.DOCUMENT_ID, d.FILE_NAME, d.USER_ID
FROM DOCUMENT d;

CREATE OR REPLACE VIEW v_document_data AS
SELECT d.DOCUMENT_ID, d.FILE_DATA
FROM DOCUMENT d;

-- Branch overview: with address and parent branch name
CREATE OR REPLACE VIEW v_branch_overview AS
SELECT b.BRANCH_ID,
       b.BRANCH_NAME,
       a.COUNTRY,
       a.STATE,
       a.CITY,
       a.STREET,
       a.HOUSE_NUMBER,
       a.ZIP_CODE,
       pb.BRANCH_NAME AS PARENT_NAME
FROM BRANCH b
JOIN ADDRESS a ON a.ADDRESS_ID = b.ADDRESS_ID
LEFT JOIN BRANCH pb ON pb.BRANCH_ID = b.PARENT_BRANCH_ID;

-- Branch simple list
CREATE OR REPLACE VIEW v_branch_list AS
SELECT b.BRANCH_ID, b.BRANCH_NAME
FROM BRANCH b;

-- Branch tree: pairs of (root_branch_id, branch_id) for entire hierarchy
CREATE OR REPLACE VIEW v_branch_tree AS
SELECT DISTINCT CONNECT_BY_ROOT BRANCH_ID AS ROOT_BRANCH_ID,
       BRANCH_ID
FROM BRANCH
CONNECT BY PRIOR BRANCH_ID = PARENT_BRANCH_ID;

-- Teller's clients and their accounts (for teller overview table)
CREATE OR REPLACE VIEW v_teller_clients_accounts AS
SELECT c.TELLER_ID,
       a.ACCOUNT_ID,
       a.ACCOUNT_NUMBER,
       u.NAME,
       u.SURNAME
FROM CLIENT c
JOIN "User" u ON u.USER_ID = c.USER_ID
JOIN ACCOUNT a ON a.CLIENT_ID = c.USER_ID
WHERE u.APPROVED = 'Y';

-- User overview: with role and address
CREATE OR REPLACE VIEW v_user_overview AS
SELECT u.USER_ID,
       u.NAME,
       u.SURNAME,
       u.APPROVED,
       r.ROLE_NAME,
       a.COUNTRY,
       a.STATE,
       a.CITY,
       a.STREET,
       a.HOUSE_NUMBER,
       a.ZIP_CODE,
       u.ADDRESS_ID
FROM "User" u
JOIN ROLE r ON r.ROLE_ID = u.ROLE_ID
JOIN ADDRESS a ON a.ADDRESS_ID = u.ADDRESS_ID;

-- Simple user lists
CREATE OR REPLACE VIEW v_user_list AS
SELECT u.USER_ID, u.NAME, u.SURNAME
FROM "User" u;

CREATE OR REPLACE VIEW v_client_user_list AS
SELECT u.USER_ID, u.NAME, u.SURNAME
FROM CLIENT c
JOIN "User" u ON u.USER_ID = c.USER_ID;

CREATE OR REPLACE VIEW v_teller_user_list AS
SELECT u.USER_ID, u.NAME, u.SURNAME
FROM TELLER t
JOIN "User" u ON u.USER_ID = t.USER_ID;

-- Teller overview: with branch and address
CREATE OR REPLACE VIEW v_teller_overview AS
SELECT t.USER_ID,
       u.NAME,
       u.SURNAME,
       t.WORK_PHONE_NUMBER,
       t.WORK_EMAIL_ADDRESS,
       b.BRANCH_ID,
       b.BRANCH_NAME,
       a.COUNTRY,
       a.STATE,
       a.CITY,
       a.STREET,
       a.HOUSE_NUMBER,
       a.ZIP_CODE
FROM TELLER t
JOIN "User" u ON u.USER_ID = t.USER_ID
JOIN BRANCH b ON b.BRANCH_ID = t.BRANCH_ID
JOIN ADDRESS a ON a.ADDRESS_ID = u.ADDRESS_ID;

-- Client overview: with teller and address
CREATE OR REPLACE VIEW v_client_overview AS
SELECT c.USER_ID,
       u.NAME,
       u.SURNAME,
       c.BIRTH_NUMBER,
       c.PHONE_NUMBER,
       c.EMAIL_ADDRESS,
       c.TELLER_ID,
       ut.NAME AS TELLER_NAME,
       ut.SURNAME AS TELLER_SURNAME,
       tt.WORK_PHONE_NUMBER AS TELLER_WORK_PHONE,
        tt.WORK_EMAIL_ADDRESS AS TELLER_WORK_EMAIL,
        bb.BRANCH_NAME AS TELLER_BRANCH_NAME,
       a.COUNTRY,
       a.STATE,
       a.CITY,
       a.STREET,
       a.HOUSE_NUMBER,
       a.ZIP_CODE
FROM CLIENT c
JOIN "User" u ON u.USER_ID = c.USER_ID
JOIN ADDRESS a ON a.ADDRESS_ID = u.ADDRESS_ID
LEFT JOIN "User" ut ON ut.USER_ID = c.TELLER_ID
LEFT JOIN TELLER tt ON tt.USER_ID = c.TELLER_ID
LEFT JOIN BRANCH bb ON bb.BRANCH_ID = tt.BRANCH_ID;

-- Accounts overview: with owner full name
CREATE OR REPLACE VIEW v_account_overview AS
SELECT a.ACCOUNT_ID,
       a.ACCOUNT_NUMBER,
       a.ACCOUNT_BALANCE,
       u.NAME AS OWNER_NAME,
       u.SURNAME AS OWNER_SURNAME,
       c.USER_ID AS CLIENT_USER_ID
FROM ACCOUNT a
JOIN CLIENT c ON c.USER_ID = a.CLIENT_ID
JOIN "User" u ON u.USER_ID = c.USER_ID;

-- Simple account list
CREATE OR REPLACE VIEW v_account_list AS
SELECT a.ACCOUNT_ID, a.ACCOUNT_NUMBER, a.CLIENT_ID, a.ACCOUNT_BALANCE
FROM ACCOUNT a;

-- Transactions overview: with type and account numbers
CREATE OR REPLACE VIEW v_transaction_overview AS
SELECT t.TRANSACTION_ID,
       t.TRANSFER_AMOUNT,
       t.TRANSACTION_TIME,
       tt.TRANSACTION_TYPE_NAME,
       t.ACCOUNT_FROM_ID,
        t.ACCOUNT_TO_ID,
       af.ACCOUNT_NUMBER AS ACCOUNT_FROM_NUMBER,
       at.ACCOUNT_NUMBER AS ACCOUNT_TO_NUMBER
FROM TRANSACTION t
JOIN TRANSACTION_TYPE tt ON tt.TRANSACTION_TYPE_ID = t.TRANSACTION_TYPE_ID
LEFT JOIN ACCOUNT af ON af.ACCOUNT_ID = t.ACCOUNT_FROM_ID
LEFT JOIN ACCOUNT at ON at.ACCOUNT_ID = t.ACCOUNT_TO_ID;

-- Messages overview: with from/to user names
CREATE OR REPLACE VIEW v_message_overview AS
SELECT m.MESSAGE_ID,
       uf.NAME AS FROM_NAME,
       uf.SURNAME AS FROM_SURNAME,
       ut.NAME AS TO_NAME,
       ut.SURNAME AS TO_SURNAME,
       m.MESSAGE_TEXT,
       m.MESSAGE_SENT_AT,
       m.MESSAGE_READ,
       m.USER_FROM_ID,
       m.USER_TO_ID
FROM MESSAGE m
JOIN "User" uf ON uf.USER_ID = m.USER_FROM_ID
JOIN "User" ut ON ut.USER_ID = m.USER_TO_ID;

-- User with role for login (includes all columns from User plus ROLE_NAME)
CREATE OR REPLACE VIEW v_user_with_role AS
SELECT u.USER_ID, u.NAME, u.SURNAME, u.PASSWORD, u.ACTIVE, u.APPROVED, u.ROLE_ID, u.ADDRESS_ID,
       r.ROLE_NAME
FROM "User" u
JOIN ROLE r ON r.ROLE_ID = u.ROLE_ID;

COMMIT;
