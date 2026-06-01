CREATE DATABASE IF NOT EXISTS `${MYSQL_DATABASE}`;
USE `${MYSQL_DATABASE}`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Pointing;
DROP TABLE IF EXISTS Batch;
DROP TABLE IF EXISTS Detail;
DROP TABLE IF EXISTS RecipeComposition;
DROP TABLE IF EXISTS Recipe;
DROP TABLE IF EXISTS PreparationOrder;
DROP TABLE IF EXISTS Discount;
DROP TABLE IF EXISTS QuantityProduct;
DROP TABLE IF EXISTS LocationProduct;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS ProductCategory;
DROP TABLE IF EXISTS Document_;
DROP TABLE IF EXISTS DocumentType;
DROP TABLE IF EXISTS WorkFlow;
DROP TABLE IF EXISTS Status_;
DROP TABLE IF EXISTS WorkFlowType;
DROP TABLE IF EXISTS WorkFlowDocument;
DROP TABLE IF EXISTS FidelityCard;
DROP TABLE IF EXISTS Client_supplier;
DROP TABLE IF EXISTS Position_;
DROP TABLE IF EXISTS Role_;
DROP TABLE IF EXISTS Absence;
DROP TABLE IF EXISTS Absence_type;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Address_;
DROP TABLE IF EXISTS Locality;
DROP VIEW IF EXISTS vw_ProductSuppliers;
DROP VIEW IF EXISTS vw_LowQuantity_ProductSupplier;

SET FOREIGN_KEY_CHECKS = 1;


-- ! TABLES CREATION --
CREATE TABLE Locality
(
    postalId INT          NOT NULL,
    city     VARCHAR(255) NOT NULL,

    PRIMARY KEY (postalId, city)
);


CREATE TABLE Address_
(
    id_          INT AUTO_INCREMENT PRIMARY KEY,
    streetName   VARCHAR(255) NOT NULL,
    streetNumber INT          NOT NULL,
    postalId     INT          NOT NULL,
    city         VARCHAR(255) NOT NULL,

    UNIQUE (streetName, streetNumber, postalId, city),
    FOREIGN KEY (postalId, city) REFERENCES Locality (postalId, city)
);

-- * Human resources tables --

CREATE TABLE Employee
(
    id_                INT AUTO_INCREMENT PRIMARY KEY,
    firstname          VARCHAR(255)   NOT NULL,
    lastname           VARCHAR(255)   NOT NULL,
    email              VARCHAR(255)   NOT NULL UNIQUE,
    phoneNumber        VARCHAR(20)    NOT NULL UNIQUE,
    iban               VARCHAR(34)    NOT NULL UNIQUE,
    hourlyWage         DECIMAL(10, 2) NOT NULL,
    nbHoursPlannedWeek INT            NOT NULL,
    hiringDate         DATE           NOT NULL,
    nbPaidDaysHalfDay  INT            NOT NULL,
    pwd                VARCHAR(255)   NOT NULL,
    addressId          INT            NOT NULL,
    managerId          INT,

    FOREIGN KEY (addressId) REFERENCES Address_ (id_),
    FOREIGN KEY (managerId) REFERENCES Employee (id_)
);

