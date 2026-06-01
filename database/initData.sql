SET FOREIGN_KEY_CHECKS = 0;

-- Locality
INSERT INTO Locality (postalId, city)
VALUES (1000, 'Brussels'),
       (2000, 'Antwerp'),
       (4000, 'Liège'),
       (9000, 'Ghent'),
       (3000, 'Leuven');

-- Address_
INSERT INTO Address_ (streetName, streetNumber, postalId, city)
VALUES ('Rue de la Loi', 42, 1000, 'Brussels'),
       ('Avenue Louise', 120, 1000, 'Brussels'),
       ('Meir', 50, 2000, 'Antwerp'),
       ('Rue du Pot d Or', 15, 4000, 'Liège'),
       ('Veldstraat', 88, 9000, 'Ghent'),
       ('Bondgenotenlaan', 22, 3000, 'Leuven'),
       ('Rue Royale', 5, 1000, 'Brussels'),
       ('Nationalestraat', 33, 2000, 'Antwerp'),
       ('Rue de la Cathédrale', 7, 4000, 'Liège'),
       ('Korenmarkt', 1, 9000, 'Ghent');

-- Employee
INSERT INTO Employee (firstname, lastname, email, phoneNumber, iban, hourlyWage, nbHoursPlannedWeek, hiringDate,
                      nbPaidDaysHalfDay, pwd, addressId, managerId)
VALUES ('Alice', 'Janssen', 'alice.janssen@shop.be', '+32475550101', 'BE68539007547034', 22.50, 40, '2019-03-15', 40,
        'hashed_pwd_001', 1, NULL),
       ('Bob', 'Peeters', 'bob.peeters@shop.be', '+32475550102', 'BE71096917112961', 18.00, 35, '2020-07-01', 30,
        'hashed_pwd_002', 2, NULL),
       ('Carol', 'Maes', 'carol.maes@shop.be', '+32475550103', 'BE97210012663460', 20.00, 40, '2021-01-10', 20,
        'hashed_pwd_003', 3, NULL),
       ('David', 'Dubois', 'david.dubois@shop.be', '+32475550104', 'BE53627056514702', 19.50, 38, '2022-05-22', 25,
        'hashed_pwd_004', 4, NULL),
       ('Eva', 'Claes', 'eva.claes@shop.be', '+32475550105', 'BE74726060484232', 17.00, 30, '2023-02-14', 20,
        'hashed_pwd_005', 5, NULL),
       ('Frank', 'Vermeersch', 'frank.vermeersch@shop.be', '+32475550106', 'BE43798205413577', 25.00, 40, '2018-11-03',
        50, 'hashed_pwd_006', 6, NULL),
       ('Grace', 'Willems', 'grace.willems@shop.be', '+32475550107', 'BE32587963214850', 21.00, 40, '2020-09-17', 35,
        'hashed_pwd_007', 7, NULL),
       ('Henry', 'Leclercq', 'henry.leclercq@shop.be', '+32475550108', 'BE65148677096264', 16.50, 32, '2023-06-01', 15,
        'hashed_pwd_008', 8, NULL);

UPDATE Employee
SET managerId = 1
WHERE id_ IN (2, 3, 4);
UPDATE Employee
SET managerId = 6
WHERE id_ IN (5, 7, 8);

-- Absence_type
INSERT INTO Absence_type (name_)
VALUES ('Paid Leave'),
       ('Sick Leave'),
       ('Unpaid Leave'),
       ('Maternity Leave'),
       ('Training Leave');

-- Absence
INSERT INTO Absence (startDate, endDate, description_, employeeId, absenceTypeId)
VALUES ('2025-08-01', '2025-08-05', 'Summer holiday', 2, 1),
       ('2025-09-10', '2025-09-11', 'Flu', 3, 2),
       ('2025-10-20', NULL, 'Personal errand', 4, 3),
       ('2025-11-03', '2025-11-14', 'Maternity', 5, 4),
       ('2026-01-12', '2026-01-14', 'Tech conference training', 7, 5);

