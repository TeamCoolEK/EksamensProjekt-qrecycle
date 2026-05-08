-- ============================================================
-- DEV SEED — full schema + dummy data
-- Based on ER diagram + existing business list
-- ============================================================

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS Collection CASCADE;
DROP TABLE IF EXISTS Temp_Stop CASCADE;
DROP TABLE IF EXISTS Route CASCADE;
DROP TABLE IF EXISTS Expenses CASCADE;
DROP TABLE IF EXISTS Business CASCADE;
DROP TABLE IF EXISTS AppUser CASCADE;

-- ============================================================
-- AppUser
-- ============================================================
CREATE TABLE AppUser (
                         id          SERIAL PRIMARY KEY,
                         username    VARCHAR(255) NOT NULL,
                         password    VARCHAR(255) NOT NULL,
                         role        INT NOT NULL  -- 0 = admin, 1 = driver, 2 = business
);

INSERT INTO AppUser (id, username, password, role) VALUES
                                                       (1,  'pappas_pizza',       'hashed_pw_1',  2),
                                                       (2,  'muban_thai',         'hashed_pw_2',  2),
                                                       (3,  'hulks_burger',       'hashed_pw_3',  2),
                                                       (4,  'boldklubben',        'hashed_pw_4',  2),
                                                       (5,  'boys_albertslund',   'hashed_pw_5',  2),
                                                       (6,  'alliance_ishoj',     'hashed_pw_6',  2),
                                                       (7,  'bagel_kebab_lab',    'hashed_pw_7',  2),
                                                       (8,  'driver_lars',        'hashed_pw_8',  1),
                                                       (9,  'driver_mette',       'hashed_pw_9',  1),
                                                       (10, 'admin_søren',        'hashed_pw_10', 0);

-- ============================================================
-- Business
-- ============================================================
CREATE TABLE Business (
                          id          SERIAL PRIMARY KEY,
                          name        VARCHAR(255) NOT NULL,
                          address     VARCHAR(255) NOT NULL,
                          appUser_id  INT REFERENCES AppUser(id)
);

INSERT INTO Business (id, name, address, appUser_id) VALUES
                                                         (1, 'Pappas pizza',          'Vigerslev Allé 122, 2500 København',  1),
                                                         (2, 'Muban Thai',            'Sluseholmen 16D, 2450 København',      2),
                                                         (3, 'Hulks Burger Glostrup', 'Hovedvejen 105, 2600 Glostrup',        3),
                                                         (4, 'Boldklubben Folehaven', 'Folehaven 39, 2500 København',         4),
                                                         (5, 'Boys Albertslund',      'Holsbjergvej 35, 2620 Albertslund',    5),
                                                         (6, 'Alliance Ishøj',        'Ishøj Østergade 16, 2635 Ishøj',       6),
                                                         (7, 'Bagel/Kebab Lab/Thai',  'Folehaven 70, 2500 København',         7);

-- ============================================================
-- Expenses
-- ============================================================
CREATE TABLE Expenses (
                          id          SERIAL PRIMARY KEY,
                          value       INT NOT NULL,
                          attachment  VARCHAR(255),
                          category    INT NOT NULL,  -- 1 = fuel, 2 = maintenance, 3 = other
                          date        DATE NOT NULL,
                          appUser_id  INT REFERENCES AppUser(id)
);

INSERT INTO Expenses (id, value, attachment, category, date, appUser_id) VALUES
                                                                             (1, 450,  'receipt_fuel_lars_01.jpg',    1, '2026-05-01', 8),
                                                                             (2, 1200, 'receipt_service_lars.jpg',    2, '2026-05-03', 8),
                                                                             (3, 380,  'receipt_fuel_mette_01.jpg',   1, '2026-05-02', 9),
                                                                             (4, 200,  NULL,                          3, '2026-05-04', 9),
                                                                             (5, 560,  'receipt_fuel_lars_02.jpg',    1, '2026-05-06', 8);

-- ============================================================
-- Route
-- ============================================================
CREATE TABLE Route (
                       id      SERIAL PRIMARY KEY,
                       status  INT NOT NULL  -- 0 = planned, 1 = active, 2 = completed
);

INSERT INTO Route (id, status) VALUES
                                   (1, 2),
                                   (2, 2),
                                   (3, 1),
                                   (4, 0);

-- ============================================================
-- Temp_Stop
-- ============================================================
CREATE TABLE Temp_Stop (
                           id      SERIAL PRIMARY KEY,
                           address VARCHAR(255) NOT NULL,
                           route_id INT REFERENCES Route(id)
);

INSERT INTO Temp_Stop (id, address, route_id) VALUES
                                                  (1,  'Vigerslev Allé 122, 2500 København',  1),
                                                  (2,  'Folehaven 39, 2500 København',         1),
                                                  (3,  'Folehaven 70, 2500 København',         1),
                                                  (4,  'Sluseholmen 16D, 2450 København',      2),
                                                  (5,  'Holsbjergvej 35, 2620 Albertslund',    2),
                                                  (6,  'Ishøj Østergade 16, 2635 Ishøj',       2),
                                                  (7,  'Hovedvejen 105, 2600 Glostrup',        3),
                                                  (8,  'Folehaven 39, 2500 København',         3),
                                                  (9,  'Vigerslev Allé 122, 2500 København',  4),
                                                  (10, 'Sluseholmen 16D, 2450 København',      4);

-- ============================================================
-- Collection
-- ============================================================
CREATE TABLE Collection (
                            id            SERIAL PRIMARY KEY,
                            status        INT NOT NULL,   -- 0 = pending, 1 = collected, 2 = failed
                            business_bags INT NOT NULL,
                            driver_bags   INT NOT NULL,
                            date          DATE NOT NULL,
                            buisness_id   INT REFERENCES Business(id)
);

INSERT INTO Collection (id, status, business_bags, driver_bags, date, buisness_id) VALUES
                                                                                       (1,  1, 12, 12, '2026-05-01', 1),
                                                                                       (2,  1, 8,  8,  '2026-05-01', 4),
                                                                                       (3,  1, 15, 14, '2026-05-01', 7),
                                                                                       (4,  1, 10, 10, '2026-05-02', 2),
                                                                                       (5,  1, 6,  6,  '2026-05-02', 5),
                                                                                       (6,  1, 9,  9,  '2026-05-02', 6),
                                                                                       (7,  1, 11, 11, '2026-05-03', 3),
                                                                                       (8,  1, 7,  7,  '2026-05-03', 1),
                                                                                       (9,  0, 0,  0,  '2026-05-08', 2),
                                                                                       (10, 0, 0,  0,  '2026-05-08', 5),
                                                                                       (11, 0, 0,  0,  '2026-05-08', 6),
                                                                                       (12, 0, 0,  0,  '2026-05-08', 7);