CREATE TABLE Absence_type
(
    id_   INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Absence
(
    id_           INT AUTO_INCREMENT PRIMARY KEY,
    startDate     DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate       DATE,
    description_  VARCHAR(255),
    employeeId    INT  NOT NULL,
    absenceTypeId INT  NOT NULL,

    FOREIGN KEY (employeeId) REFERENCES Employee (id_),
    FOREIGN KEY (absenceTypeId) REFERENCES Absence_type (id_),
    UNIQUE (employeeId, startDate, endDate),
    CHECK (endDate IS NULL OR endDate >= startDate)
);

CREATE TABLE Role_
(
    id_   INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Position_
(
    roleId     INT NOT NULL,
    employeeId INT NOT NULL,

    PRIMARY KEY (employeeId, roleId),
    FOREIGN KEY (roleId) REFERENCES Role_ (id_),
    FOREIGN KEY (employeeId) REFERENCES Employee (id_)
);

CREATE TABLE Pointing
(
    date_      DATE NOT NULL DEFAULT (CURRENT_DATE),
    startTime  TIME NOT NULL DEFAULT (CURRENT_TIME),
    endTime    TIME,
    employeeId INT  NOT NULL,

    PRIMARY KEY (date_, employeeId),
    FOREIGN KEY (employeeId) REFERENCES Employee (id_),
    CHECK (endTime IS NULL OR endTime > startTime)
);

-- * Stock management tables --

CREATE TABLE ProductCategory
(
    id_   INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE Product
(
    id_              INT AUTO_INCREMENT PRIMARY KEY,
    name_            VARCHAR(255)   NOT NULL,
    priceEVAT        DECIMAL(10, 2) NOT NULL CHECK (priceEVAT >= 0),
    VAT              DECIMAL(5, 2)  NOT NULL CHECK (VAT >= 0 AND VAT <= 100),
    loyaltyPoints    INT            NOT NULL,
    isEdible         BOOLEAN        NOT NULL,
    minStockQuantity INT            NOT NULL CHECK (minStockQuantity >= 0),
    categoryId       INT            NOT NULL,

    FOREIGN KEY (categoryId) REFERENCES ProductCategory (id_)
);

CREATE TABLE LocationProduct
(
    shelf     VARCHAR(255) NOT NULL,
    floor_    INT          NOT NULL,
    isStock   BOOLEAN      NOT NULL,
    isFreezer BOOLEAN      NOT NULL,
    PRIMARY KEY (shelf, floor_, isStock)
);

CREATE TABLE QuantityProduct
(
    shelf     VARCHAR(255) NOT NULL,
    floor_    INT          NOT NULL,
    isStock   BOOLEAN      NOT NULL,
    productId INT          NOT NULL,
    quantity  INT          NOT NULL CHECK (quantity >= 0),

    PRIMARY KEY (shelf, floor_, isStock, productId),
    FOREIGN KEY (shelf, floor_, isStock) REFERENCES LocationProduct (shelf, floor_, isStock),
    FOREIGN KEY (productId) REFERENCES Product (id_)
);

CREATE TABLE Discount
(
    discountPercentage DECIMAL(5, 2) NOT NULL CHECK (discountPercentage BETWEEN 0 AND 100),
    requiredQuantity   INT           NOT NULL CHECK (requiredQuantity >= 1),
    startDate          DATE          NOT NULL DEFAULT (CURRENT_DATE),
    endDate            DATE          NOT NULL,
    name_              VARCHAR(255)  NOT NULL,
    productId          INT           NOT NULL,

    PRIMARY KEY (startDate, endDate, requiredQuantity, discountPercentage),
    FOREIGN KEY (productId) REFERENCES Product (id_),
    CHECK (endDate >= startDate)
);

-- * Workflow and document management tables --

CREATE TABLE Client_supplier
(
    id_              INT AUTO_INCREMENT PRIMARY KEY,
    name_            VARCHAR(255) NOT NULL,
    firstname        VARCHAR(255),
    email            VARCHAR(255) NOT NULL,
    phoneNumber      VARCHAR(20)  NOT NULL,
    isClient         BOOLEAN      NOT NULL,
    isSupplier       BOOLEAN      NOT NULL,
    isUs             BOOLEAN      NOT NULL,
    VATNumber        VARCHAR(50),
    dateBecameClient DATE,

    addressId        INT          NOT NULL,
    FOREIGN KEY (addressId) REFERENCES Address_ (id_)
);

CREATE TABLE FidelityCard
(
    id_      INT AUTO_INCREMENT PRIMARY KEY,
    points   INT     NOT NULL CHECK (points >= 0) DEFAULT 0,
    isValid  BOOLEAN NOT NULL                     DEFAULT TRUE,

    clientId INT     NOT NULL,
    FOREIGN KEY (clientId) REFERENCES Client_supplier (id_)
);

CREATE TABLE WorkFlowType
(
    id_        INT AUTO_INCREMENT PRIMARY KEY,
    name_      VARCHAR(255) NOT NULL UNIQUE,
    isBuy      BOOLEAN      NOT NULL,
    isSupplier BOOLEAN      NOT NULL,
    isInternal BOOLEAN      NOT NULL
);

-- ? Add field to specify the next status
CREATE TABLE Status_
(
    name_ VARCHAR(255) PRIMARY KEY
);

CREATE TABLE WorkFlow
(
    id_            INT AUTO_INCREMENT PRIMARY KEY,

    workFlowTypeId INT          NOT NULL,
    statusId       VARCHAR(255) NOT NULL,
    usId           INT          NOT NULL,
    otherId        INT,
    FOREIGN KEY (workFlowTypeId) REFERENCES WorkFlowType (id_),
    FOREIGN KEY (usId) REFERENCES Client_supplier (id_),
    FOREIGN KEY (otherId) REFERENCES Client_supplier (id_),
    FOREIGN KEY (statusId) REFERENCES Status_ (name_)
);

CREATE TABLE DocumentType
(
    id_   INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Document_
(
    id_                  INT AUTO_INCREMENT PRIMARY KEY,
    date_                DATE    NOT NULL DEFAULT (CURRENT_DATE),
    plannedSendingDate   DATE,
    plannedReceiveDate   DATE,
    effectiveSendingDate DATE,
    effectiveReceiveDate DATE,
    paymentDelay         INT     NOT NULL,
    commentary           VARCHAR(255),
    isChecked            BOOLEAN NOT NULL,

    workflowId           INT     NOT NULL,
    documentTypeId       INT     NOT NULL,
    addressId            INT,
    FOREIGN KEY (documentTypeId) REFERENCES DocumentType (id_),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow (id_),
    FOREIGN KEY (addressId) REFERENCES Address_ (id_)
);

CREATE TABLE WorkFlowDocument
(
    workflowId INT NOT NULL,
    documentId INT NOT NULL,

    PRIMARY KEY (workflowId, documentId),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow (id_),
    FOREIGN KEY (documentId) REFERENCES Document_ (id_)
);

CREATE TABLE Recipe
(
    id_            INT AUTO_INCREMENT PRIMARY KEY,
    name_          VARCHAR(255) NOT NULL UNIQUE,
    instructions   TEXT         NOT NULL,
    finalProductId INT          NOT NULL,
    FOREIGN KEY (finalProductId) REFERENCES Product (id_)
);

CREATE TABLE RecipeComposition
(
    recipeId  INT NOT NULL,
    productId INT NOT NULL,
    quantity  INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (recipeId, productId),
    FOREIGN KEY (recipeId) REFERENCES Recipe (id_),
    FOREIGN KEY (productId) REFERENCES Product (id_)
);

CREATE TABLE PreparationOrder
(
    id_        INT AUTO_INCREMENT PRIMARY KEY,
    documentId INT NOT NULL,
    recipeId   INT NOT NULL,
    FOREIGN KEY (documentId) REFERENCES Document_ (id_),
    FOREIGN KEY (recipeId) REFERENCES Recipe (id_),
    UNIQUE (documentId, recipeId)
);

CREATE TABLE Detail
(
    id_                  INT AUTO_INCREMENT PRIMARY KEY,
    documentId           INT            NOT NULL,
    productId            INT            NOT NULL,
    quantity             INT            NOT NULL CHECK (quantity > 0),
    priceVAT             DECIMAL(10, 2) NOT NULL CHECK (priceVAT >= 0),
    VAT                  DECIMAL(5, 2)  NOT NULL CHECK (VAT >= 0 AND VAT <= 100),
    fidelityPointsEarned INT            NOT NULL CHECK (fidelityPointsEarned >= 0),
    FOREIGN KEY (productId) REFERENCES Product (id_),
    FOREIGN KEY (documentId) REFERENCES Document_ (id_),
    UNIQUE (documentId, productId)
);

CREATE TABLE Batch
(
    id_            INT AUTO_INCREMENT,
    detailId       INT          NOT NULL,
    productId      INT          NOT NULL,
    expirationDate DATE,
    originCountry  VARCHAR(255) NOT NULL,
    PRIMARY KEY (detailId, productId, id_),
    KEY (id_),
    FOREIGN KEY (detailId) REFERENCES Detail (id_),
    FOREIGN KEY (productId) REFERENCES Product (id_)
);

-- Triggers --

/**
 * Trigger notation:
 * tgr_{CRUD}_{Target table}_{Trigger name or purpose}

 * This trigger ensures that all document created respect there respective
 * requirements regarding optional fields.


 * CRUD codes:
 *   C  = INSERT only
 *   U  = UPDATE only
 *   D  = DELETE only
 *   CU = INSERT + UPDATE
 */


CREATE TRIGGER tgr_C_Document_IntegrityCheck
    BEFORE INSERT
    ON Document_
    FOR EACH ROW
BEGIN
    DECLARE documentTypeName VARCHAR(255);
    SELECT name_
    INTO documentTypeName
    FROM DocumentType
    WHERE id_ = NEW.documentTypeId;

    IF documentTypeName = 'Purchase Order'
        AND (
           NEW.plannedSendingDate IS NULL
               OR NEW.plannedSendingDate < CURRENT_DATE
               OR NEW.plannedReceiveDate IS NULL
               OR NEW.plannedReceiveDate < NEW.plannedSendingDate
               OR NEW.paymentDelay < 0
           )
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid planned dates or payment delay for Purchase Order';

    ELSEIF documentTypeName = 'Delivery Note'
        AND NEW.commentary IS NOT NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Delivery Note must not have a commentary';

    ELSEIF documentTypeName = 'Preparation Order'
        AND NEW.commentary IS NULL
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Preparation Order must have a commentary';
    END IF;
END
CREATE TRIGGER tgr_CU_Batch_CheckExpirationDateRequirement
BEFORE
INSERT ON Batch FOR EACH ROW
BEGIN DECLARE productIsEdible BOOLEAN;
SELECT isEdible
INTO productIsEdible
FROM Product
WHERE id_ = NEW.productId;

IF productIsEdible
       AND (NEW.expirationDate IS NULL OR NEW.expirationDate <= CURRENT_DATE)
    THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Edible products must have a valid future expiration date';
END IF;
END


CREATE TRIGGER tgr_U_Batch_CheckExpirationDateRequirement
    BEFORE UPDATE
    ON Batch
    FOR EACH ROW
BEGIN
    DECLARE productIsEdible BOOLEAN;
    SELECT isEdible INTO productIsEdible FROM Product WHERE id_ = NEW.productId;

    IF productIsEdible
        AND (NEW.expirationDate IS NULL OR NEW.expirationDate <= CURRENT_DATE)
    THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Edible products must have a valid future expiration date';
    END IF;
END
DELIMITER $$
CREATE TRIGGER tgr_CU_ClientSupplier_CheckIsClientIsSupplier
    BEFORE INSERT
    ON Client_supplier
    FOR EACH ROW
BEGIN
    IF NEW.isUs = TRUE AND (NEW.isClient = TRUE OR NEW.isSupplier = TRUE) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'US can not be a client nor a supplier';

    ELSEIF NEW.isClient = FALSE AND NEW.isSupplier = FALSE AND NEW.isUs = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A client/supplier must be either a client or a supplier';
    END IF;
END
CREATE TRIGGER tgr_U_ClientSupplier_CheckIsClientIsSupplier
BEFORE
UPDATE ON Client_supplier
    FOR EACH ROW
BEGIN IF NEW.isUs = TRUE AND (NEW.isClient = TRUE OR NEW.isSupplier = TRUE) THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'US can not be a client nor a supplier';

ELSEIF NEW.isClient = FALSE AND NEW.isSupplier = FALSE AND NEW.isUs = FALSE THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A client/supplier must be either a client or a supplier';
END IF;
END


CREATE TRIGGER tgr_C_FidelityCard_CheckIsClient
    BEFORE INSERT
    ON FidelityCard
    FOR EACH ROW
BEGIN
    DECLARE clientIsClient BOOLEAN;
    SELECT isClient
    INTO clientIsClient
    FROM Client_supplier
    WHERE id_ = NEW.clientId;

    IF clientIsClient = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A fidelity card can only be assigned to a client';
    END IF;
END
CREATE TRIGGER tgr_U_FidelityCard_CheckIsClient
BEFORE
UPDATE ON FidelityCard
    FOR EACH ROW
BEGIN DECLARE clientIsClient BOOLEAN;
SELECT isClient
INTO clientIsClient
FROM Client_supplier
WHERE id_ = NEW.clientId;

IF clientIsClient = FALSE THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A fidelity card can only be assigned to a client';
END IF;
END


CREATE TRIGGER tgr_C_Employee_CheckNoSelfManager
    BEFORE INSERT
    ON Employee
    FOR EACH ROW
BEGIN
    IF NEW.managerId IS NOT NULL AND NEW.managerId = NEW.id_ THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'An employee cannot be their own manager';
    END IF;
END
CREATE TRIGGER tgr_U_Employee_CheckNoSelfManager
BEFORE
UPDATE ON Employee
    FOR EACH ROW
BEGIN IF NEW.managerId IS NOT NULL AND NEW.managerId = NEW.id_ THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'An employee cannot be their own manager';
END IF;
END



CREATE TRIGGER tgr_C_Absence_CheckPaidDaysBalance
    BEFORE INSERT
    ON Absence
    FOR EACH ROW
BEGIN
    DECLARE absenceTypeName VARCHAR(255);
    DECLARE remainingDays INT;
    DECLARE durationHalfDays INT;

    SELECT name_
    INTO absenceTypeName
    FROM Absence_type
    WHERE id_ = NEW.absenceTypeId;

    IF absenceTypeName = 'Paid Leave' THEN
        SELECT nbPaidDaysHalfDay
        INTO remainingDays
        FROM Employee
        WHERE id_ = NEW.employeeId;

        -- Calcul en demi-journées : DATEDIFF * 2
        IF NEW.endDate IS NOT NULL THEN
            SET durationHalfDays = (DATEDIFF(NEW.endDate, NEW.startDate) + 1) * 2;
        ELSE
            SET durationHalfDays = 2; -- absence d'une journée (demi-journée par défaut)
        END IF;

        IF remainingDays < durationHalfDays THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Insufficient paid leave balance for this employee';
        END IF;

        -- Décrémenter le solde
        UPDATE Employee
        SET nbPaidDaysHalfDay = nbPaidDaysHalfDay - durationHalfDays
        WHERE id_ = NEW.employeeId;
    END IF;
END
CREATE TRIGGER tgr_C_Discount_CheckNoOverlap
BEFORE
INSERT ON Discount FOR EACH ROW
BEGIN DECLARE overlapCount INT;

SELECT COUNT(*)
INTO overlapCount
FROM Discount
WHERE productId = NEW.productId
  AND NEW.startDate <= endDate
  AND NEW.endDate >= startDate;

IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A discount already exists for this product during the requested period';
END IF;
END



CREATE TRIGGER tgr_U_Discount_CheckNoOverlap
    BEFORE UPDATE
    ON Discount
    FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;

    SELECT COUNT(*)
    INTO overlapCount
    FROM Discount
    WHERE productId = NEW.productId
      AND NEW.startDate <= endDate
      AND NEW.endDate >= startDate
      -- Exclure la ligne en cours de modification
      AND NOT (startDate = OLD.startDate
        AND endDate = OLD.endDate
        AND requiredQuantity = OLD.requiredQuantity
        AND discountPercentage = OLD.discountPercentage);

    IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A discount already exists for this product during the requested period';
    END IF;
END
CREATE TRIGGER tgr_C_RecipeComposition_CheckNoSelfReference
BEFORE
INSERT ON RecipeComposition FOR EACH ROW
BEGIN DECLARE finalProduct INT;
SELECT finalProductId
INTO finalProduct
FROM Recipe
WHERE id_ = NEW.recipeId;

IF NEW.productId = finalProduct THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'A recipe cannot contain its own final product as an ingredient';
END IF;
END



CREATE TRIGGER tgr_U_RecipeComposition_CheckNoSelfReference
    BEFORE UPDATE
    ON RecipeComposition
    FOR EACH ROW
BEGIN
    DECLARE finalProduct INT;
    SELECT finalProductId INTO finalProduct FROM Recipe WHERE id_ = NEW.recipeId;

    IF NEW.productId = finalProduct THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A recipe cannot contain its own final product as an ingredient';
    END IF;
END
CREATE TRIGGER tgr_C_Pointing_CheckNoOverlap
BEFORE
INSERT ON Pointing FOR EACH ROW
BEGIN DECLARE overlapCount INT;

SELECT COUNT(*)
INTO overlapCount
FROM Pointing
WHERE employeeId = NEW.employeeId
  AND date_ = NEW.date_
  AND endTime IS NOT NULL
  AND NEW.startTime < endTime
  AND (NEW.endTime IS NULL OR NEW.endTime > startTime);

IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
SET MESSAGE_TEXT = 'Overlapping time entry for this employee on this day';
END IF;
END



CREATE TRIGGER tgr_U_Pointing_CheckNoOverlap
    BEFORE UPDATE
    ON Pointing
    FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;

    SELECT COUNT(*)
    INTO overlapCount
    FROM Pointing
    WHERE employeeId = NEW.employeeId
      AND date_ = NEW.date_
      AND endTime IS NOT NULL
      AND NEW.startTime < endTime
      AND (NEW.endTime IS NULL OR NEW.endTime > startTime)
      -- Exclure la ligne en cours de modification
      AND NOT (date_ = OLD.date_ AND employeeId = OLD.employeeId);

    IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Overlapping time entry for this employee on this day';
    END IF;
END
-- STORED INDEXES --

/**
 * Stored index notation:
 * idx_{Target table}_{Indexed column(s)}_{Purpose}
*/

-- TODO: Check if it create a specific sorted table or a reference to all row ids of the target table.
/**
 * Easily find a supplier
*/
CREATE INDEX idx_ClientSupplier_isSupplier ON Client_supplier(isSupplier);

-- VIEWS --

/**
 * View notation:
 * vw_{Purpose}_{Joined target table(s)}
*/

/**
 * Easy link between a product and it's suppliers
*/
CREATE VIEW vw_ProductSuppliers AS
SELECT p.id_ AS productId, p.name_ AS productLabel, cs.id_ AS supplierId, cs.name_ AS supplierName
FROM Product p,
     idx_ClientSupplier_isSupplier s,
     Document_ d,
     WorkFlow w,
     WorkFlowType wt,
     Detail dt
WHERE p.id_ = dt.productId
  AND dt.documentId = d.id_
  AND d.workflowId = w.id_
  AND w.workFlowTypeId = wt.id_
  AND wt.isSupplier = TRUE
  AND w.otherId = s.id_;

CREATE VIEW vw_LowQuantity_ProductSupplier AS
SELECT v.productId as productID, v.supplierId as supplierId
FROM vw_ProductSuppliers v,
     Product p
WHERE v.productId = p.id_
  AND p.minStockQuantity * 1.1 >= (SELECT SUM(q.quantity)
                                   FROM QuantityProduct q
                                   WHERE p.id_ = q.productId)
ORDER BY v.supplierId;

-- Insert
INSERT INTO ProductCategory (id_, name_)
VALUES (1, 'Household'),
       (2, 'Home Appliances'),
       (3, 'Fruits & Vegetables'),
       (4, 'Meat & Poultry'),
       (5, 'Dairy Products'),
       (6, 'Bakery & Pastry'),
       (7, 'Frozen Foods'),
       (8, 'Beverages'),
       (9, 'Alcoholic Beverages'),
       (10, 'Snacks & Confectionery'),
       (11, 'Canned & Preserved Foods'),
       (12, 'Pasta, Rice & Cereals'),
       (13, 'Condiments & Sauces'),
       (14, 'Spices & Herbs'),
       (15, 'Seafood'),
       (16, 'Deli & Charcuterie'),
       (17, 'Baby Products'),
       (18, 'Personal Care'),
       (19, 'Health & Pharmacy'),
       (20, 'Cleaning Supplies'),
       (21, 'Pet Supplies'),
       (22, 'Stationery & Office'),
       (23, 'Toys & Games'),
       (24, 'Clothing & Apparel'),
       (25, 'Sports & Outdoors'),
       (26, 'Electronics'),
       (27, 'Tools & Hardware'),
       (28, 'Garden & Plants'),
       (29, 'Automotive'),
       (30, 'Books & Magazines');

INSERT INTO Product (id_, name_, priceEVAT, VAT, loyaltyPoints, isEdible, minStockQuantity, categoryId)
VALUES
-- Household (1)
(1, 'Mop & Bucket Set', 12.99, 21.00, 5, FALSE, 10, 1),
(2, 'Laundry Basket', 8.49, 21.00, 3, FALSE, 15, 1),
(3, 'Ironing Board', 24.99, 21.00, 10, FALSE, 5, 1),
-- Home Appliances (2)
(4, 'Microwave Oven 800W', 89.99, 21.00, 40, FALSE, 3, 2),
(5, 'Vacuum Cleaner', 119.99, 21.00, 55, FALSE, 3, 2),
(6, 'Electric Kettle', 29.99, 21.00, 12, FALSE, 8, 2),
(7, 'Toaster 2-Slice', 19.99, 21.00, 8, FALSE, 8, 2),
-- Fruits & Vegetables (3)
(8, 'Banana 1kg', 1.29, 6.00, 1, TRUE, 50, 3),
(9, 'Cherry Tomatoes 500g', 2.49, 6.00, 1, TRUE, 40, 3),
(10, 'Broccoli', 1.99, 6.00, 1, TRUE, 30, 3),
(11, 'Golden Apple 4-pack', 2.99, 6.00, 1, TRUE, 40, 3),
-- Meat & Poultry (4)
(12, 'Chicken Breast 500g', 5.99, 6.00, 3, TRUE, 20, 4),
(13, 'Ground Beef 400g', 6.49, 6.00, 3, TRUE, 20, 4),
(14, 'Pork Ribs 1kg', 8.99, 6.00, 4, TRUE, 15, 4),
-- Dairy Products (5)
(15, 'Whole Milk 1L', 1.09, 6.00, 1, TRUE, 60, 5),
(16, 'Butter 250g', 2.29, 6.00, 1, TRUE, 40, 5),
(17, 'Gouda Cheese 400g', 3.99, 6.00, 2, TRUE, 30, 5),
(18, 'Greek Yogurt 500g', 2.49, 6.00, 1, TRUE, 35, 5),
-- Bakery & Pastry (6)
(19, 'White Sandwich Bread', 1.89, 6.00, 1, TRUE, 30, 6),
(20, 'Croissant x6', 3.49, 6.00, 2, TRUE, 25, 6),
(21, 'Chocolate Muffin', 1.29, 6.00, 1, TRUE, 40, 6),
-- Frozen Foods (7)
(22, 'Frozen French Fries 1kg', 2.99, 6.00, 2, TRUE, 25, 7),
(23, 'Frozen Margherita Pizza', 4.49, 6.00, 2, TRUE, 20, 7),
(24, 'Frozen Spinach 750g', 2.19, 6.00, 1, TRUE, 20, 7),
-- Beverages (8)
(25, 'Still Water 6x1.5L', 3.29, 6.00, 2, TRUE, 40, 8),
(26, 'Orange Juice 1L', 2.49, 6.00, 1, TRUE, 35, 8),
(27, 'Coca-Cola 1.5L', 2.29, 6.00, 1, TRUE, 40, 8),
-- Alcoholic Beverages (9)
(28, 'Blonde Beer 6x33cl', 7.99, 21.00, 4, TRUE, 20, 9),
(29, 'Red Wine 75cl', 9.99, 21.00, 5, TRUE, 15, 9),
(30, 'White Wine 75cl', 8.49, 21.00, 4, TRUE, 15, 9),
-- Snacks & Confectionery (10)
(31, 'Salted Crisps 200g', 1.99, 6.00, 1, TRUE, 50, 10),
(32, 'Dark Chocolate Bar 100g', 1.79, 6.00, 1, TRUE, 50, 10),
(33, 'Gummy Bears 250g', 1.49, 6.00, 1, TRUE, 45, 10),
-- Canned & Preserved Foods (11)
(34, 'Canned Tuna in Water 160g', 1.59, 6.00, 1, TRUE, 40, 11),
(35, 'Canned Chickpeas 400g', 0.99, 6.00, 1, TRUE, 40, 11),
(36, 'Tomato Paste 200g', 0.89, 6.00, 1, TRUE, 35, 11),
-- Pasta, Rice & Cereals (12)
(37, 'Spaghetti 500g', 1.29, 6.00, 1, TRUE, 50, 12),
(38, 'Basmati Rice 1kg', 2.49, 6.00, 1, TRUE, 40, 12),
(39, 'Corn Flakes 375g', 2.99, 6.00, 1, TRUE, 30, 12),
-- Condiments & Sauces (13)
(40, 'Ketchup 500ml', 2.19, 6.00, 1, TRUE, 30, 13),
(41, 'Mayonnaise 400ml', 2.49, 6.00, 1, TRUE, 30, 13),
(42, 'Soy Sauce 150ml', 1.99, 6.00, 1, TRUE, 25, 13),
-- Spices & Herbs (14)
(43, 'Ground Black Pepper 50g', 1.49, 6.00, 1, TRUE, 30, 14),
(44, 'Paprika Powder 40g', 1.29, 6.00, 1, TRUE, 30, 14),
(45, 'Dried Oregano 15g', 0.99, 6.00, 1, TRUE, 30, 14),
-- Seafood (15)
(46, 'Atlantic Salmon Fillet 300g', 7.99, 6.00, 4, TRUE, 15, 15),
(47, 'Cooked Shrimps 200g', 5.49, 6.00, 3, TRUE, 15, 15),
(48, 'Cod Fillet 400g', 6.99, 6.00, 3, TRUE, 15, 15),
-- Deli & Charcuterie (16)
(49, 'Sliced Ham 150g', 2.99, 6.00, 2, TRUE, 20, 16),
(50, 'Salami 100g', 3.49, 6.00, 2, TRUE, 20, 16),
(51, 'Smoked Salmon 100g', 4.99, 6.00, 3, TRUE, 15, 16),
-- Baby Products (17)
(52, 'Baby Diapers Size 3 x44', 14.99, 21.00, 7, FALSE, 10, 17),
(53, 'Baby Wipes x72', 3.49, 21.00, 2, FALSE, 20, 17),
(54, 'Infant Formula 800g', 19.99, 6.00, 9, TRUE, 10, 17),
-- Personal Care (18)
(55, 'Shampoo 400ml', 4.49, 21.00, 2, FALSE, 20, 18),
(56, 'Toothpaste 75ml', 2.29, 21.00, 1, FALSE, 25, 18),
(57, 'Shower Gel 250ml', 3.19, 21.00, 2, FALSE, 20, 18),
(58, 'Deodorant Roll-On 50ml', 3.49, 21.00, 2, FALSE, 20, 18),
-- Health & Pharmacy (19)
(59, 'Paracetamol 500mg x20', 3.99, 21.00, 2, FALSE, 15, 19),
(60, 'Vitamin C 1000mg x30', 7.49, 21.00, 4, FALSE, 10, 19),
(61, 'Adhesive Bandages x20', 2.49, 21.00, 1, FALSE, 15, 19),
-- Cleaning Supplies (20)
(62, 'All-Purpose Cleaner 1L', 3.29, 21.00, 2, FALSE, 20, 20),
(63, 'Dishwasher Tablets x30', 7.99, 21.00, 4, FALSE, 15, 20),
(64, 'Toilet Cleaner 750ml', 2.49, 21.00, 1, FALSE, 20, 20),
-- Pet Supplies (21)
(65, 'Dry Dog Food 3kg', 14.99, 21.00, 7, FALSE, 10, 21),
(66, 'Dry Cat Food 2kg', 11.99, 21.00, 6, FALSE, 10, 21),
(67, 'Cat Litter 5L', 5.99, 21.00, 3, FALSE, 10, 21),
-- Stationery & Office (22)
(68, 'Ballpoint Pens x10', 3.49, 21.00, 2, FALSE, 20, 22),
(69, 'A4 Paper 500 sheets', 5.99, 21.00, 3, FALSE, 15, 22),
(70, 'Sticky Notes x100', 2.29, 21.00, 1, FALSE, 20, 22),
-- Toys & Games (23)
(71, 'Building Blocks Set', 19.99, 21.00, 10, FALSE, 8, 23),
(72, 'Puzzle 500 pieces', 12.99, 21.00, 6, FALSE, 8, 23),
(73, 'Playing Cards', 3.99, 21.00, 2, FALSE, 15, 23),
-- Clothing & Apparel (24)
(74, 'Cotton T-Shirt', 12.99, 21.00, 6, FALSE, 10, 24),
(75, 'Winter Scarf', 9.99, 21.00, 5, FALSE, 10, 24),
(76, 'Sports Socks x3', 6.49, 21.00, 3, FALSE, 15, 24),
-- Sports & Outdoors (25)
(77, 'Yoga Mat', 24.99, 21.00, 12, FALSE, 5, 25),
(78, 'Jump Rope', 7.99, 21.00, 4, FALSE, 10, 25),
(79, 'Water Bottle 750ml', 9.99, 21.00, 5, FALSE, 10, 25),
-- Electronics (26)
(80, 'USB-C Charging Cable 1m', 8.99, 21.00, 4, FALSE, 15, 26),
(81, 'Wireless Earbuds', 49.99, 21.00, 25, FALSE, 5, 26),
(82, 'Power Bank 10000mAh', 34.99, 21.00, 17, FALSE, 5, 26),
-- Tools & Hardware (27)
(83, 'Hammer 500g', 12.99, 21.00, 6, FALSE, 8, 27),
(84, 'Screwdriver Set x8', 15.99, 21.00, 8, FALSE, 8, 27),
(85, 'Measuring Tape 5m', 6.49, 21.00, 3, FALSE, 10, 27),
-- Garden & Plants (28)
(86, 'Potting Soil 10L', 6.99, 21.00, 3, FALSE, 10, 28),
(87, 'Garden Gloves', 4.99, 21.00, 2, FALSE, 12, 28),
(88, 'Basil Plant', 2.49, 6.00, 1, TRUE, 20, 28),
-- Automotive (29)
(89, 'Car Air Freshener', 3.49, 21.00, 2, FALSE, 15, 29),
(90, 'Windshield Washer Fluid 1L', 4.99, 21.00, 2, FALSE, 10, 29),
(91, 'Microfiber Car Cloth x3', 7.99, 21.00, 4, FALSE, 10, 29),
-- Books & Magazines (30)
(92, 'Weekly TV Guide', 2.99, 21.00, 1, FALSE, 20, 30),
(93, 'Cooking Recipe Book', 16.99, 21.00, 8, FALSE, 8, 30),
(94, 'Children Story Book', 9.99, 21.00, 5, FALSE, 10, 30),
-- Plat prerpa pour rectte
(95, 'Spaghetti Bolognese (préparé)', 8.99, 6.00, 5, TRUE, 5, 4),
(96, 'Salmon with Rice and Soy Glaze (préparé)', 10.99, 6.00, 6, TRUE, 5, 15),
(97, 'Tuna and Chickpea Salad (préparé)', 6.99, 6.00, 4, TRUE, 5, 11),
(98, 'Croissant Ham and Cheese Sandwich (préparé)', 5.49, 6.00, 3, TRUE, 5, 6),
(99, 'Greek Yogurt Banana Bowl (préparé)', 4.99, 6.00, 3, TRUE, 5, 5),
(100, 'Paprika Chicken with Frozen Fries (préparé)', 9.49, 6.00, 5, TRUE, 5, 4),
(101, 'Cod with Tomato Sauce and Rice (préparé)', 9.99, 6.00, 5, TRUE, 5, 15);


INSERT INTO LocationProduct (shelf, floor_, isStock, isFreezer)
VALUES ('A-a', 1, TRUE, FALSE),
       ('A-a', 2, TRUE, FALSE),
       ('A-a', 3, TRUE, TRUE),
       ('A-b', 1, TRUE, TRUE),
       ('B-a', 1, FALSE, FALSE),
       ('B-a', 2, FALSE, FALSE),
       ('B-a', 3, FALSE, TRUE),
       ('B-b', 1, FALSE, TRUE);

INSERT INTO QuantityProduct (shelf, floor_, isStock, productId, quantity)
VALUES ('A-a', 1, TRUE, 1, 12),
       ('A-a', 1, TRUE, 2, 18),
       ('A-a', 1, TRUE, 3, 7),
       ('A-a', 1, TRUE, 4, 4),
       ('A-a', 1, TRUE, 5, 4),
       ('A-a', 1, TRUE, 6, 10),
       ('A-a', 1, TRUE, 7, 10),
       ('A-a', 1, TRUE, 25, 30),
       ('A-a', 1, TRUE, 26, 40),
       ('A-a', 1, TRUE, 27, 45),
       ('A-a', 1, TRUE, 28, 25),
       ('A-a', 1, TRUE, 29, 18),
       ('A-a', 1, TRUE, 30, 16),
       ('A-a', 1, TRUE, 31, 55),
       ('A-a', 1, TRUE, 32, 60),
       ('A-a', 1, TRUE, 33, 50),
       ('A-a', 1, TRUE, 34, 45),
       ('A-a', 1, TRUE, 35, 45),
       ('A-a', 1, TRUE, 36, 40),
       ('A-a', 1, TRUE, 37, 55),
       ('A-a', 1, TRUE, 38, 45),
       ('A-a', 1, TRUE, 39, 35),
       ('A-a', 1, TRUE, 40, 35),
       ('A-a', 1, TRUE, 41, 35),
       ('A-a', 1, TRUE, 42, 28),
       ('A-a', 1, TRUE, 43, 35),
       ('A-a', 1, TRUE, 44, 32),
       ('A-a', 1, TRUE, 45, 34),

       ('A-a', 2, TRUE, 51, 25),
       ('A-a', 2, TRUE, 52, 28),
       ('A-a', 2, TRUE, 53, 12),
       ('A-a', 2, TRUE, 54, 25),
       ('A-a', 2, TRUE, 55, 30),
       ('A-a', 2, TRUE, 56, 24),
       ('A-a', 2, TRUE, 57, 22),
       ('A-a', 2, TRUE, 58, 18),
       ('A-a', 2, TRUE, 59, 12),
       ('A-a', 2, TRUE, 60, 18),
       ('A-a', 2, TRUE, 61, 24),
       ('A-a', 2, TRUE, 62, 18),
       ('A-a', 2, TRUE, 63, 22),
       ('A-a', 2, TRUE, 64, 12),
       ('A-a', 2, TRUE, 65, 12),
       ('A-a', 2, TRUE, 66, 14),
       ('A-a', 2, TRUE, 67, 22),
       ('A-a', 2, TRUE, 68, 18),
       ('A-a', 2, TRUE, 69, 22),
       ('A-a', 2, TRUE, 70, 10),
       ('A-a', 2, TRUE, 71, 10),
       ('A-a', 2, TRUE, 72, 18),
       ('A-a', 2, TRUE, 73, 12),
       ('A-a', 2, TRUE, 74, 12),
       ('A-a', 2, TRUE, 75, 18),
       ('A-a', 2, TRUE, 76, 6),
       ('A-a', 2, TRUE, 77, 12),
       ('A-a', 2, TRUE, 78, 12),
       ('A-a', 2, TRUE, 79, 18),
       ('A-a', 2, TRUE, 80, 6),
       ('A-a', 2, TRUE, 81, 6),
       ('A-a', 2, TRUE, 82, 10),
       ('A-a', 2, TRUE, 83, 10),
       ('A-a', 2, TRUE, 84, 12),
       ('A-a', 2, TRUE, 85, 12),
       ('A-a', 2, TRUE, 86, 14),
       ('A-a', 2, TRUE, 88, 18),
       ('A-a', 2, TRUE, 89, 12),
       ('A-a', 2, TRUE, 90, 12),
       ('A-a', 2, TRUE, 91, 22),
       ('A-a', 2, TRUE, 92, 10),
       ('A-a', 2, TRUE, 93, 12),

       ('A-a', 3, TRUE, 23, 30),
       ('A-a', 3, TRUE, 24, 25),
       ('A-a', 3, TRUE, 25, 22),

       ('A-b', 1, TRUE, 8, 60),
       ('A-b', 1, TRUE, 9, 48),
       ('A-b', 1, TRUE, 10, 36),
       ('A-b', 1, TRUE, 11, 44),
       ('A-b', 1, TRUE, 12, 24),
       ('A-b', 1, TRUE, 13, 24),
       ('A-b', 1, TRUE, 14, 18),
       ('A-b', 1, TRUE, 15, 70),
       ('A-b', 1, TRUE, 16, 48),
       ('A-b', 1, TRUE, 17, 36),
       ('A-b', 1, TRUE, 18, 40),
       ('A-b', 1, TRUE, 19, 36),
       ('A-b', 1, TRUE, 20, 30),
       ('A-b', 1, TRUE, 21, 48),
       ('A-b', 1, TRUE, 46, 18),
       ('A-b', 1, TRUE, 47, 18),
       ('A-b', 1, TRUE, 48, 18),
       ('A-b', 1, TRUE, 49, 24),
       ('A-b', 1, TRUE, 50, 22),
       ('A-b', 1, TRUE, 51, 18),
       ('A-b', 1, TRUE, 53, 14),
       ('A-b', 1, TRUE, 87, 24);

INSERT INTO QuantityProduct (shelf, floor_, isStock, productId, quantity)
VALUES ('B-a', 1, FALSE, 1, 10),
       ('B-a', 1, FALSE, 2, 15),
       ('B-a', 1, FALSE, 3, 5),
       ('B-a', 1, FALSE, 4, 3),
       ('B-a', 1, FALSE, 5, 3),
       ('B-a', 1, FALSE, 6, 8),
       ('B-a', 1, FALSE, 7, 8),
       ('B-a', 1, FALSE, 25, 25),
       ('B-a', 1, FALSE, 26, 30),
       ('B-a', 1, FALSE, 27, 35),
       ('B-a', 1, FALSE, 28, 18),
       ('B-a', 1, FALSE, 29, 15),
       ('B-a', 1, FALSE, 30, 15),
       ('B-a', 1, FALSE, 31, 50),
       ('B-a', 1, FALSE, 32, 50),
       ('B-a', 1, FALSE, 33, 45),
       ('B-a', 1, FALSE, 34, 40),
       ('B-a', 1, FALSE, 35, 40),
       ('B-a', 1, FALSE, 36, 35),
       ('B-a', 1, FALSE, 37, 50),
       ('B-a', 1, FALSE, 38, 40),
       ('B-a', 1, FALSE, 39, 30),
       ('B-a', 1, FALSE, 40, 30),
       ('B-a', 1, FALSE, 41, 30),
       ('B-a', 1, FALSE, 42, 25),
       ('B-a', 1, FALSE, 43, 30),
       ('B-a', 1, FALSE, 44, 30),
       ('B-a', 1, FALSE, 45, 30),

       ('B-a', 2, FALSE, 51, 20),
       ('B-a', 2, FALSE, 52, 22),
       ('B-a', 2, FALSE, 54, 20),
       ('B-a', 2, FALSE, 55, 25),
       ('B-a', 2, FALSE, 56, 20),
       ('B-a', 2, FALSE, 57, 20),
       ('B-a', 2, FALSE, 58, 15),
       ('B-a', 2, FALSE, 59, 10),
       ('B-a', 2, FALSE, 60, 15),
       ('B-a', 2, FALSE, 61, 20),
       ('B-a', 2, FALSE, 62, 15),
       ('B-a', 2, FALSE, 63, 20),
       ('B-a', 2, FALSE, 64, 10),
       ('B-a', 2, FALSE, 65, 10),
       ('B-a', 2, FALSE, 66, 10),
       ('B-a', 2, FALSE, 67, 20),
       ('B-a', 2, FALSE, 68, 15),
       ('B-a', 2, FALSE, 69, 20),
       ('B-a', 2, FALSE, 70, 8),
       ('B-a', 2, FALSE, 71, 8),
       ('B-a', 2, FALSE, 72, 15),
       ('B-a', 2, FALSE, 73, 10),
       ('B-a', 2, FALSE, 74, 10),
       ('B-a', 2, FALSE, 75, 15),
       ('B-a', 2, FALSE, 76, 5),
       ('B-a', 2, FALSE, 77, 10),
       ('B-a', 2, FALSE, 78, 10),
       ('B-a', 2, FALSE, 79, 15),
       ('B-a', 2, FALSE, 80, 5),
       ('B-a', 2, FALSE, 81, 5),
       ('B-a', 2, FALSE, 82, 8),
       ('B-a', 2, FALSE, 83, 8),
       ('B-a', 2, FALSE, 84, 10),
       ('B-a', 2, FALSE, 85, 10),
       ('B-a', 2, FALSE, 86, 12),
       ('B-a', 2, FALSE, 88, 15),
       ('B-a', 2, FALSE, 89, 10),
       ('B-a', 2, FALSE, 90, 10),
       ('B-a', 2, FALSE, 91, 20),
       ('B-a', 2, FALSE, 92, 8),
       ('B-a', 2, FALSE, 93, 10),

       ('B-a', 3, FALSE, 23, 22),
       ('B-a', 3, FALSE, 24, 18),
       ('B-a', 3, FALSE, 25, 18),

       ('B-b', 1, FALSE, 8, 50),
       ('B-b', 1, FALSE, 9, 40),
       ('B-b', 1, FALSE, 10, 30),
       ('B-b', 1, FALSE, 11, 40),
       ('B-b', 1, FALSE, 12, 20),
       ('B-b', 1, FALSE, 13, 20),
       ('B-b', 1, FALSE, 14, 15),
       ('B-b', 1, FALSE, 15, 60),
       ('B-b', 1, FALSE, 16, 40),
       ('B-b', 1, FALSE, 17, 30),
       ('B-b', 1, FALSE, 18, 35),
       ('B-b', 1, FALSE, 19, 30),
       ('B-b', 1, FALSE, 20, 25),
       ('B-b', 1, FALSE, 21, 40),
       ('B-b', 1, FALSE, 46, 15),
       ('B-b', 1, FALSE, 47, 15),
       ('B-b', 1, FALSE, 48, 15),
       ('B-b', 1, FALSE, 49, 20),
       ('B-b', 1, FALSE, 50, 20),
       ('B-b', 1, FALSE, 87, 20);

-- Discount passé
INSERT INTO Discount (discountPercentage, requiredQuantity, startDate, endDate, name_, productId)
VALUES (20.00, 1, '2023-12-20', '2024-01-02', 'Promo Fêtes', 29),
       (15.00, 1, '2023-12-20', '2024-01-02', 'Promo Fêtes', 30),
       (10.00, 3, '2023-12-20', '2024-01-02', 'Promo Fêtes', 28),
       (25.00, 1, '2024-01-08', '2024-01-31', 'Soldes Hiver', 73),
       (30.00, 1, '2024-01-08', '2024-01-31', 'Soldes Hiver', 74),
       (20.00, 1, '2024-01-08', '2024-01-31', 'Soldes Hiver', 76),
       (15.00, 2, '2024-02-10', '2024-02-14', 'Saint-Valentin', 32),
       (10.00, 1, '2024-02-10', '2024-02-14', 'Saint-Valentin', 29),
       (10.00, 2, '2024-03-28', '2024-04-01', 'Offre Pâques', 32),
       (15.00, 3, '2024-03-28', '2024-04-01', 'Offre Pâques', 33),
       (20.00, 1, '2024-06-01', '2024-08-31', 'Été Fraîcheur', 25),
       (10.00, 2, '2024-06-01', '2024-08-31', 'Été Fraîcheur', 27),
       (15.00, 1, '2024-06-01', '2024-08-31', 'Été Fraîcheur', 26),
       (30.00, 1, '2024-11-29', '2024-12-01', 'Black Friday', 80),
       (25.00, 1, '2024-11-29', '2024-12-01', 'Black Friday', 81),
       (20.00, 1, '2024-11-29', '2024-12-01', 'Black Friday', 4),
       (15.00, 1, '2024-11-29', '2024-12-01', 'Black Friday', 5),
       (40.00, 1, '2025-01-15', '2025-01-31', 'Déstockage', 71),
       (35.00, 1, '2025-01-15', '2025-01-31', 'Déstockage', 70),
       (20.00, 5, '2025-01-15', '2025-01-31', 'Déstockage', 91);
-- Discount active
INSERT INTO Discount (discountPercentage, requiredQuantity, startDate, endDate, name_, productId)
VALUES (15.00, 2, '2025-05-01', '2025-06-15', 'Seasonal Vegetables', 10),
       (10.00, 3, '2025-05-01', '2025-06-15', 'Seasonal Vegetables', 9),
       (20.00, 1, '2025-05-26', '2025-06-08', 'Fish Week', 46),
       (15.00, 1, '2025-05-26', '2025-06-08', 'Fish Week', 48),
       (10.00, 2, '2025-05-26', '2025-06-08', 'Fish Week', 47),
       (10.00, 2, '2025-05-15', '2025-06-14', 'Hygiene Plus', 54),
       (12.00, 3, '2025-05-20', '2025-06-20', 'Grocery Bundle', 37),
       (10.00, 3, '2025-05-20', '2025-06-20', 'Grocery Bundle', 39),
       (18.00, 1, '2025-06-01', '2025-06-30', 'Tech June', 79),
       (22.00, 1, '2025-06-01', '2025-06-30', 'Tech June', 80),
       (15.00, 2, '2025-06-01', '2025-06-30', 'Happy Pets', 64),
       (20.00, 6, '2025-05-28', '2025-06-07', 'Weekend Snacks', 31),
       (15.00, 4, '2025-05-28', '2025-06-07', 'Weekend Snacks', 33);

-- Discount futur
INSERT INTO Discount (discountPercentage, requiredQuantity, startDate, endDate, name_, productId)
VALUES (30.00, 1, '2025-07-01', '2025-07-31', 'Summer Sale', 73),
       (25.00, 1, '2025-07-01', '2025-07-31', 'Summer Sale', 75),
       (35.00, 1, '2025-07-01', '2025-07-31', 'Summer Sale', 76),
       (20.00, 1, '2025-07-01', '2025-07-31', 'Summer Sale', 77),
       (21.00, 1, '2025-07-21', '2025-07-21', 'National Day', 28),
       (25.00, 1, '2025-07-21', '2025-07-21', 'National Day ', 29),
       (15.00, 2, '2025-08-20', '2025-09-15', 'Back to School', 67),
       (10.00, 2, '2025-08-20', '2025-09-15', 'Back to School', 68),
       (12.00, 3, '2025-08-20', '2025-09-15', 'Back to School', 69),
       (20.00, 1, '2025-08-20', '2025-09-15', 'Back to School', 92),
       (20.00, 1, '2025-09-01', '2025-09-30', 'Autumn Gardening', 85),
       (15.00, 1, '2025-09-01', '2025-09-30', 'Autumn Gardening', 86),
       (35.00, 1, '2025-11-28', '2025-11-30', 'Black Friday', 4),
       (40.00, 1, '2025-11-28', '2025-11-30', 'Black Friday', 5),
       (30.00, 1, '2025-11-28', '2025-11-30', 'Black Friday', 81),
       (20.00, 1, '2025-12-15', '2025-12-31', 'Christmas', 30),
       (15.00, 3, '2025-12-15', '2025-12-31', 'Christmas', 32),
       (10.00, 2, '2025-12-15', '2025-12-31', 'Christmas', 51);

INSERT INTO Locality (postalId, city)
VALUES (1000, 'Bruxelles'),
       (4000, 'Liège'),
       (2000, 'Antwerpen'),
       (9000, 'Gent'),
       (5000, 'Namur'),
       (3000, 'Leuven'),
       (6000, 'Charleroi'),
       (8000, 'Brugge'),
       (7000, 'Mons'),
       (4500, 'Huy'),
       (1300, 'Wavre'),
       (6700, 'Arlon'),
       (3500, 'Hasselt'),
       (8500, 'Kortrijk'),
       (5100, 'Jambes');

-- Addresses
INSERT INTO Address_ (streetName, streetNumber, postalId, city)
VALUES ('Rue de la Java', 1, 1000, 'Bruxelles'),
       ('Rue de la Loi', 42, 1000, 'Bruxelles'),
       ('Rue Féronstrée', 17, 4000, 'Liège'),
       ('Meir', 88, 2000, 'Antwerpen'),
       ('Veldstraat', 23, 9000, 'Gent'),
       ('Rue de Fer', 5, 5000, 'Namur'),
       ('Naamsestraat', 61, 3000, 'Leuven'),
       ('Rue de la Montagne', 33, 6000, 'Charleroi'),
       ('Steenstraat', 14, 8000, 'Brugge'),
       ('Grand-Rue', 7, 7000, 'Mons'),
       ('Rue des Alliés', 99, 4500, 'Huy'),
       ('Rue du Java', 67, 6700, 'Arlon'),
       ('Rue de la Station', 12, 1300, 'Wavre'),
       ('Rue des Martyrs', 45, 6700, 'Arlon'),
       ('Kleine Breemstraat', 8, 3500, 'Hasselt'),
       ('Beheerstraat', 77, 8500, 'Kortrijk'),
       ('Chaussée de Namur', 55, 5100, 'Jambes'),
       ('Rue du Commerce', 31, 1000, 'Bruxelles'),
       ('Boulevard d\'Avroy', 19, 4000, 'Liège'),
       ('Frankrijklei', 64, 2000, 'Antwerpen'),
       ('Korenmarkt', 3, 9000, 'Gent'),
       ('Avenue de Stassart', 28, 5000, 'Namur');

-- Magasin
INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, addressId)
VALUES ('Le grand Bazard', NULL, 'contact@magasin.be', '047123456789', false, false, true, 1);

-- Clients
INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('Dupont', 'Marie', 'marie.dupont@gmail.com', '+32470123456', TRUE, FALSE, FALSE, NULL, '2021-03-15', 2),
       ('Lejeune', 'Thomas', 'thomas.lejeune@hotmail.com', '+32478234567', TRUE, FALSE, FALSE, NULL, '2020-07-22', 3),
       ('Peeters', 'Sofie', 'sofie.peeters@outlook.com', '+32492345678', TRUE, FALSE, FALSE, NULL, '2019-11-05', 4),
       ('De Smet', 'Jonas', 'jonas.desmet@gmail.com', '+32456456789', TRUE, FALSE, FALSE, NULL, '2022-01-30', 5),
       ('Lambert', 'Isabelle', 'isabelle.lambert@yahoo.fr', '+32471567890', TRUE, FALSE, FALSE, NULL, '2018-06-10', 6),
       ('Maes', 'Pieter', 'pieter.maes@gmail.com', '+32493678901', TRUE, FALSE, FALSE, NULL, '2023-02-14', 7),
       ('Renard', 'Claire', 'claire.renard@proximus.be', '+32487789012', TRUE, FALSE, FALSE, NULL, '2021-09-03', 8),
       ('Claes', 'Bram', 'bram.claes@telenet.be', '+32465890123', TRUE, FALSE, FALSE, NULL, '2020-04-18', 9),
       ('Fontaine', 'Nathalie', 'nathalie.fontaine@gmail.com', '+32479901234', TRUE, FALSE, FALSE, NULL, '2017-12-01',
        10),
       ('Willems', 'Kevin', 'kevin.willems@hotmail.be', '+32468012345', TRUE, FALSE, FALSE, NULL, '2022-08-25', 11);

-- Fournisseurs
INSERT INTO Client_supplier (name_, firstname, email, phoneNumber, isClient, isSupplier, isUs, VATNumber,
                             dateBecameClient, addressId)
VALUES ('BioFresh SA', NULL, 'contact@biofresh.be', '+32(0)10441122', FALSE, TRUE, FALSE, 'BE0111222333', '2018-04-12',
        12),
       ('Métal Pro SPRL', NULL, 'info@metalpro.be', '+32(0)63881234', FALSE, TRUE, FALSE, 'BE0222333444', '2015-09-03',
        13),
       ('Limburg Supplies NV', NULL, 'orders@limburgsupplies.be', '+32(0)11223344', FALSE, TRUE, FALSE, 'BE0333444555',
        '2020-11-27', 14),
       ('TextilKort BV', NULL, 'verkoop@textilkort.be', '+32(0)56991234', FALSE, TRUE, FALSE, 'BE0444555666',
        '2017-06-15', 15),
       ('Namur Logistics SA', NULL, 'logistique@namurlog.be', '+32(0)81556677', FALSE, TRUE, FALSE, 'BE0555666777',
        '2019-02-08', 16),
       ('BruxTech SPRL', NULL, 'support@bruxtech.be', '+32(0)25678901', FALSE, TRUE, FALSE, 'BE0666777888',
        '2021-07-30', 17),
       ('LiègeChim SA', NULL, 'chimie@liegechim.be', '+32(0)43456789', FALSE, TRUE, FALSE, 'BE0777888999', '2016-03-22',
        18),
       ('AntwerpGoods NV', NULL, 'goods@antwerpgoods.be', '+32(0)33109988', FALSE, TRUE, FALSE, 'BE0888999000',
        '2022-10-05', 19),
       ('GentDistrib BVBA', NULL, 'distrib@gentdistrib.be', '+32(0)92345678', FALSE, TRUE, FALSE, 'BE0999000111',
        '2014-08-19', 20),
       ('SudBelge SA', NULL, 'contact@sudbelge.be', '+32(0)81234567', FALSE, TRUE, FALSE, 'BE0100200300', '2023-01-11',
        21);

-- Fidelity Cards
INSERT INTO FidelityCard (points, isValid, clientId)
VALUES (150, TRUE, 2),
       (320, TRUE, 3),
       (80, TRUE, 5),
       (5000, FALSE, 6),
       (540, TRUE, 7),
       (210, TRUE, 8),
       (1050, TRUE, 10),
       (75, TRUE, 11);

INSERT INTO DocumentType (id_, name_)
VALUES (1, 'Purchase Order'),
       (2, 'Delivery Note'),
       (3, 'Invoice'),
       (4, 'Credit Note'),
       (5, 'Preparation Order'),
       (6, 'Internal Transfer');

INSERT INTO Status_ (name_)
VALUES ('Pending'),
       ('Delivered'),
       ('In Progress'),
       ('Cancelled'),
       ('Paid');

INSERT INTO WorkFlowType (id_, name_, isBuy, isSupplier, isInternal)
VALUES (1, 'Buy', TRUE, FALSE, FALSE),
       (2, 'Sell', FALSE, TRUE, FALSE),
       (3, 'Internal', FALSE, FALSE, TRUE);

INSERT INTO WorkFlow (id_, workFlowTypeId, statusId, usId, otherId)
VALUES (1, 1, 'Delivered', 1, 12),
       (2, 1, 'Pending', 1, 13),
       (3, 1, 'Cancelled', 1, 18),
       (4, 1, 'Delivered', 1, 20),
       (5, 2, 'Delivered', 1, 2),
       (6, 2, 'Pending', 1, 3),
       (7, 2, 'Delivered', 1, 4),
       (8, 2, 'Paid', 1, 11),
       (9, 3, 'Delivered', 1, NULL),
       (10, 3, 'In Progress', 1, NULL),
       (11, 3, 'Pending', 1, NULL);

INSERT INTO Document_ (id_, date_, plannedSendingDate, plannedReceiveDate, effectiveSendingDate, effectiveReceiveDate,
                       paymentDelay, commentary, isChecked, workflowId, documentTypeId, addressId)
VALUES (1, '2025-01-10', '2026-06-01', '2026-06-11', '2025-01-10', NULL, 30, NULL, TRUE, 1, 1, 12),
       (2, '2025-01-17', NULL, NULL, NULL, '2025-01-17', 0, NULL, TRUE, 1, 2, NULL),
       (3, '2025-01-17', NULL, NULL, NULL, '2025-01-17', 30, NULL, TRUE, 1, 3, NULL),
       (4, '2025-05-20', '2026-06-01', '2026-06-15', '2025-05-20', NULL, 45, NULL, FALSE, 2, 1, 13),
       (5, '2025-03-01', '2026-06-01', '2026-06-15', NULL, NULL, 30, NULL, FALSE, 3, 1, 18),
       (6, '2025-03-05', NULL, NULL, NULL, NULL, 0, NULL, TRUE, 3, 4, NULL),
       (7, '2025-04-02', '2026-06-01', '2026-06-11', '2025-04-02', NULL, 60, NULL, TRUE, 4, 1, 20),
       (8, '2025-04-11', NULL, NULL, NULL, '2025-04-11', 0, NULL, TRUE, 4, 2, NULL),
       (9, '2025-04-11', NULL, NULL, NULL, '2025-04-11', 60, NULL, TRUE, 4, 3, NULL),
       (10, '2025-02-14', NULL, NULL, '2025-02-14', '2025-02-14', 0, NULL, TRUE, 5, 3, 2),
       (11, '2025-02-14', NULL, NULL, '2025-02-14', '2025-02-14', 0, NULL, TRUE, 5, 2, NULL),
       (12, '2025-05-28', NULL, NULL, NULL, NULL, 15, NULL, FALSE, 6, 3, 3),
       (13, '2025-03-10', NULL, NULL, '2025-03-10', '2025-03-10', 0, NULL, TRUE, 7, 3, 4),
       (14, '2025-03-10', NULL, NULL, '2025-03-10', '2025-03-10', 0, NULL, TRUE, 7, 2, NULL),
       (15, '2025-03-15', NULL, NULL, '2025-03-15', NULL, 0, 'Partial return — 2 damaged items', TRUE, 7, 4, NULL),
       (16, '2025-04-22', NULL, NULL, '2025-04-22', '2025-04-22', 30, NULL, TRUE, 8, 3, 11),
       (17, '2025-04-22', NULL, NULL, '2025-04-22', '2025-04-22', 0, NULL, TRUE, 8, 2, NULL),
       (18, '2025-05-05', NULL, NULL, '2025-05-05', '2025-05-05', 0, NULL, TRUE, 9, 6, NULL),
       (19, '2025-05-30', NULL, '2025-06-02', NULL, NULL, 0, 'Prepare 20 units of fresh croissant assortment', FALSE,
        10, 5, NULL),
       (20, '2025-06-01', NULL, '2025-06-01', NULL, NULL, 0, 'Monthly stock count — freezer section A-a/3', FALSE, 11,
        6, NULL);

INSERT INTO WorkFlowDocument (workflowId, documentId)
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (2, 4),
       (3, 5),
       (3, 6),
       (4, 7),
       (4, 8),
       (4, 9),
       (5, 10),
       (5, 11),
       (6, 12),
       (7, 13),
       (7, 14),
       (7, 15),
       (8, 16),
       (8, 17),
       (9, 18),
       (10, 19),
       (11, 20);

INSERT INTO Recipe (id_, name_, instructions, finalProductId)
VALUES (1, 'Spaghetti Bolognese',
        '1. Bring a large pot of salted water to a boil and cook the spaghetti until al dente, then drain and set aside.
        2. In a large pan, brown the ground beef over medium-high heat, breaking it up as it cooks, until no pink remains.
        3. Add the tomato paste and stir well to coat the meat, cooking for 2 minutes to deepen the flavour.
        4. Season generously with black pepper and dried oregano, stir and simmer for 1 minute.
        5. Add the ketchup as a quick sauce base, stir to combine, and let the sauce simmer on low heat for 10 minutes.
        6. Serve the sauce over the drained spaghetti and finish with an extra pinch of oregano.',
        95),

       (2, 'Salmon with Rice and Soy Glaze',
        '1. Cook the basmati rice according to package instructions and keep warm.
        2. Pat the salmon fillets dry and season with black pepper on both sides.
        3. Heat a non-stick pan over medium-high heat and sear the salmon skin-side down for 4 minutes.
        4. Flip the fillets, drizzle soy sauce over them, and cook for another 3 minutes until cooked through.
        5. Plate the rice, lay the salmon on top, and spoon any remaining soy glaze from the pan over the dish.',
        96),

       (3, 'Tuna and Chickpea Salad',
        '1. Drain and rinse the canned tuna and canned chickpeas thoroughly.
        2. In a large bowl, combine the tuna and chickpeas.
        3. Add mayonnaise and mix gently until everything is well coated.
        4. Season with black pepper and a pinch of paprika powder.
        5. Serve chilled or at room temperature.',
        97),

       (4, 'Croissant Ham and Cheese Sandwich',
        '1. Slice each croissant in half horizontally.
        2. Layer slices of ham evenly on the bottom half of each croissant.
        3. Add a generous layer of gouda cheese on top of the ham.
        4. Close the croissant and press lightly.
        5. Optional: warm in a preheated oven at 180°C for 5 minutes until the cheese begins to melt.',
        98),

       (5, 'Greek Yogurt Banana Bowl',
        '1. Peel and slice the bananas into rounds.
        2. Spoon the greek yogurt into a bowl.
        3. Arrange the banana slices on top of the yogurt.
        4. Drizzle with a small amount of ketchup if desired for a sweet-tangy contrast, or omit for a plain version.
        5. Serve immediately as a fresh breakfast or snack.',
        99),

       (6, 'Paprika Chicken with Frozen Fries',
        '1. Cut the chicken breast into strips and season thoroughly with paprika powder, black pepper, and dried oregano.
        2. Heat a pan over medium-high heat and cook the chicken strips for 6 to 8 minutes, turning regularly, until golden and cooked through.
        3. Meanwhile, cook the frozen french fries according to package instructions, either in an oven or air fryer.
        4. Serve the chicken strips alongside the fries with mayonnaise and ketchup as dipping sauces.',
        100),

       (7, 'Cod with Tomato Sauce and Rice',
        '1. Cook the basmati rice according to package instructions and keep warm.
        2. In a saucepan, heat the tomato paste with a splash of water over medium heat, stirring until smooth.
        3. Season the sauce with dried oregano, black pepper, and paprika powder.
        4. In a separate pan, sear the cod fillets for 3 minutes per side until flaky and opaque.
        5. Plate the rice, place the cod on top, and spoon the tomato sauce generously over the fish.',
        101);

INSERT INTO RecipeComposition (recipeId, productId, quantity)
VALUES (1, 37, 2),
       (1, 13, 1),
       (1, 36, 1),
       (1, 43, 1),
       (1, 45, 1),
       (1, 40, 1),

       (2, 38, 1),
       (2, 46, 1),
       (2, 42, 2),
       (2, 43, 1),

       (3, 34, 2),
       (3, 35, 1),
       (3, 41, 1),
       (3, 43, 1),
       (3, 44, 1),

       (4, 20, 1),
       (4, 49, 2),
       (4, 17, 1),

       (5, 18, 1),
       (5, 8, 1),

       (6, 12, 1),
       (6, 23, 1),
       (6, 44, 1),
       (6, 43, 1),
       (6, 45, 1),
       (6, 41, 1),
       (6, 40, 1),

       (7, 48, 1),
       (7, 38, 1),
       (7, 36, 1),
       (7, 45, 1),
       (7, 43, 1),
       (7, 44, 1);
