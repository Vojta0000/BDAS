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
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description)
VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'DEPOSIT', 'Standard deposit into an account');
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description)
VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'WITHDRAW', 'Standard withdraw from an account');

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