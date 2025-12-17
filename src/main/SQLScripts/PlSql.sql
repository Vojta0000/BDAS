-- ==============================
-- 1) Create roles
-- ==============================
INSERT INTO Role(Role_id, Role_name, Role_description)
VALUES (ROLE_SEQ.NEXTVAL, 'Teller', 'Bank teller');

INSERT INTO Role(Role_id, Role_name, Role_description)
VALUES (ROLE_SEQ.NEXTVAL, 'Client', 'Bank client');

-- ==============================
-- 2) Create 3 addresses
-- ==============================
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code)
VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', 'Prague', 'Prague', 'Branch St', 1, 11000);

INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code)
VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', 'Brno', 'Brno', 'Teller St', 10, 60200);

INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code)
VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', 'Ostrava', 'Ostrava', 'Client St', 5, 70200);

-- ==============================
-- 3) Create branch (use first address)
-- ==============================
INSERT INTO Branch(Branch_id, Branch_name, Address_id)
VALUES (BRANCH_SEQ.NEXTVAL, 'Central Branch', 1);

-- ==============================
-- 4) Create teller user (use second address)
-- ==============================
INSERT INTO "User"(User_id, Name, Surname, Password, active, approved, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Alice', 'Teller', 'pass123', 'Y', 'Y', 1, 2);

-- ==============================
-- 5) Create teller entry
-- ==============================
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id)
VALUES (1, '123456789', 'alice@bank.com', 1);

-- ==============================
-- 6) Create client user (use third address)
-- ==============================
INSERT INTO "User"(User_id, Name, Surname, Password, active, approved, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Bob', 'Client', 'pass123', 'Y', 'Y', 2, 3);

-- ==============================
-- 7) Create client entry with teller
-- ==============================
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id)
VALUES (2, '900101/1234', '987654321', 'bob@example.com', 1);

-- ==============================
-- 8) Create transaction type
-- ==============================
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description)
VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'TRANSFER', 'Standard transfer between accounts');

-- ==============================
-- 9) Create 2 accounts for client
-- ==============================
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id)
VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ1000000001', 1000, 'Y', 2);

INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id)
VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ1000000002', 500, 'Y', 2);

-- ==============================
-- 10) Create 4 transactions between the 2 accounts
-- ==============================
-- Transaction 1: from account 1 to account 2
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 100, 'Payment to savings', 'Received from main', 1, 1, SYSDATE, 2);

-- Transaction 2: from account 2 to account 1
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 50, 'Transfer back', 'Refund', 2, 1, SYSDATE + 1 / 24, 1);

-- Transaction 3: from account 1 to account 2
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 200, 'Monthly transfer', 'Received monthly', 1, 1, SYSDATE + 2 / 24, 2);

-- Transaction 4: from account 2 to account 1
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 25, 'Small transfer', 'Received small', 2, 1, SYSDATE + 3 / 24, 1);

-- 1. Bob (Client, ID: 2) sends a message to Alice (Teller, ID: 1)
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Hello Alice, I have a question about a transaction fee on my account.', 'Y', 2,
        SYSDATE - 2 / 24, 1);

-- 2. Alice replies to Bob
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Hi Bob, I can certainly help with that. Which transaction are you referring to?', 'Y', 1,
        SYSDATE - 1.5 / 24, 2);

-- 3. Bob replies back
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'It is the transfer from yesterday for 50 CZK.', 'N', 2, SYSDATE - 1 / 24, 1);

-- 4. Alice confirms receipt
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'I see it now. That fee was applied in error. I will refund it immediately.', 'N', 1,
        SYSDATE, 2);

INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Suspicious activity detected in account CZ100000001', 'N', NULL, SYSDATE, 1);

-- Add Admin Role
INSERT INTO Role (Role_id, Role_name, Role_description)
VALUES (ROLE_SEQ.NEXTVAL, 'Admin', 'System Administrator');

-- Add Admin Address
INSERT INTO Address (Address_id, Country, State, City, Street, House_number, ZIP_code)
VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague', 'Tech Street', 1, 10000);

-- Add Admin User
INSERT INTO "User" (User_id, Name, Surname, Password, active, approved, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL,
        'Super',
        'Admin',
        'admin', -- Password should be hashed in production
        'Y',
        'Y',
        (SELECT Role_id FROM Role WHERE Role_name = 'Admin'),
        (SELECT Address_id FROM Address WHERE Street = 'Tech Street' AND City = 'Prague'));

