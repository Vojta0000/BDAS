-- =============================================================
-- RealInsertScript.sql (Plain INSERT version)
-- Purpose: Populate the BankIS schema with realistic sample data
-- IMPORTANT: This script uses ONLY plain INSERT statements.
--  - No PL/SQL blocks, no loops, no collection types, no functions.
--  - Assumes an empty/fresh schema (run destroyScript.sql before rerun to avoid duplicates).
--  - Uses sequences from CreateScript.sql via NEXTVAL.
--  - Foreign keys resolved via simple scalar subqueries on natural keys (names/emails/account numbers).
-- Run order (recommended):
--   1) CreateScript.sql
--   2) RealInsertScript.sql (this file)
--   3) PlSql.sql (optional, contains stored routines unrelated to inserts here)
--   4) Views.sql (optional)
-- =============================================================

-- ======================
-- 1) Reference data (combo-box tables)
-- ======================
INSERT INTO Role(Role_id, Role_name, Role_description) VALUES (ROLE_SEQ.NEXTVAL, 'Admin',  'System administrator');
INSERT INTO Role(Role_id, Role_name, Role_description) VALUES (ROLE_SEQ.NEXTVAL, 'Teller', 'Bank teller');
INSERT INTO Role(Role_id, Role_name, Role_description) VALUES (ROLE_SEQ.NEXTVAL, 'Client', 'Bank client');

INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'TRANSFER',  'Account-to-account transfer');
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'DEPOSIT',   'Cash deposit at branch');
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'WITHDRAWAL','Cash withdrawal at branch');
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'FEE',       'Service fee');
INSERT INTO Transaction_type(Transaction_type_id, Transaction_type_name, Transaction_type_description) VALUES (TRANSACTION_TYPE_SEQ.NEXTVAL, 'INTEREST',  'Monthly interest credit');

-- ======================
-- 2) Addresses (~28 rows)
-- ======================
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Na Prikope', 14, 11000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',    'Ceska',     22, 60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Vodičkova', 28, 11000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Jungmannova', 10, 11000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Sokolovska',  85, 18000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Evropska',    95, 16000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',    'Masarykova', 17, 60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',    'Joštova',    12, 60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Ostrava', 'Nádražní',   44, 70200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Plzeň',   'Americka',   50, 30100);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Liberec', 'Pražská',    21, 46001);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Olomouc', 'Dolní nám.',  7, 77900);
-- additional reusable client/teller addresses
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Karmelitska', 18, 11800);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',  'Italska',     3,  12000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',    'Lidicka',     9,  60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',    'Kounicova',   6,  60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Ostrava', '28. října',  90,  70200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Plzeň',   'Tylova',     15,  30100);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Liberec', 'Masarykova',  2,  46001);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Olomouc', 'Kateřinská', 11,  77900);
-- 10 more simple addresses
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Germany', NULL, 'Berlin',   'Friedrichstrasse', 100, 11011);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Austria', NULL, 'Vienna',   'Mariahilfer Str.', 22, 1070);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Poland',  NULL, 'Krakow',   'Florianska',      5,  31000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',   'Narodni',        33, 11000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Prague',   'Spalena',        12, 11000);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Brno',     'Zelny trh',      1,  60200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Ostrava',  'Stodolni',      10, 70200);
INSERT INTO Address(Address_id, Country, State, City, Street, House_number, ZIP_code) VALUES (ADDRESS_SEQ.NEXTVAL, 'Czechia', NULL, 'Plzeň',    'Smetanova',      8,  30100);

