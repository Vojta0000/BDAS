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
INSERT INTO "User"(User_id, Name, Surname, Password, active, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Alice', 'Teller', 'pass123', 'Y', 1, 2);

-- ==============================
-- 5) Create teller entry
-- ==============================
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id)
VALUES (1, '123456789', 'alice@bank.com', 1);

-- ==============================
-- 6) Create client user (use third address)
-- ==============================
INSERT INTO "User"(User_id, Name, Surname, Password, active, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Bob', 'Client', 'pass123', 'Y', 2, 3);

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
VALUES (TRANSACTION_SEQ.NEXTVAL, 50, 'Transfer back', 'Refund', 2, 1, SYSDATE+1/24, 1);

-- Transaction 3: from account 1 to account 2
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 200, 'Monthly transfer', 'Received monthly', 1, 1, SYSDATE+2/24, 2);

-- Transaction 4: from account 2 to account 1
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 25, 'Small transfer', 'Received small', 2, 1, SYSDATE+3/24, 1);

-- 1. Bob (Client, ID: 2) sends a message to Alice (Teller, ID: 1)
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Hello Alice, I have a question about a transaction fee on my account.', 'Y', 2, SYSDATE - 2/24, 1);

-- 2. Alice replies to Bob
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Hi Bob, I can certainly help with that. Which transaction are you referring to?', 'Y', 1, SYSDATE - 1.5/24, 2);

-- 3. Bob replies back
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'It is the transfer from yesterday for 50 CZK.', 'N', 2, SYSDATE - 1/24, 1);

-- 4. Alice confirms receipt
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'I see it now. That fee was applied in error. I will refund it immediately.', 'N', 1, SYSDATE, 2);

INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id)
VALUES (MESSAGE_SEQ.NEXTVAL, 'Suspicious activity detected in account CZ100000001', 'N', NULL, SYSDATE, 1);

-- Add Admin Role
INSERT INTO Role (Role_id, Role_name, Role_description)
VALUES (ROLE_SEQ.NEXTVAL, 'Admin', 'System Administrator');

-- Add Admin Address
INSERT INTO Address (Address_id, Country, State, City, Street, House_number, ZIP_code)
VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague', 'Tech Street', 1, 10000);

-- Add Admin User
INSERT INTO "User" (User_id, Name, Surname, Password, active, Role_id, Address_id)
VALUES (
    USER_SEQ.NEXTVAL,
    'Super',
    'Admin',
    'admin', -- Password should be hashed in production
    'Y',
    (SELECT Role_id FROM Role WHERE Role_name = 'Admin'),
    (SELECT Address_id FROM Address WHERE Street = 'Tech Street' AND City = 'Prague')
);

COMMIT;


CREATE OR REPLACE VIEW v_account_overview AS
SELECT
    a.Account_number,
    u.Name AS owner_name,
    u.Surname AS owner_surname,
    a.Account_balance,
    a.Account_active,

    t.Transfer_amount,
    t.Transaction_time,

    CASE
        WHEN t.Account_from_id = a.Account_id THEN ct.Account_number
        WHEN t.Account_to_id   = a.Account_id THEN cf.Account_number
        END AS counterparty_account_number,

    CASE
        WHEN t.Account_from_id = a.Account_id THEN 1
        ELSE 0
        END AS is_sender

FROM Account a
         JOIN Client c ON a.Client_id = c.User_id
         JOIN "User" u ON c.User_id = u.User_id

         LEFT JOIN Transaction t
                   ON t.Account_from_id = a.Account_id
                       OR t.Account_to_id   = a.Account_id

         LEFT JOIN Account ct ON ct.Account_id = t.Account_to_id
         LEFT JOIN Account cf ON cf.Account_id = t.Account_from_id

WHERE a.Account_id = 1
ORDER BY t.Transaction_time DESC
    FETCH FIRST 5 ROWS ONLY;





CREATE OR REPLACE VIEW Transaction_detail AS
SELECT  u.NAME AS name,
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
SELECT
    a.Account_number,
    u.Name AS owner_name,
    u.Surname AS owner_surname,
    a.Account_balance,
    a.Account_active,
    SUM(t.Transfer_amount) AS total_transferred_last_day
FROM Account a
         JOIN Client c ON a.Client_id = c.User_id
         JOIN "User" u ON c.User_id = u.User_id
         JOIN Transaction t
              ON t.Account_from_id = a.Account_id
                  OR t.Account_to_id = a.Account_id
WHERE t.Transaction_time >= SYSDATE - 1  -- last 1 day
GROUP BY a.Account_number, u.Name, u.Surname, a.Account_balance, a.Account_active
HAVING SUM(t.Transfer_amount) >= 1000000;




CREATE OR REPLACE FUNCTION get_client_age(p_client_id NUMBER)
    RETURN NUMBER
    IS
    v_birth   VARCHAR2(20);
    v_date    DATE;
    v_years   NUMBER;
BEGIN
    -- Get the birth number for the client
SELECT Birth_number
INTO v_birth
FROM Client
WHERE User_id = p_client_id;

-- Expect format YYMMDD or YYMMDD/XXXX, take first 6 digits
v_date := TO_DATE(SUBSTR(v_birth,1,6), 'RRMMDD');

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
    p_to_acc_id   IN NUMBER,
    p_amount      IN NUMBER,
    p_msg_sender  IN VARCHAR2,
    p_msg_recipient IN VARCHAR2
) IS
    v_balance NUMBER;