COMMIT;


CREATE OR REPLACE VIEW v_account_overview AS
SELECT a.Account_number,
       u.Name    AS owner_name,
       u.Surname AS owner_surname,
       a.Account_balance,
       a.Account_active,

       t.Transfer_amount,
       t.Transaction_time,

       CASE
           WHEN t.Account_from_id = a.Account_id THEN ct.Account_number
           WHEN t.Account_to_id = a.Account_id THEN cf.Account_number
           END   AS counterparty_account_number,

       CASE
           WHEN t.Account_from_id = a.Account_id THEN 1
           ELSE 0
           END   AS is_sender

FROM Account a
         JOIN Client c ON a.Client_id = c.User_id
         JOIN "User" u ON c.User_id = u.User_id

         LEFT JOIN Transaction t
                   ON t.Account_from_id = a.Account_id
                       OR t.Account_to_id = a.Account_id

         LEFT JOIN Account ct ON ct.Account_id = t.Account_to_id
         LEFT JOIN Account cf ON cf.Account_id = t.Account_from_id

WHERE a.Account_id = 1
ORDER BY t.Transaction_time DESC
    FETCH FIRST 5 ROWS ONLY;



CREATE OR REPLACE VIEW Transaction_detail AS
SELECT u.NAME    AS name,
       u.SURNAME AS surname,
       a.ACCOUNT_NUMBER,
       t.*,
       TRANSACTION_TYPE.TRANSACTION_TYPE_NAME

FROM TRANSACTION t
         JOIN TRANSACTION_TYPE ON t.TRANSACTION_TYPE_ID = TRANSACTION_TYPE.TRANSACTION_TYPE_ID
         LEFT JOIN ACCOUNT a ON t.ACCOUNT_TO_ID = a.ACCOUNT_ID
         LEFT JOIN "User" u ON a.CLIENT_ID = u.USER_ID
WHERE t.TRANSACTION_ID = 1;



CREATE OR REPLACE VIEW v_high_activity_accounts AS
SELECT a.Account_number,
       u.Name                 AS owner_name,
       u.Surname              AS owner_surname,
       a.Account_balance,
       a.Account_active,
       SUM(t.Transfer_amount) AS total_transferred_last_day
FROM Account a
         JOIN Client c ON a.Client_id = c.User_id
         JOIN "User" u ON c.User_id = u.User_id
         JOIN Transaction t
              ON t.Account_from_id = a.Account_id
                  OR t.Account_to_id = a.Account_id
WHERE t.Transaction_time >= SYSDATE - 1 -- last 1 day
GROUP BY a.Account_number, u.Name, u.Surname, a.Account_balance, a.Account_active
HAVING SUM(t.Transfer_amount) >= 1000000;



CREATE OR REPLACE FUNCTION get_client_age(p_client_id NUMBER)
    RETURN NUMBER
    IS
    v_birth VARCHAR2(20);
    v_date  DATE;
    v_years NUMBER;
BEGIN
    -- Get the birth number for the client
    SELECT Birth_number
    INTO v_birth
    FROM Client
    WHERE User_id = p_client_id;

-- Expect format YYMMDD or YYMMDD/XXXX, take first 6 digits
    v_date := TO_DATE(SUBSTR(v_birth, 1, 6), 'RRMMDD');

    -- Calculate age in years
    v_years := FLOOR(MONTHS_BETWEEN(TRUNC(SYSDATE), v_date) / 12);

    RETURN v_years;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL; -- client not found
END;
/

SELECT get_client_age(2);



CREATE OR REPLACE FUNCTION total_amount(p_account_id NUMBER, p_sent_flag NUMBER)
    RETURN NUMBER
    IS
    v_total NUMBER;
BEGIN
    IF p_sent_flag = 1 THEN
        -- Calculate total sent
        SELECT NVL(SUM(Transfer_amount), 0)
        INTO v_total
        FROM Transaction
        WHERE Account_from_id = p_account_id;
    ELSE
        -- Calculate total received
        SELECT NVL(SUM(Transfer_amount), 0)
        INTO v_total
        FROM Transaction
        WHERE Account_to_id = p_account_id;
    END IF;

    RETURN v_total;