-- ======================
-- 3) Branches (10 rows, with hierarchy)
-- ======================
-- Parents will be linked by name using scalar subqueries
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,
  'Headquarters',
  (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Na Prikope' AND House_number=14 AND ZIP_code=11000),
  NULL
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Central Prague',
  (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Vodičkova' AND House_number=28 AND ZIP_code=11000),
  (SELECT Branch_id FROM Branch WHERE Branch_name='Headquarters')
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Prague East',
  (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Sokolovska' AND House_number=85 AND ZIP_code=18000),
  (SELECT Branch_id FROM Branch WHERE Branch_name='Central Prague')
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Prague West',
  (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Evropska' AND House_number=95 AND ZIP_code=16000),
  (SELECT Branch_id FROM Branch WHERE Branch_name='Central Prague')
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Brno Center',
  (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Masarykova' AND House_number=17 AND ZIP_code=60200),
  NULL
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Brno North',
  (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Joštova' AND House_number=12 AND ZIP_code=60200),
  (SELECT Branch_id FROM Branch WHERE Branch_name='Brno Center')
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Ostrava',
  (SELECT Address_id FROM Address WHERE City='Ostrava' AND Street='Nádražní' AND House_number=44 AND ZIP_code=70200),
  NULL
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Plzeň',
  (SELECT Address_id FROM Address WHERE City='Plzeň' AND Street='Americka' AND House_number=50 AND ZIP_code=30100),
  NULL
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Liberec',
  (SELECT Address_id FROM Address WHERE City='Liberec' AND Street='Pražská' AND House_number=21 AND ZIP_code=46001),
  NULL
);
INSERT INTO Branch(Branch_id, Branch_name, Address_id, Parent_branch_id) VALUES (
  BRANCH_SEQ.NEXTVAL,'Olomouc',
  (SELECT Address_id FROM Address WHERE City='Olomouc' AND Street='Dolní nám.' AND House_number=7 AND ZIP_code=77900),
  NULL
);

-- ======================
-- 4) Users (2 Admins, 8 Tellers, 20 Clients)
-- ======================
-- Admins
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Pavel', 'Kral', 'pass1', 'Y', 'Y', (SELECT Role_id FROM Role WHERE Role_name='Admin'),
        (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Na Prikope' AND House_number=14 AND ZIP_code=11000));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id)
VALUES (USER_SEQ.NEXTVAL, 'Jana', 'Novakova', 'pass2', 'Y', 'Y', (SELECT Role_id FROM Role WHERE Role_name='Admin'),
        (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Ceska' AND House_number=22 AND ZIP_code=60200));

-- Tellers (8)
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Petr','Novak','t01','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Italska' AND House_number=3));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Jana','Svoboda','t02','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Karmelitska' AND House_number=18));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Martin','Dvorak','t03','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Lidicka' AND House_number=9));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Lucie','Cerny','t04','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Kounicova' AND House_number=6));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Tomas','Prochazka','t05','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Ostrava' AND Street='28. října' AND House_number=90));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Anna','Kucera','t06','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Plzeň' AND Street='Tylova' AND House_number=15));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Jakub','Vesely','t07','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Liberec' AND Street='Masarykova' AND House_number=2));
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Tereza','Horak','t08','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Teller'), (SELECT Address_id FROM Address WHERE City='Olomouc' AND Street='Kateřinská' AND House_number=11));

-- Teller details (match the 8 teller users above by name)
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Petr'   AND Surname='Novak'    AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 101 111', 'petr.novak@bank.test',   (SELECT Branch_id FROM Branch WHERE Branch_name='Central Prague'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Jana'   AND Surname='Svoboda'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 102 121', 'jana.svoboda@bank.test', (SELECT Branch_id FROM Branch WHERE Branch_name='Prague West'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 103 131', 'martin.dvorak@bank.test',(SELECT Branch_id FROM Branch WHERE Branch_name='Brno Center'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Lucie'  AND Surname='Cerny'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 104 141', 'lucie.cerny@bank.test',  (SELECT Branch_id FROM Branch WHERE Branch_name='Brno North'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Tomas'  AND Surname='Prochazka' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 105 151', 'tomas.prochazka@bank.test',(SELECT Branch_id FROM Branch WHERE Branch_name='Ostrava'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Anna'   AND Surname='Kucera'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 106 161', 'anna.kucera@bank.test',  (SELECT Branch_id FROM Branch WHERE Branch_name='Plzeň'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Jakub'  AND Surname='Vesely'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 107 171', 'jakub.vesely@bank.test', (SELECT Branch_id FROM Branch WHERE Branch_name='Liberec'));
INSERT INTO Teller(User_id, Work_phone_number, Work_email_address, Branch_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')), '+420 777 108 181', 'tereza.horak@bank.test', (SELECT Branch_id FROM Branch WHERE Branch_name='Olomouc'));

-- Clients (20) – distinct realistic names; Approved 'Y'
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Ondrej','Katerina','c01','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Narodni' AND House_number=33));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0001', '+420 606 551 101', 'ondrej.katerina@mail.test', (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Michal','Barbora','c02','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Spalena' AND House_number=12));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0002', '+420 606 552 102', 'michal.barbora@mail.test', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Lukas','Monika','c03','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Zelny trh' AND House_number=1));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0003', '+420 606 553 103', 'lukas.monika@mail.test', (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Jiri','Veronika','c04','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Lidicka' AND House_number=9));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0004', '+420 606 554 104', 'jiri.veronika@mail.test', (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Marek','Alena','c05','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Ostrava' AND Street='Stodolni' AND House_number=10));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0005', '+420 606 555 105', 'marek.alena@mail.test', (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Filip','Klara','c06','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Plzeň' AND Street='Smetanova' AND House_number=8));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Filip' AND Surname='Klara' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0006', '+420 606 556 106', 'filip.klara@mail.test', (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Adam','Petra','c07','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Liberec' AND Street='Masarykova' AND House_number=2));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0007', '+420 606 557 107', 'adam.petra@mail.test', (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Daniel','Karolina','c08','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Olomouc' AND Street='Kateřinská' AND House_number=11));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Daniel' AND Surname='Karolina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0008', '+420 606 558 108', 'daniel.karolina@mail.test', (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

-- 12 more clients for a total of 20
INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Roman','Lenka','c09','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Vodičkova' AND House_number=28));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Roman' AND Surname='Lenka' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0009', '+420 606 559 109', 'roman.lenka@mail.test', (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Radek','Ivana','c10','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Jungmannova' AND House_number=10));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Radek' AND Surname='Ivana' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0010', '+420 606 560 110', 'radek.ivana@mail.test', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Stepan','Denisa','c11','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Masarykova' AND House_number=17));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Stepan' AND Surname='Denisa' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0011', '+420 606 561 111', 'stepan.denisa@mail.test', (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Viktor','Gabriela','c12','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Joštova' AND House_number=12));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Viktor' AND Surname='Gabriela' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0012', '+420 606 562 112', 'viktor.gabriela@mail.test', (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Milan','Marcela','c13','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Ostrava' AND Street='Nádražní' AND House_number=44));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Milan' AND Surname='Marcela' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0013', '+420 606 563 113', 'milan.marcela@mail.test', (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Pavel','Natalie','c14','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Plzeň' AND Street='Americka' AND House_number=50));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Pavel' AND Surname='Natalie' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0014', '+420 606 564 114', 'pavel.natalie@mail.test', (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Vojtech','Hana','c15','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Liberec' AND Street='Pražská' AND House_number=21));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Vojtech' AND Surname='Hana' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0015', '+420 606 565 115', 'vojtech.hana@mail.test', (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Petr','Jelinek','c16','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Olomouc' AND Street='Dolní nám.' AND House_number=7));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Jelinek' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0016', '+420 606 566 116', 'petr.jelinek@mail.test', (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Kovar','Pospisil','c17','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Evropska' AND House_number=95));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Kovar' AND Surname='Pospisil' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0017', '+420 606 567 117', 'kovar.pospisil@mail.test', (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Erben','Sedivy','c18','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Prague' AND Street='Sokolovska' AND House_number=85));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Erben' AND Surname='Sedivy' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0018', '+420 606 568 118', 'erben.sedivy@mail.test', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Krejci','Simek','c19','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Joštova' AND House_number=12));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Krejci' AND Surname='Simek' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0019', '+420 606 569 119', 'krejci.simek@mail.test', (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