BEGIN
    -- 1. Check if sender has enough money
    SELECT Account_balance INTO v_balance
    FROM Account
    WHERE Account_id = p_from_acc_id;

    IF v_balance < p_amount THEN
        RAISE_APPLICATION_ERROR(-20001, 'Insufficient funds for transfer.');
    END IF;

    -- 2. Deduct from Sender
    UPDATE Account
    SET Account_balance = Account_balance - p_amount
    WHERE Account_id = p_from_acc_id;

    -- 3. Add to Receiver
    UPDATE Account
    SET Account_balance = Account_balance + p_amount
    WHERE Account_id = p_to_acc_id;

    -- 4. Record Transaction (Type 1 = Transfer)
    INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                            Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
    VALUES (TRANSACTION_SEQ.NEXTVAL, p_amount, p_msg_sender, p_msg_recipient,
            p_from_acc_id, 1, SYSDATE, p_to_acc_id);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- ==========================================
-- PROCEDURE 2: Onboard New Client (Wizard)
-- ==========================================
CREATE OR REPLACE PROCEDURE onboard_new_client(
    p_teller_id   IN NUMBER,
    p_name        IN VARCHAR2,
    p_surname     IN VARCHAR2,
    p_password    IN VARCHAR2,
    p_birth_num   IN VARCHAR2,
    p_phone       IN VARCHAR2,
    p_email       IN VARCHAR2,
    p_country     IN VARCHAR2,
    p_city        IN VARCHAR2,
    p_street      IN VARCHAR2,
    p_house_num   IN NUMBER,
    p_zip         IN NUMBER
) IS
    v_addr_id NUMBER;
    v_user_id NUMBER;
    v_role_id NUMBER;
BEGIN
    SELECT Role_id INTO v_role_id
    FROM Role
    WHERE Role_name = 'Client';
    -- 1. Create Address
    v_addr_id := ADDRESS_SEQ.NEXTVAL;
    INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code)
    VALUES (v_addr_id, p_country, NULL, p_city, p_street, p_house_num, p_zip);

    -- 2. Create User (Role 2 = Client)
    v_user_id := USER_SEQ.NEXTVAL;
    INSERT INTO "User"(User_id, Name, Surname, Password, active, Role_id, Address_id)
    VALUES (v_user_id, p_name, p_surname, p_password, 'Y', v_role_id, v_addr_id);

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
    v_interest NUMBER;
    v_interest_id NUMBER;
BEGIN
    SELECT TRANSACTION_TYPE_ID INTO v_interest_id
    FROM Transaction_type
    WHERE Transaction_type_name = 'INTEREST';
    -- Loop through all active accounts
    FOR r IN (SELECT Account_id, Account_balance FROM Account WHERE Account_active = 'Y') LOOP

            v_interest := r.Account_balance * (p_percentage / 100);

            IF v_interest > 0 THEN
                -- Update Balance
                UPDATE Account
                SET Account_balance = Account_balance + v_interest
                WHERE Account_id = r.Account_id;

                -- Insert Transaction (Source is NULL because it comes from the Bank)
                INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient,
                                        Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
                VALUES (TRANSACTION_SEQ.NEXTVAL, v_interest, 'Interest Payment', 'Monthly Interest',
                        NULL, v_interest_id, SYSDATE, r.Account_id);
            END IF;
        END LOOP;
    COMMIT;
END;
/

-- ==========================================
-- PROCEDURE 4: Send Message to Personal Teller
-- ==========================================
CREATE OR REPLACE PROCEDURE send_message(
    p_client_user_id IN NUMBER,
    p_message_text   IN VARCHAR2
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
-- TRIGGER 1: Audit User Profile Changes
-- ==========================================
CREATE OR REPLACE TRIGGER trg_audit_user_changes
    AFTER UPDATE ON "User"
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
    BEFORE UPDATE ON Account
    FOR EACH ROW
BEGIN
    -- If the new balance is negative, block the update
    IF :NEW.Account_balance < 0 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Transaction rejected: Insufficient funds. Account balance cannot be negative.');
    END IF;
END;
/

-- ==========================================
-- TRIGGER 3: Complex - Large Transfer Alert System
-- ==========================================
CREATE OR REPLACE TRIGGER trg_large_transfer_alert
    AFTER INSERT ON Transaction
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
        VALUES (
                   MESSAGE_SEQ.NEXTVAL,
                   'ALERT: Large Transaction of ' || :NEW.Transfer_amount || ' CZK detected on Account ' || v_acc_num,
                   'N',         -- Unread
                   v_client_id, -- Sent "by" the client (system acting on behalf)
                   SYSDATE,
                   v_teller_id  -- Sent "to" their specific teller
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
    BEFORE UPDATE ON Account
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
        RAISE_APPLICATION_ERROR(-20004, 'Business Rule Violation: Cannot deactivate an account that still holds funds (' || :NEW.Account_balance || '). Withdraw funds first.');
    END IF;

    -- LOGIC 3: Auto-Reactivate
    -- If money enters an inactive account, wake it up
    IF :NEW.Account_balance > 0 AND :OLD.Account_active = 'N' THEN
        :NEW.Account_active := 'Y';
    END IF;
END;
/