END;
/

select total_amount(1, 1);



CREATE OR REPLACE FUNCTION calculate_transfer_fee(p_client_id IN NUMBER, p_amount IN NUMBER) RETURN NUMBER
    IS
    v_age           NUMBER;
    v_total_balance NUMBER;
    v_fee           NUMBER;
BEGIN
    -- 1. Check Age Discount (Youth/Students under 26 pay 0 fees)
    -- This reuses the function you already created
    v_age := get_client_age(p_client_id);

    IF v_age IS NOT NULL AND v_age < 26 THEN
        RETURN 0;
    END IF;

    -- 2. Calculate Total Balance across all active accounts for this client
    -- to determine VIP status
    SELECT SUM(Account_balance)
    INTO v_total_balance
    FROM Account
    WHERE Client_id = p_client_id
      AND Account_active = 'Y';

    v_total_balance := NVL(v_total_balance, 0);

    -- 3. Apply Fee Logic
    IF v_total_balance >= 50000 THEN
        -- VIP Client (High balance): 0.5% fee
        v_fee := p_amount * 0.005;
    ELSE
        -- Standard Client: 1.0% fee
        v_fee := p_amount * 0.01;
    END IF;

    -- 4. Ensure a minimum fee (e.g., 10 CZK) unless it was free
    IF v_fee < 10 THEN
        v_fee := 10;
    END IF;

    RETURN ROUND(v_fee, 2);
EXCEPTION
    WHEN OTHERS THEN
        -- In case of error (e.g. client doesn't exist), return NULL or handle gracefully
        RETURN NULL;
END;
/



-- ==========================================
-- FUNCTION 5: Get Clients for Teller
-- ==========================================
CREATE OR REPLACE FUNCTION get_teller_clients(
    p_teller_id IN NUMBER
) RETURN SYS_REFCURSOR IS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT u.User_id, u.Name, u.Surname, c.Email_address, c.Phone_number
        FROM Client c
                 JOIN "User" u ON c.User_id = u.User_id
        WHERE c.Teller_id = p_teller_id;

    RETURN v_cursor;
END;
/



-- ==========================================
-- PROCEDURE 1: Execute Transfer (Strict A -> B)
-- ==========================================
-- TODO: add transaction fee to the transaction screen and apply it to the total before calling this procedure
CREATE OR REPLACE PROCEDURE execute_transfer(
    p_from_acc_id IN NUMBER,
    p_to_acc_id IN NUMBER,
    p_amount IN NUMBER,
    p_msg_sender IN VARCHAR2,
    p_msg_recipient IN VARCHAR2
) IS
BEGIN
    -- Record Transaction only. Zaúčtování zůstatků provede DB (triggery) po INSERTu.
    -- Type 1 = Transfer
    INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                            Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
    VALUES (TRANSACTION_SEQ.NEXTVAL, p_amount, p_msg_sender, p_msg_recipient,
            p_from_acc_id, 1, SYSDATE, p_to_acc_id);
END;
/

-- ==========================================
-- PROCEDURE 2: Onboard New Client (Wizard)
-- ==========================================
CREATE OR REPLACE PROCEDURE onboard_new_client(
    p_teller_id IN NUMBER,
    p_name IN VARCHAR2,
    p_surname IN VARCHAR2,
    p_password IN VARCHAR2,
    p_birth_num IN VARCHAR2,
    p_phone IN VARCHAR2,
    p_email IN VARCHAR2,
    p_country IN VARCHAR2,
    p_city IN VARCHAR2,
    p_street IN VARCHAR2,
    p_house_num IN NUMBER,
    p_zip IN NUMBER
) IS
    v_addr_id NUMBER;
    v_user_id NUMBER;
    v_role_id NUMBER;
BEGIN
    SELECT Role_id
    INTO v_role_id
    FROM Role
    WHERE Role_name = 'Client';
    -- 1. Create Address
    v_addr_id := ADDRESS_SEQ.NEXTVAL;
    INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code)
    VALUES (v_addr_id, p_country, NULL, p_city, p_street, p_house_num, p_zip);

    -- 2. Create User (Role 2 = Client)
    v_user_id := USER_SEQ.NEXTVAL;
    INSERT INTO "User"(User_id, Name, Surname, Password, active, approved, Role_id, Address_id)
    VALUES (v_user_id, p_name, p_surname, p_password, 'Y', 'Y', v_role_id, v_addr_id);

    -- 3. Create Client (Linked to Teller)
    INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id)
    VALUES (v_user_id, p_birth_num, p_phone, p_email, p_teller_id);

    COMMIT;