-- Role_
INSERT INTO Role_ (name_)
VALUES ('Store Manager'),
       ('Cashier'),
       ('Stock Clerk'),
       ('Accountant'),
       ('Logistics Coordinator'),
       ('HR Officer'),
       ('Chef'),
       ('Delivery Driver');

-- Position_
INSERT INTO Position_ (roleId, employeeId)
VALUES (1, 1),
       (6, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (2, 5),
       (1, 6),
       (5, 7),
       (8, 8);

-- Pointing
INSERT INTO Pointing (date_, startTime, endTime, employeeId)
VALUES ('2026-05-20', '08:00:00', '16:30:00', 1),
       ('2026-05-20', '09:00:00', '17:00:00', 2),
       ('2026-05-20', '07:30:00', '15:30:00', 3),
       ('2026-05-21', '08:00:00', '16:00:00', 1),
       ('2026-05-21', '08:30:00', '17:30:00', 4),
       ('2026-05-21', '09:00:00', '13:00:00', 5),
       ('2026-05-22', '07:00:00', '15:00:00', 6),
       ('2026-05-22', '10:00:00', '18:00:00', 7),
       ('2026-05-23', '08:00:00', '12:00:00', 8),
       ('2026-05-23', '08:00:00', '16:00:00', 2);

-- ProductCategory
INSERT INTO ProductCategory (name_)
VALUES ('Household Goods'),
       ('Home Appliances'),
       ('Fruits & Vegetables'),
       ('Meat & Poultry'),
       ('Dairy Products'),
       ('Bakery'),
       ('Beverages'),
       ('Frozen Foods'),
       ('Cleaning Supplies'),
       ('Snacks & Confectionery');

-- Product
INSERT INTO Product (name_, priceEVAT, VAT, loyaltyPoints, isEdible, minStockQuantity, categoryId)
VALUES ('Whole Milk 1L', 0.85, 6.00, 2, TRUE, 20, 5),
       ('Free-Range Eggs x12', 2.50, 6.00, 5, TRUE, 15, 5),
       ('Sourdough Bread 500g', 2.20, 6.00, 4, TRUE, 10, 6),
       ('Chicken Breast 1kg', 6.80, 12.00, 8, TRUE, 8, 4),
       ('Ground Beef 500g', 4.50, 12.00, 6, TRUE, 10, 4),
       ('Broccoli 500g', 1.20, 6.00, 2, TRUE, 12, 3),
       ('Organic Tomatoes 1kg', 1.80, 6.00, 3, TRUE, 15, 3),
       ('Cheddar Cheese 250g', 2.90, 6.00, 5, TRUE, 10, 5),
       ('Orange Juice 1L', 1.50, 6.00, 3, TRUE, 20, 7),
       ('Sparkling Water 1.5L', 0.60, 6.00, 1, TRUE, 30, 7),
       ('Frozen Pizza Margherita', 3.40, 12.00, 5, TRUE, 15, 8),
       ('Vanilla Ice Cream 500ml', 2.80, 12.00, 4, TRUE, 10, 8),
       ('All-Purpose Flour 1kg', 0.95, 6.00, 2, TRUE, 25, 6),
       ('White Sugar 1kg', 0.90, 6.00, 2, TRUE, 20, 6),
       ('Dish Soap 500ml', 1.80, 21.00, 2, FALSE, 15, 9),
       ('Laundry Detergent 2kg', 7.50, 21.00, 8, FALSE, 8, 9),
       ('Kitchen Sponge x3', 1.20, 21.00, 1, FALSE, 20, 1),
       ('Toaster 2-Slice', 19.90, 21.00, 15, FALSE, 5, 2),
       ('Coffee Maker 1.5L', 34.99, 21.00, 25, FALSE, 3, 2),
       ('Chocolate Bar 100g', 1.10, 6.00, 2, TRUE, 30, 10),
       ('Potato Chips 150g', 1.30, 6.00, 2, TRUE, 25, 10),
       ('Butter 250g', 2.10, 6.00, 4, TRUE, 15, 5),
       ('Greek Yogurt 500g', 1.95, 6.00, 3, TRUE, 12, 5),
       ('Salmon Fillet 300g', 7.20, 12.00, 10, TRUE, 6, 4),
       ('Caesar Salad Kit', 3.50, 6.00, 5, TRUE, 10, 3);

-- LocationProduct
INSERT INTO LocationProduct (shelf, floor_, isStock, isFreezer)
VALUES ('C-a', 1, FALSE, FALSE),
       ('C-a', 2, FALSE, FALSE),
       ('C-b', 1, FALSE, TRUE),
       ('D-a', 1, FALSE, FALSE),
       ('D-b', 1, TRUE, FALSE),
       ('D-b', 2, TRUE, FALSE);

-- QuantityProduct
INSERT INTO QuantityProduct (shelf, floor_, isStock, productId, quantity)
VALUES ('A-a', 1, TRUE, 15, 50),
       ('A-a', 1, TRUE, 16, 30),
       ('A-a', 2, TRUE, 17, 40),
       ('A-a', 3, TRUE, 11, 20),
       ('A-a', 3, TRUE, 12, 15),
       ('A-b', 1, TRUE, 4, 8),
       ('A-b', 1, TRUE, 5, 12),
       ('B-a', 1, FALSE, 1, 35),
       ('B-a', 1, FALSE, 2, 25),
       ('B-a', 2, FALSE, 3, 18),
       ('B-a', 2, FALSE, 6, 22),
       ('B-a', 2, FALSE, 7, 28),
       ('B-a', 3, FALSE, 11, 14),
       ('B-b', 1, FALSE, 12, 10),
       ('C-a', 1, FALSE, 9, 40),
       ('C-a', 1, FALSE, 10, 60),
       ('C-a', 2, FALSE, 20, 55),
       ('C-a', 2, FALSE, 21, 45),
       ('C-b', 1, FALSE, 24, 10),
       ('D-a', 1, FALSE, 18, 5),
       ('D-a', 1, FALSE, 19, 3);

-- Discount
INSERT INTO Discount (discountPercentage, requiredQuantity, startDate, endDate, name_, productId)
VALUES (10.00, 2, '2026-06-01', '2026-06-30', 'June Dairy Deal', 1),
       (15.00, 3, '2026-06-01', '2026-06-15', 'Buy 3 Eggs Save 15%', 2),
       (20.00, 1, '2026-07-01', '2026-07-31', 'Summer Meat Promo', 4),
       (10.00, 2, '2026-06-01', '2026-06-30', 'Refreshment Bundle', 9),
       (5.00, 1, '2026-06-15', '2026-08-31', 'Snack Attack', 20),
       (25.00, 1, '2026-06-01', '2026-06-30', 'Appliance Month', 18);

-- Client_supplier
INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('Our Shop SPRL', NULL, 'admin@ourshop.be', '+3225550000', FALSE, FALSE, TRUE, 'BE0999999999', NULL, 1);

INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('FreshFarm Belgium', NULL, 'orders@freshfarm.be', '+3225550201', FALSE, TRUE, FALSE, 'BE0100000001', NULL, 3),
       ('Belgomeat NV', NULL, 'sales@belgomeat.be', '+3225550202', FALSE, TRUE, FALSE, 'BE0100000002', NULL, 4),
       ('Electro Wholesale BE', NULL, 'b2b@electrowholesale.be', '+3225550203', FALSE, TRUE, FALSE, 'BE0100000003',
        NULL, 6),
       ('Daily Dairy BVBA', NULL, 'supply@dailydairy.be', '+3225550204', FALSE, TRUE, FALSE, 'BE0100000004', NULL, 9);

INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('Dupont', 'Thomas', 'thomas.dupont@gmail.com', '+32475550301', TRUE, FALSE, FALSE, NULL, '2023-01-15', 2),
       ('De Smedt', 'Laura', 'laura.desmedt@gmail.com', '+32475550302', TRUE, FALSE, FALSE, NULL, '2023-03-22', 5),
       ('Claes', 'Pieter', 'pieter.claes@gmail.com', '+32475550303', TRUE, FALSE, FALSE, NULL, '2024-06-10', 7),
       ('Simon', 'Nathalie', 'nathalie.simon@gmail.com', '+32475550304', TRUE, FALSE, FALSE, NULL, '2024-09-05', 8),
       ('Goossens', 'Marc', 'marc.goossens@gmail.com', '+32475550305', TRUE, FALSE, FALSE, NULL, '2025-02-18', 10);

INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('GreenLeaf BVBA', NULL, 'contact@greenleaf.be', '+3225550400', TRUE, TRUE, FALSE, 'BE0200000001', '2022-11-01',
        1);

-- FidelityCard
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 120, TRUE, id_
FROM Client_supplier
WHERE email = 'thomas.dupont@gmail.com';
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 350, TRUE, id_
FROM Client_supplier
WHERE email = 'laura.desmedt@gmail.com';
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 80, TRUE, id_
FROM Client_supplier
WHERE email = 'pieter.claes@gmail.com';
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 500, TRUE, id_
FROM Client_supplier
WHERE email = 'nathalie.simon@gmail.com';
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 0, TRUE, id_
FROM Client_supplier
WHERE email = 'marc.goossens@gmail.com';
INSERT INTO FidelityCard (points, isValid, clientId)
SELECT 220, TRUE, id_
FROM Client_supplier
WHERE email = 'contact@greenleaf.be';

-- WorkFlowType
INSERT INTO WorkFlowType (name_, isBuy, isSupplier, isInternal)
VALUES ('Purchase from Supplier', TRUE, TRUE, FALSE),
       ('Sale to Client', FALSE, FALSE, FALSE),
       ('Internal Transfer', FALSE, FALSE, TRUE),
       ('Return to Supplier', FALSE, TRUE, FALSE),
       ('Client Return', TRUE, FALSE, FALSE);

-- Status_
INSERT INTO Status_ (name_)
VALUES ('Draft'),
       ('Pending'),
       ('Confirmed'),
       ('Shipped'),
       ('Delivered'),
       ('Cancelled'),
       ('On Hold'),
       ('Returned');

-- WorkFlow
INSERT INTO WorkFlow (workFlowTypeId, statusId, usId, otherId)
VALUES (1, 'Confirmed', 1, 2),
       (1, 'Delivered', 1, 3),
       (1, 'Pending', 1, 4),
       (2, 'Shipped', 1, 7),
       (2, 'Delivered', 1, 8),
       (2, 'Draft', 1, 9),
       (3, 'Confirmed', 1, NULL),
       (4, 'Pending', 1, 3),
       (2, 'Delivered', 1, 11),
       (5, 'Returned', 1, 8);

-- DocumentType
INSERT INTO DocumentType (name_)
VALUES ('Purchase Order'),
       ('Delivery Note'),
       ('Invoice'),
       ('Credit Note'),
       ('Preparation Order'),
       ('Return Order');

-- Document_
INSERT INTO Document_ (date_, plannedSendingDate, plannedReceiveDate, effectiveSendingDate, effectiveReceiveDate,
                       paymentDelay, commentary, isChecked, workflowId, documentTypeId, addressId)
VALUES ('2026-05-01', '2026-06-05', '2026-06-20', NULL, NULL, 30, NULL, FALSE, 1, 1, 3),
       ('2026-04-10', '2026-05-01', '2026-05-15', '2026-05-01', '2026-05-14', 30, NULL, TRUE, 2, 1, 4),
       ('2026-05-14', NULL, NULL, '2026-05-14', '2026-05-15', 0, NULL, TRUE, 2, 2, 4),
       ('2026-05-20', NULL, NULL, '2026-05-20', NULL, 0, NULL, FALSE, 4, 2, 2),
       ('2026-05-15', NULL, NULL, NULL, NULL, 15, NULL, TRUE, 4, 3, 2),
       ('2026-05-22', NULL, NULL, NULL, NULL, 15, NULL, FALSE, 5, 3, 5),
       ('2026-05-23', NULL, NULL, NULL, NULL, 0, 'Prep for weekend', FALSE, 7, 5, NULL),
       ('2026-05-25', NULL, NULL, NULL, NULL, 0, NULL, FALSE, 10, 4, 5),
       ('2026-05-26', '2026-06-10', '2026-06-25', NULL, NULL, 45, NULL, FALSE, 3, 1, 6),
       ('2026-05-27', NULL, NULL, '2026-05-27', NULL, 0, NULL, FALSE, 7, 2, NULL);

-- WorkFlowDocument
INSERT INTO WorkFlowDocument (workflowId, documentId)
VALUES (1, 1),
       (2, 2),
       (2, 3),
       (4, 4),
       (4, 5),
       (5, 6),
       (7, 7),
       (10, 8),
       (3, 9),
       (7, 10);

-- Recipe
INSERT INTO Recipe (name_, instructions, finalProductId)
VALUES ('Classic Sourdough Bread',
        'Mix flour, water, salt and starter. Ferment 8h. Shape. Proof 4h. Bake at 230C for 40 min.', 3),
       ('Homemade Margherita Pizza', 'Prepare dough. Spread tomato sauce. Add mozzarella. Bake at 250C for 12 min.',
        11),
       ('Vanilla Ice Cream Base',
        'Whisk egg yolks with sugar. Heat milk. Combine. Add cream and vanilla. Churn 30 min. Freeze 4h.', 12);

-- RecipeComposition
INSERT INTO RecipeComposition (recipeId, productId, quantity)
VALUES (1, 13, 2),
       (1, 14, 1),
       (2, 13, 1),
       (2, 7, 2),
       (2, 8, 1),
       (3, 1, 2),
       (3, 2, 1),
       (3, 14, 1);

-- Detail
INSERT INTO Detail (documentId, productId, quantity, priceVAT, VAT, fidelityPointsEarned)
VALUES (1, 6, 100, 1.27, 6.00, 0),
       (1, 7, 150, 1.91, 6.00, 0),
       (2, 4, 50, 7.62, 12.00, 0),
       (2, 5, 80, 5.04, 12.00, 0),
       (5, 1, 3, 0.90, 6.00, 6),
       (5, 9, 2, 1.59, 6.00, 6),
       (6, 8, 2, 3.07, 6.00, 10),
       (6, 20, 5, 1.17, 6.00, 10),
       (7, 13, 4, 1.01, 6.00, 0),
       (8, 22, 2, 2.23, 6.00, 0);

-- Batch
INSERT INTO Batch (detailId, productId, expirationDate, originCountry)
VALUES (1, 6, '2026-07-15', 'Belgium'),
       (2, 7, '2026-06-30', 'Netherlands'),
       (3, 4, '2026-06-10', 'Belgium'),
       (4, 5, '2026-06-05', 'France'),
       (5, 1, '2026-09-01', 'Belgium'),
       (6, 9, '2027-01-01', 'Spain'),
       (7, 8, '2026-11-30', 'France'),
       (8, 20, '2027-06-01', 'Belgium'),
       (9, 13, '2027-12-31', 'Belgium'),
       (10, 22, '2026-10-15', 'Ireland');

-- PreparationOrder
INSERT INTO PreparationOrder (documentId, recipeId)
VALUES (7, 1),
       (10, 2);

SET FOREIGN_KEY_CHECKS = 1;