INSERT INTO "User"(User_id, Name, Surname, Password, Active, Approved, Role_id, Address_id) VALUES (USER_SEQ.NEXTVAL, 'Kolar','Barta','c20','Y','Y',(SELECT Role_id FROM Role WHERE Role_name='Client'), (SELECT Address_id FROM Address WHERE City='Brno' AND Street='Lidicka' AND House_number=9));
INSERT INTO Client(User_id, Birth_number, Phone_number, Email_address, Teller_id) VALUES ((SELECT User_id FROM "User" WHERE Name='Kolar' AND Surname='Barta' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), '800101/0020', '+420 606 570 120', 'kolar.barta@mail.test', (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Teller')));

-- ======================
-- 5) Accounts (~30 rows)
-- ======================
-- One account per client (20) + 10 additional second accounts
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000001',  3500, 'Y', (SELECT User_id FROM "User" WHERE Name='Ondrej'  AND Surname='Katerina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000002',  4200, 'Y', (SELECT User_id FROM "User" WHERE Name='Michal'  AND Surname='Barbora'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000003',  5150, 'Y', (SELECT User_id FROM "User" WHERE Name='Lukas'   AND Surname='Monika'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000004',  2100, 'Y', (SELECT User_id FROM "User" WHERE Name='Jiri'    AND Surname='Veronika'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000005',  6100, 'Y', (SELECT User_id FROM "User" WHERE Name='Marek'   AND Surname='Alena'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000006',  1800, 'Y', (SELECT User_id FROM "User" WHERE Name='Filip'   AND Surname='Klara'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000007',  9700, 'Y', (SELECT User_id FROM "User" WHERE Name='Adam'    AND Surname='Petra'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000008',  2550, 'Y', (SELECT User_id FROM "User" WHERE Name='Daniel'  AND Surname='Karolina'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000009',  7250, 'Y', (SELECT User_id FROM "User" WHERE Name='Roman'   AND Surname='Lenka'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000010',  3325, 'Y', (SELECT User_id FROM "User" WHERE Name='Radek'   AND Surname='Ivana'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000011',  2880, 'Y', (SELECT User_id FROM "User" WHERE Name='Stepan'  AND Surname='Denisa'    AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000012',  4450, 'Y', (SELECT User_id FROM "User" WHERE Name='Viktor'  AND Surname='Gabriela'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000013',  3890, 'Y', (SELECT User_id FROM "User" WHERE Name='Milan'   AND Surname='Marcela'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000014',  8120, 'Y', (SELECT User_id FROM "User" WHERE Name='Pavel'   AND Surname='Natalie'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000015',  5010, 'Y', (SELECT User_id FROM "User" WHERE Name='Vojtech' AND Surname='Hana'      AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000016',  2660, 'Y', (SELECT User_id FROM "User" WHERE Name='Petr'    AND Surname='Jelinek'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000017',  3100, 'Y', (SELECT User_id FROM "User" WHERE Name='Kovar'   AND Surname='Pospisil'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000018',  2770, 'Y', (SELECT User_id FROM "User" WHERE Name='Erben'   AND Surname='Sedivy'    AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000019',  6990, 'Y', (SELECT User_id FROM "User" WHERE Name='Krejci'  AND Surname='Simek'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ5801000000000020',  2410, 'Y', (SELECT User_id FROM "User" WHERE Name='Kolar'   AND Surname='Barta'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
-- second accounts for 10 clients
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001001',  1200, 'Y', (SELECT User_id FROM "User" WHERE Name='Ondrej'  AND Surname='Katerina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001002',  2200, 'Y', (SELECT User_id FROM "User" WHERE Name='Michal'  AND Surname='Barbora'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001003',  1800, 'Y', (SELECT User_id FROM "User" WHERE Name='Lukas'   AND Surname='Monika'   AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001004',  2600, 'Y', (SELECT User_id FROM "User" WHERE Name='Jiri'    AND Surname='Veronika'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001005',  1400, 'Y', (SELECT User_id FROM "User" WHERE Name='Marek'   AND Surname='Alena'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001006',  3900, 'Y', (SELECT User_id FROM "User" WHERE Name='Filip'   AND Surname='Klara'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001007',  4100, 'Y', (SELECT User_id FROM "User" WHERE Name='Adam'    AND Surname='Petra'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001008',  2900, 'Y', (SELECT User_id FROM "User" WHERE Name='Daniel'  AND Surname='Karolina'  AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001009',  3350, 'Y', (SELECT User_id FROM "User" WHERE Name='Roman'   AND Surname='Lenka'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));
INSERT INTO Account(Account_id, Account_number, Account_balance, Account_active, Client_id) VALUES (ACCOUNT_SEQ.NEXTVAL, 'CZ3101000000001010',  1750, 'Y', (SELECT User_id FROM "User" WHERE Name='Radek'   AND Surname='Ivana'     AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')));

-- ======================
-- 6) Transactions (~35 rows) – plain INSERTs only
-- ======================
-- Helpers via scalar subqueries
-- Transfer rows (20)
-- Ondrej pays rent
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 12000, 'Nájem 12/2025', 'Nájem - Ondřej Kateřina',
        (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000001'),
        (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'),
        SYSDATE-15,
        (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000002'));

-- Lukas pays for services
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 180, 'Platba za služby', 'Služby 12/2025', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000003'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-11, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000004'));

-- Marek buys something
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 4500, 'Nákup elektroniky', 'Objednávka #12345', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000005'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-10, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000006'));

-- Fee refund for Jiri (as discussed in chat)
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 100, 'Vrácení poplatku', 'Refundace poplatku za vedení účtu', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-9 + 10/24, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000007'));

-- Regular transfers
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 310, 'Večeře', 'Za včerejšek', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000009'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-9,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000010'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 2250, 'Splátka', 'Půjčka', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000011'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-8,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000012'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 1500, 'Dárek', 'Všechno nejlepší', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000013'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-8,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000014'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 510, 'Lékárna', 'Platba kartou', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000015'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-7,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000016'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 750, 'Benzín', 'Tankování OMV', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000017'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-6,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000018'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id)
VALUES (TRANSACTION_SEQ.NEXTVAL, 260, 'Kino', 'Vstupenky', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000019'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-6,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000020'));
-- 10 more transfers between second accounts
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 130, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001001'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-5, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001002'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 190, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001003'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-5, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001004'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 70,  'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001005'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-4, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001006'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 300, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001007'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-4, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001008'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 210, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001009'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-3, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001010'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 85,  'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000002'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-3, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000001'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 145, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000004'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-2, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000003'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 220, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000006'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-2, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000005'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 175, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000008'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-1, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000007'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 260, 'Payment', 'Payment', (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000010'), (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='TRANSFER'), SYSDATE-1, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000009'));

-- Deposits (10) – Account_to only
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 300, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-14, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000001'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 220, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-13, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000005'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 150, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-12, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000010'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 480, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-11, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001003'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 95,  'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-10, (SELECT Account_id FROM Account WHERE Account_number='CZ3101000000001007'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 210, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-9,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000014'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 330, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-8,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000016'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 145, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-7,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000018'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 260, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-6,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000006'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 190, 'Cash-in', 'Deposit', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='DEPOSIT'), SYSDATE-5,  (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000020'));

-- Interest (5) – small credits
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 8,  'Interest', 'Monthly Interest', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='INTEREST'), SYSDATE-30, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000002'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 12, 'Interest', 'Monthly Interest', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='INTEREST'), SYSDATE-29, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000006'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 7,  'Interest', 'Monthly Interest', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='INTEREST'), SYSDATE-28, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000010'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 9,  'Interest', 'Monthly Interest', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='INTEREST'), SYSDATE-27, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000014'));
INSERT INTO Transaction(Transaction_id, Transfer_amount, Message_for_sender, Message_for_recipient, Account_from_id, Transaction_type_id, Transaction_time, Account_to_id) VALUES (TRANSACTION_SEQ.NEXTVAL, 6,  'Interest', 'Monthly Interest', NULL, (SELECT Transaction_type_id FROM Transaction_type WHERE Transaction_type_name='INTEREST'), SYSDATE-26, (SELECT Account_id FROM Account WHERE Account_number='CZ5801000000000018'));

-- ======================
-- 7) Messages (~30 rows) – client ↔ teller
-- ======================
-- Conversation 1: Ondrej (Client) ↔ Petr (Teller) - Question about account
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, mohl byste se prosím podívat na můj aktuální zůstatek? Zdá se mi nějaký nízký.', 'Y', (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina'), SYSDATE-12, (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Ondřeji. Samozřejmě, hned se na to podívám. Vidím tam nedávnou platbu za nájem, která mohla zůstatek snížit.', 'Y', (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'), SYSDATE-12 + 1/24, (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Aha, máte pravdu, na ten nájem jsem úplně zapomněl. Děkuji za rychlou odpověď!', 'Y', (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina'), SYSDATE-12 + 2/24, (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'));

-- Conversation 2: Michal (Client) ↔ Jana Svoboda (Teller) - Card delivery
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Zdravím, kdy můžu očekávat doručení své nové platební karty?', 'Y', (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora'), SYSDATE-11, (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Michale. Vaše karta byla odeslána včera, měla by dorazit do 3 pracovních dnů.', 'Y', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'), SYSDATE-11 + 2/24, (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Skvělé, děkuji moc.', 'Y', (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora'), SYSDATE-11 + 3/24, (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'));

-- Conversation 3: Lukas (Client) ↔ Martin (Teller) - Transaction inquiry
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Mohl byste prosím prověřit moji poslední platbu? Nejsem si jistý, zda odešla správná částka.', 'Y', (SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika'), SYSDATE-10, (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Lukáši. Prověřil jsem to a částka 180 CZK byla úspěšně odeslána na účet CZ5801000000000004.', 'Y', (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'), SYSDATE-10 + 1/24, (SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Děkuji, to sedí.', 'Y', (SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika'), SYSDATE-10 + 2/24, (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'));

-- Conversation 4: Jiri (Client) ↔ Lucie (Teller) - Fee refund request
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, byl mi naúčtován poplatek za vedení účtu, i když jsem měl mít tento měsíc slevu. Mohli byste to prověřit?', 'Y', (SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika'), SYSDATE-9, (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Jiří. Omlouvám se za chybu, poplatek vám bude v nejbližší době vrácen zpět na účet.', 'Y', (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny'), SYSDATE-9 + 5/24, (SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Moc děkuji za vyřízení.', 'Y', (SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika'), SYSDATE-9 + 6/24, (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny'));

-- Conversation 5: Marek (Client) ↔ Tomas (Teller) - Account statement
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Potřeboval byhc zaslat výpis z účtu za minulý měsíc ve formátu PDF.', 'Y', (SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena'), SYSDATE-8, (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Marku. Výpis jsem vám právě odeslal do sekce dokumentů ve vašem profilu.', 'Y', (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka'), SYSDATE-8 + 1/24, (SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Děkuji, už ho tam vidím.', 'Y', (SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena'), SYSDATE-8 + 2/24, (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka'));

-- Conversation 6: Filip (Client) ↔ Anna (Teller) - Limit increase
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, rád bych si navýšil denní limit pro platby kartou na 50 000 CZK.', 'Y', (SELECT User_id FROM "User" WHERE Name='Filip' AND Surname='Klara'), SYSDATE-7, (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Filipe. Váš limit byl úspěšně navýšen. Změna se projeví okamžitě.', 'Y', (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera'), SYSDATE-7 + 2/24, (SELECT User_id FROM "User" WHERE Name='Filip' AND Surname='Klara'));

-- Conversation 7: Adam (Client) ↔ Jakub (Teller) - Lost card
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Ztratil jsem kartu! Prosím o okamžité zablokování.', 'Y', (SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra'), SYSDATE-6, (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Adame. Karta byla zablokována. Přejete si vystavit novou?', 'Y', (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely'), SYSDATE-6 + 10/1440, (SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Ano, prosím o novou kartu.', 'Y', (SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra'), SYSDATE-6 + 30/1440, (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely'));

-- Conversation 8: Daniel (Client) ↔ Tereza (Teller) - Branch change
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, přestěhoval jsem se do Olomouce a rád bych změnil svou domovskou pobočku.', 'Y', (SELECT User_id FROM "User" WHERE Name='Daniel' AND Surname='Karolina'), SYSDATE-5, (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Dobrý den, Danieli. Žádný problém, domovskou pobočku jsem vám změnila na pobočku v Olomouci.', 'Y', (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak'), SYSDATE-5 + 4/24, (SELECT User_id FROM "User" WHERE Name='Daniel' AND Surname='Karolina'));

-- System messages and notifications
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Upozornění: Vaše heslo vyprší za 7 dní. Prosím o jeho změnu v nastavení.', 'N', NULL, SYSDATE-3, (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Nová verze obchodních podmínek je k dispozici ke stažení.', 'N', NULL, SYSDATE-2, (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora'));
INSERT INTO Message(Message_id, Message_text, Message_read, User_from_id, Message_sent_at, User_to_id) VALUES (MESSAGE_SEQ.NEXTVAL, 'Vaše platba ve výši 510 CZK byla úspěšně zpracována.', 'N', NULL, SYSDATE-7, (SELECT User_id FROM "User" WHERE Name='Vojtech' AND Surname='Hana'));

-- ======================
-- 8) Login records (25 rows)
-- ======================
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.10', (SELECT User_id FROM "User" WHERE Name='Pavel' AND Surname='Kral'), SYSDATE-10);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.11', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Novakova'), SYSDATE-9);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.12', (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'), SYSDATE-8);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.13', (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'), SYSDATE-8);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.14', (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'), SYSDATE-7);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.15', (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny'), SYSDATE-7);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.16', (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka'), SYSDATE-6);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.17', (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera'), SYSDATE-6);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.18', (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely'), SYSDATE-5);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '192.168.1.19', (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak'), SYSDATE-5);
-- 15 client logins
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.1',  (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-15);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.2',  (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-14);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.3',  (SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-13);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.4',  (SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-12);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.5',  (SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-11);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.6',  (SELECT User_id FROM "User" WHERE Name='Filip' AND Surname='Klara' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-10);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.7',  (SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-9);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.8',  (SELECT User_id FROM "User" WHERE Name='Daniel' AND Surname='Karolina' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-8);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.9',  (SELECT User_id FROM "User" WHERE Name='Roman' AND Surname='Lenka' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-7);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.10', (SELECT User_id FROM "User" WHERE Name='Radek' AND Surname='Ivana' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-6);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.11', (SELECT User_id FROM "User" WHERE Name='Stepan' AND Surname='Denisa' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-5);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.12', (SELECT User_id FROM "User" WHERE Name='Viktor' AND Surname='Gabriela' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-4);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.13', (SELECT User_id FROM "User" WHERE Name='Milan' AND Surname='Marcela' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-3);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.14', (SELECT User_id FROM "User" WHERE Name='Pavel' AND Surname='Natalie' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-2);
INSERT INTO Login_record(Login_id, Login_ip_address, User_id, Login_time) VALUES (LOGIN_SEQ.NEXTVAL, '10.0.0.15', (SELECT User_id FROM "User" WHERE Name='Vojtech' AND Surname='Hana' AND Role_id=(SELECT Role_id FROM Role WHERE Role_name='Client')), SYSDATE-1);

-- ======================
-- 9) Audit logs (15 rows)
-- ======================
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'User created: Pavel Kral', SYSDATE-20, (SELECT User_id FROM "User" WHERE Name='Pavel' AND Surname='Kral'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'User updated: Jana Novakova', SYSDATE-19, (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Novakova'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Login: Petr Novak',  SYSDATE-18, (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Account created: CZ5801000000000001', SYSDATE-17, (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Logout: Martin Dvorak', SYSDATE-16, (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'));
-- 10 more assorted audit rows for clients
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'User created: Ondrej Katerina', SYSDATE-15, (SELECT User_id FROM "User" WHERE Name='Ondrej' AND Surname='Katerina'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Password changed', SYSDATE-14, (SELECT User_id FROM "User" WHERE Name='Michal' AND Surname='Barbora'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Login: Lukas Monika',  SYSDATE-13, (SELECT User_id FROM "User" WHERE Name='Lukas' AND Surname='Monika'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Address updated', SYSDATE-12, (SELECT User_id FROM "User" WHERE Name='Jiri' AND Surname='Veronika'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Document uploaded: pavel_kral_doc1.pdf', SYSDATE-11, (SELECT User_id FROM "User" WHERE Name='Marek' AND Surname='Alena'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Login: Filip Klara',  SYSDATE-10, (SELECT User_id FROM "User" WHERE Name='Filip' AND Surname='Klara'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Logout: Adam Petra', SYSDATE-9,  (SELECT User_id FROM "User" WHERE Name='Adam' AND Surname='Petra'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Home branch updated to Olomouc', SYSDATE-8,  (SELECT User_id FROM "User" WHERE Name='Daniel' AND Surname='Karolina'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'Login: Roman Lenka',  SYSDATE-7,  (SELECT User_id FROM "User" WHERE Name='Roman' AND Surname='Lenka'));
INSERT INTO Audit_log(Audit_id, Change_type, Change_time, User_id) VALUES (AUDIT_SEQ.NEXTVAL, 'User created: Radek Ivana', SYSDATE-6,  (SELECT User_id FROM "User" WHERE Name='Radek' AND Surname='Ivana'));

-- ======================
-- 10) Documents (10 rows) – metadata with empty blobs
-- ======================
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'pavel_kral_doc1',     'pdf',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Pavel' AND Surname='Kral'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'jana_novakova_doc2',  'png',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Novakova'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'petr_novak_doc3',     'jpg',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Petr' AND Surname='Novak'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'jana_svoboda_doc4',   'docx', EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Jana' AND Surname='Svoboda'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'martin_dvorak_doc5',  'xlsx', EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Martin' AND Surname='Dvorak'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'lucie_cerny_doc6',    'pdf',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Lucie' AND Surname='Cerny'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'tomas_prochazka_doc7','png',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Tomas' AND Surname='Prochazka'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'anna_kucera_doc8',    'jpg',  EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Anna' AND Surname='Kucera'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'jakub_vesely_doc9',   'docx', EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Jakub' AND Surname='Vesely'));
INSERT INTO Document(Document_id, File_name, File_extension, File_data, User_id) VALUES (DOCUMENT_SEQ.NEXTVAL, 'tereza_horak_doc10',  'xlsx', EMPTY_BLOB(), (SELECT User_id FROM "User" WHERE Name='Tereza' AND Surname='Horak'));

COMMIT;

-- =============================================================
-- End of RealInsertScript.sql (Plain INSERT version)
-- =============================================================