END;
/

-- ==========================================
-- PROCEDURE 3: Apply Monthly Interest
-- ==========================================
CREATE OR REPLACE PROCEDURE apply_monthly_interest(
    p_percentage IN NUMBER
) IS
    v_interest    NUMBER;
    v_interest_id NUMBER;
BEGIN
    SELECT TRANSACTION_TYPE_ID
    INTO v_interest_id
    FROM Transaction_type
    WHERE Transaction_type_name = 'INTEREST';
    -- Loop through all active accounts
    FOR r IN (SELECT Account_id, Account_balance FROM Account WHERE Account_active = 'Y')
        LOOP

            v_interest := r.Account_balance * (p_percentage / 100);

            IF v_interest > 0 THEN
                -- Insert Transaction (Source is NULL because it comes from the Bank)
                INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
                VALUES (TRANSACTION_SEQ.NEXTVAL, v_interest, 'Interest Payment', 'Monthly Interest',
                        NULL, v_interest_id, SYSDATE, r.Account_id);
            END IF;
        END LOOP;
END;
/

-- ==========================================
-- PROCEDURE 4: Send Message to Personal Teller
-- ==========================================
CREATE OR REPLACE PROCEDURE send_message(
    p_client_user_id IN NUMBER,
    p_message_text IN VARCHAR2
) IS
    v_teller_id NUMBER;
BEGIN
    -- 1. Find the Teller assigned to this client
    SELECT Teller_id
    INTO v_teller_id
    FROM Client
    WHERE User_id = p_client_user_id;

    -- 2. Insert Message
    INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
    VALUES (MESSAGE_SEQ.NEXTVAL, p_message_text, 'N', p_client_user_id, SYSDATE, v_teller_id);

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20002, 'Client not found or no teller assigned.');
END;
/

-- ==========================================
-- PROCEDURE 5: Re-post Transaction after UPDATE
-- ==========================================
-- Účel: po změně záznamu v TRANSACTION znovu promítnout změny do zůstatků účtů.
-- Algoritmus:
--  1) Vrátí efekt původní transakce (OLD):
--       - OLD.from   +amount (vrácení peněz)
--       - OLD.to     -amount (odebrání dříve připsaných peněz)
--  2) Aplikuje efekt nové transakce (NEW):
--       - NEW.from   -amount (odečtení peněz)
--       - NEW.to     +amount (připsání)
--  Využívá se stávající trigger trg_check_account_balance, který zamezí záporným zůstatkům.
CREATE OR REPLACE PROCEDURE repost_transaction(
    p_old_from    IN NUMBER,
    p_old_to      IN NUMBER,
    p_old_amount  IN NUMBER,
    p_new_from    IN NUMBER,
    p_new_to      IN NUMBER,
    p_new_amount  IN NUMBER
) IS
    v_active_from  CHAR(1);
    v_active_to    CHAR(1);
BEGIN
    -- Validace částek: pokud je nová částka <= 0, nepovolit
    IF p_new_amount IS NOT NULL AND p_new_amount <= 0 THEN
        RAISE_APPLICATION_ERROR(-20016, 'New transfer amount must be > 0');
    END IF;

    -- Bez práce, pokud se nic relevantního nezměnilo (ochrana i pro případné manuální volání)
    IF NVL(p_old_from,-1)    = NVL(p_new_from,-1)
       AND NVL(p_old_to,-1)  = NVL(p_new_to,-1)
       AND NVL(p_old_amount,0) = NVL(p_new_amount,0) THEN
        RETURN;
    END IF;

    -- Bezpečnost: zákaz self-transferu (pokud jsou obě strany vyplněny)
    IF p_new_from IS NOT NULL AND p_new_to IS NOT NULL AND p_new_from = p_new_to THEN
        RAISE_APPLICATION_ERROR(-20017, 'Account_from_id and Account_to_id cannot be the same');
    END IF;

    -- Před-validace: nové cílové účty musí být aktivní (pokud existují)
    IF p_new_from IS NOT NULL THEN
        SELECT Account_active INTO v_active_from FROM Account WHERE Account_id = p_new_from FOR UPDATE;
        IF v_active_from <> 'Y' THEN
            RAISE_APPLICATION_ERROR(-20018, 'Source account is not active');
        END IF;
    END IF;
    IF p_new_to IS NOT NULL THEN
        SELECT Account_active INTO v_active_to FROM Account WHERE Account_id = p_new_to FOR UPDATE;
        IF v_active_to <> 'Y' THEN
            RAISE_APPLICATION_ERROR(-20019, 'Destination account is not active');
        END IF;
    END IF;

    -- 1) Reverze staré transakce
    IF p_old_from IS NOT NULL AND p_old_amount IS NOT NULL THEN
        UPDATE Account SET Account_balance = Account_balance + p_old_amount
         WHERE Account_id = p_old_from;
    END IF;
    IF p_old_to IS NOT NULL AND p_old_amount IS NOT NULL THEN
        UPDATE Account SET Account_balance = Account_balance - p_old_amount
         WHERE Account_id = p_old_to;
    END IF;

    -- 2) Aplikace nové transakce
    IF p_new_from IS NOT NULL AND p_new_amount IS NOT NULL THEN
        UPDATE Account SET Account_balance = Account_balance - p_new_amount
         WHERE Account_id = p_new_from;
    END IF;
    IF p_new_to IS NOT NULL AND p_new_amount IS NOT NULL THEN
        UPDATE Account SET Account_balance = Account_balance + p_new_amount
         WHERE Account_id = p_new_to;
    END IF;
END;
/

-- ==========================================
-- TRIGGER 5: After UPDATE on Transaction → call repost_transaction
-- ==========================================
CREATE OR REPLACE TRIGGER trg_txn_after_upd
  AFTER UPDATE ON Transaction
  FOR EACH ROW
DECLARE
BEGIN
  -- Spouštět pouze při změně klíčových polí (from/to/amount)
  IF NVL(:OLD.Account_from_id,-1)    != NVL(:NEW.Account_from_id,-1)
     OR NVL(:OLD.Account_to_id,-1)   != NVL(:NEW.Account_to_id,-1)
     OR NVL(:OLD.Transfer_amount,0)  != NVL(:NEW.Transfer_amount,0) THEN

     repost_transaction(
        :OLD.Account_from_id,
        :OLD.Account_to_id,
        :OLD.Transfer_amount,
        :NEW.Account_from_id,
        :NEW.Account_to_id,
        :NEW.Transfer_amount
     );
  END IF;
END;
/

-- ==========================================
-- TRIGGER 6: After INSERT on Transaction → apply new transaction effect
-- ==========================================
CREATE OR REPLACE TRIGGER trg_txn_after_ins
  AFTER INSERT ON Transaction
  FOR EACH ROW
DECLARE
BEGIN
  -- Po vložení nové transakce aplikuj její efekt do zůstatků
  repost_transaction(
    NULL, NULL, NULL,                -- žádné OLD hodnoty při INSERTu
    :NEW.Account_from_id,
    :NEW.Account_to_id,
    :NEW.Transfer_amount
  );
END;
/

-- ==========================================
-- TRIGGER 7: After DELETE on Transaction → reverse old transaction effect
-- ==========================================
CREATE OR REPLACE TRIGGER trg_txn_after_del
  AFTER DELETE ON Transaction
  FOR EACH ROW
DECLARE
BEGIN
  -- Při smazání transakce vrať její efekt ze zůstatků
  repost_transaction(
    :OLD.Account_from_id,
    :OLD.Account_to_id,
    :OLD.Transfer_amount,
    NULL, NULL, NULL                 -- žádné NEW hodnoty při DELETE
  );
END;
/

-- =====================================================
-- AUDIT LOG: Helper procedure (kept in main transaction)
-- Uses existing Audit_log columns: (Audit_id, Change_type, Change_time, User_id)
-- =====================================================
CREATE OR REPLACE PROCEDURE write_audit(
  p_table     IN VARCHAR2,
  p_operation IN VARCHAR2,
  p_row_pk    IN VARCHAR2,
  p_details   IN VARCHAR2
) IS
  v_msg VARCHAR2(4000);
BEGIN
  v_msg := '['|| p_table ||'] '|| p_operation || ' pk='|| p_row_pk ||
           CASE WHEN p_details IS NOT NULL THEN ' — '|| SUBSTR(p_details, 1, 3500) ELSE '' END;

  INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id)
  VALUES (AUDIT_SEQ.NEXTVAL, v_msg, SYSDATE, NULL);
END;
/

-- =====================================================
-- AUDIT TRIGGERS: For all business tables (except Audit_log)
-- Each set logs INSERT, UPDATE, DELETE with basic info
-- =====================================================

-- USER table (quoted name)
CREATE OR REPLACE TRIGGER trg_audit_user_ai
AFTER INSERT ON "User"
FOR EACH ROW
BEGIN
  write_audit('User', 'INSERT', TO_CHAR(:NEW.User_id), 'User created: '|| :NEW.Name || ' ' || :NEW.Surname);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_user_au
AFTER UPDATE ON "User"
FOR EACH ROW
BEGIN
  write_audit('User', 'UPDATE', TO_CHAR(:NEW.User_id), 'User updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_user_ad
AFTER DELETE ON "User"
FOR EACH ROW
BEGIN
  write_audit('User', 'DELETE', TO_CHAR(:OLD.User_id), 'User deleted: '|| :OLD.Name || ' ' || :OLD.Surname);
END;
/

-- ADDRESS
CREATE OR REPLACE TRIGGER trg_audit_address_ai
AFTER INSERT ON Address
FOR EACH ROW
BEGIN
  write_audit('Address', 'INSERT', TO_CHAR(:NEW.Address_id), 'Address created: '|| :NEW.Country || ', '|| :NEW.City || ', '|| :NEW.Street);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_address_au
AFTER UPDATE ON Address
FOR EACH ROW
BEGIN
  write_audit('Address', 'UPDATE', TO_CHAR(:NEW.Address_id), 'Address updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_address_ad
AFTER DELETE ON Address
FOR EACH ROW
BEGIN
  write_audit('Address', 'DELETE', TO_CHAR(:OLD.Address_id), 'Address deleted');
END;
/

-- ROLE
CREATE OR REPLACE TRIGGER trg_audit_role_ai
AFTER INSERT ON Role
FOR EACH ROW
BEGIN
  write_audit('Role', 'INSERT', TO_CHAR(:NEW.Role_id), 'Role created: '|| :NEW.Role_name);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_role_au
AFTER UPDATE ON Role
FOR EACH ROW
BEGIN
  write_audit('Role', 'UPDATE', TO_CHAR(:NEW.Role_id), 'Role updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_role_ad
AFTER DELETE ON Role
FOR EACH ROW
BEGIN
  write_audit('Role', 'DELETE', TO_CHAR(:OLD.Role_id), 'Role deleted: '|| :OLD.Role_name);
END;
/

-- BRANCH
CREATE OR REPLACE TRIGGER trg_audit_branch_ai
AFTER INSERT ON Branch
FOR EACH ROW
BEGIN
  write_audit('Branch', 'INSERT', TO_CHAR(:NEW.Branch_id), 'Branch created: '|| :NEW.Branch_name);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_branch_au
AFTER UPDATE ON Branch
FOR EACH ROW
BEGIN
  write_audit('Branch', 'UPDATE', TO_CHAR(:NEW.Branch_id), 'Branch updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_branch_ad
AFTER DELETE ON Branch
FOR EACH ROW
BEGIN
  write_audit('Branch', 'DELETE', TO_CHAR(:OLD.Branch_id), 'Branch deleted: '|| :OLD.Branch_name);
END;
/

-- TELLER
CREATE OR REPLACE TRIGGER trg_audit_teller_ai
AFTER INSERT ON Teller
FOR EACH ROW
BEGIN
  write_audit('Teller', 'INSERT', TO_CHAR(:NEW.User_id), 'Teller created');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_teller_au
AFTER UPDATE ON Teller
FOR EACH ROW
BEGIN
  write_audit('Teller', 'UPDATE', TO_CHAR(:NEW.User_id), 'Teller updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_teller_ad
AFTER DELETE ON Teller
FOR EACH ROW
BEGIN
  write_audit('Teller', 'DELETE', TO_CHAR(:OLD.User_id), 'Teller deleted');
END;
/

-- CLIENT
CREATE OR REPLACE TRIGGER trg_audit_client_ai
AFTER INSERT ON Client
FOR EACH ROW
BEGIN
  write_audit('Client', 'INSERT', TO_CHAR(:NEW.User_id), 'Client created');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_client_au
AFTER UPDATE ON Client
FOR EACH ROW
BEGIN
  write_audit('Client', 'UPDATE', TO_CHAR(:NEW.User_id), 'Client updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_client_ad
AFTER DELETE ON Client
FOR EACH ROW
BEGIN
  write_audit('Client', 'DELETE', TO_CHAR(:OLD.User_id), 'Client deleted');
END;
/

-- ACCOUNT
CREATE OR REPLACE TRIGGER trg_audit_account_ai
AFTER INSERT ON Account
FOR EACH ROW
BEGIN
  write_audit('Account', 'INSERT', TO_CHAR(:NEW.Account_id), 'Account created: '|| :NEW.Account_number);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_account_au
AFTER UPDATE ON Account
FOR EACH ROW
BEGIN
  write_audit('Account', 'UPDATE', TO_CHAR(:NEW.Account_id), 'Account updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_account_ad
AFTER DELETE ON Account
FOR EACH ROW
BEGIN
  write_audit('Account', 'DELETE', TO_CHAR(:OLD.Account_id), 'Account deleted: '|| :OLD.Account_number);
END;
/

-- TRANSACTION_TYPE
CREATE OR REPLACE TRIGGER trg_audit_trtype_ai
AFTER INSERT ON Transaction_type
FOR EACH ROW
BEGIN
  write_audit('Transaction_type', 'INSERT', TO_CHAR(:NEW.Transaction_type_id), 'Transaction type created: '|| :NEW.Transaction_type_name);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_trtype_au
AFTER UPDATE ON Transaction_type
FOR EACH ROW
BEGIN
  write_audit('Transaction_type', 'UPDATE', TO_CHAR(:NEW.Transaction_type_id), 'Transaction type updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_trtype_ad
AFTER DELETE ON Transaction_type
FOR EACH ROW
BEGIN
  write_audit('Transaction_type', 'DELETE', TO_CHAR(:OLD.Transaction_type_id), 'Transaction type deleted');
END;
/

-- TRANSACTION
CREATE OR REPLACE TRIGGER trg_audit_transaction_ai
AFTER INSERT ON Transaction
FOR EACH ROW
BEGIN
  write_audit('Transaction', 'INSERT', TO_CHAR(:NEW.Transaction_id), 'Transaction created amount='|| :NEW.Transfer_amount);
END;
/

CREATE OR REPLACE TRIGGER trg_audit_transaction_au
AFTER UPDATE ON Transaction
FOR EACH ROW
BEGIN
  write_audit('Transaction', 'UPDATE', TO_CHAR(:NEW.Transaction_id), 'Transaction updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_transaction_ad
AFTER DELETE ON Transaction
FOR EACH ROW
BEGIN
  write_audit('Transaction', 'DELETE', TO_CHAR(:OLD.Transaction_id), 'Transaction deleted amount='|| :OLD.Transfer_amount);
END;
/

-- MESSAGE
CREATE OR REPLACE TRIGGER trg_audit_message_ai
AFTER INSERT ON Message
FOR EACH ROW
BEGIN
  write_audit('Message', 'INSERT', TO_CHAR(:NEW.Message_id), 'Message created');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_message_au
AFTER UPDATE ON Message
FOR EACH ROW
BEGIN
  write_audit('Message', 'UPDATE', TO_CHAR(:NEW.Message_id), 'Message updated');
END;
/

CREATE OR REPLACE TRIGGER trg_audit_message_ad
AFTER DELETE ON Message
FOR EACH ROW
BEGIN
  write_audit('Message', 'DELETE', TO_CHAR(:OLD.Message_id), 'Message deleted');
END;
/

-- ==========================================
-- TRIGGER 1: Audit User Profile Changes
-- ==========================================
CREATE OR REPLACE TRIGGER trg_audit_user_changes
    AFTER UPDATE
    ON "User"
    FOR EACH ROW
DECLARE
    v_change_details VARCHAR2(100);
BEGIN
    -- Detect what changed
    IF :OLD.Name != :NEW.Name THEN
        v_change_details := 'Name changed from ' || :OLD.Name || ' to ' || :NEW.Name;
    ELSIF :OLD.Surname != :NEW.Surname THEN
        v_change_details := 'Surname changed from ' || :OLD.Surname || ' to ' || :NEW.Surname;
    ELSIF :OLD.Password != :NEW.Password THEN
        v_change_details := 'Password changed';
    ELSE
        v_change_details := 'Other User details updated';
    END IF;

    -- Insert into Audit Log
    INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id)
    VALUES (AUDIT_SEQ.NEXTVAL, v_change_details, SYSDATE, :NEW.User_id);
END;
/

-- ==========================================
-- TRIGGER 2: Prevent Negative Balance
-- ==========================================
CREATE OR REPLACE TRIGGER trg_check_account_balance
    BEFORE UPDATE
    ON Account
    FOR EACH ROW
BEGIN
    -- If the new balance is negative, block the update
    IF :NEW.Account_balance < 0 THEN
        RAISE_APPLICATION_ERROR(-20003,
                                'Transaction rejected: Insufficient funds. Account balance cannot be negative.');
    END IF;
END;
/

-- ==========================================
-- TRIGGER 3: Complex - Large Transfer Alert System
-- ==========================================
CREATE OR REPLACE TRIGGER trg_large_transfer_alert
    AFTER INSERT
    ON Transaction
    FOR EACH ROW
DECLARE
    v_teller_id NUMBER;
    v_client_id NUMBER;
    v_acc_num   VARCHAR2(30);
BEGIN
    -- Only check if it's a real client sending money (not interest/bank deposit)
    -- And only if amount is significant (> 10,000)
    IF :NEW.Account_from_id IS NOT NULL AND :NEW.Transfer_amount > 10000 THEN

        -- Complex Join: Traverse from Account -> Client to find the responsible Teller
        SELECT c.Teller_id, c.User_id, a.Account_number
        INTO v_teller_id, v_client_id, v_acc_num
        FROM Account a
                 JOIN Client c ON a.Client_id = c.User_id
        WHERE a.Account_id = :NEW.Account_from_id;

        -- Auto-generate a message to the Teller
        INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
        VALUES (MESSAGE_SEQ.NEXTVAL,
                'ALERT: Large Transaction of ' || :NEW.Transfer_amount || ' CZK detected on Account ' || v_acc_num,
                'N', -- Unread
                null, -- Sent "by" the client (system acting on behalf)
                SYSDATE,
                v_teller_id -- Sent "to" their specific teller
               );
    END IF;
EXCEPTION
    -- Handle cases where account/client might not exist (rare, but good practice)
    WHEN NO_DATA_FOUND THEN
        NULL;
END;
/

-- ==========================================
-- TRIGGER 4: Complex - Account Lifecycle Manager
-- ==========================================
CREATE OR REPLACE TRIGGER trg_manage_account_status
    BEFORE UPDATE
    ON Account
    FOR EACH ROW
BEGIN
    -- LOGIC 1: Auto-Deactivate
    -- If balance drops to 0 (and was previously positive), auto-set Active to 'N'
    IF :NEW.Account_balance = 0 AND :OLD.Account_balance > 0 THEN
        :NEW.Account_active := 'N';
    END IF;

    -- LOGIC 2: Safety Lock
    -- If someone TRIES to set Active to 'N', but there is still money, BLOCK IT.
    IF :NEW.Account_active = 'N' AND :NEW.Account_balance > 0 THEN
        RAISE_APPLICATION_ERROR(-20004,
                                'Business Rule Violation: Cannot deactivate an account that still holds funds (' ||
                                :NEW.Account_balance || '). Withdraw funds first.');
    END IF;

    -- LOGIC 3: Auto-Reactivate
    -- If money enters an inactive account, wake it up
    IF :NEW.Account_balance > 0 AND :OLD.Account_active = 'N' THEN
        :NEW.Account_active := 'Y';
    END IF;
END;
/

-- (Removed) Trigger trg_txn_before_ins per request – transaction posting will be added later manually by admin.

-- (Removed) Duplicate trg_txn_before_ins (will be provided separately for manual inclusion later).

-- (Removed) Trigger trg_txn_after_del – delete reversal will be handled later if needed.
