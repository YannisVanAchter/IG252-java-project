

DATABASE IF NOT EXISTS `${MYSQL_DATABASE}`;
USE `${MYSQL_DATABASE}`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Pointing;
DROP TABLE IF EXISTS Batch;
DROP TABLE IF EXISTS Detail;
DROP TABLE IF EXISTS RecipeComposition;
DROP TABLE IF EXISTS Recipe;
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
DROP TABLE IF EXISTS FidelityCard;
DROP TABLE IF EXISTS Client_supplier;
DROP TABLE IF EXISTS Position_;
DROP TABLE IF EXISTS Role_;
DROP TABLE IF EXISTS Absence;
DROP TABLE IF EXISTS Absence_type;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Address_;
DROP TABLE IF EXISTS Locality;

SET FOREIGN_KEY_CHECKS = 1;


-- ! TABLES CREATION --
CREATE TABLE Locality (
    postalId INT NOT NULL,
    city VARCHAR(255) NOT NULL,

    PRIMARY KEY (postalId, city)
);


CREATE TABLE Address_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    streetName VARCHAR(255) NOT NULL,
    streetNumber INT NOT NULL,
    postalId INT NOT NULL,
    city VARCHAR(255) NOT NULL,

    UNIQUE (streetName, streetNumber, postalId, city),
    FOREIGN KEY (postalId, city) REFERENCES Locality(postalId, city)
);

-- * Human resources tables --

CREATE TABLE Employee (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phoneNumber VARCHAR(20) NOT NULL UNIQUE,
    iban VARCHAR(34) NOT NULL UNIQUE,
    hourlyWage DECIMAL(10,2) NOT NULL,
    nbHoursPlannedWeek INT NOT NULL,
    hiringDate DATE NOT NULL,
    nbPaidDaysHalfDay INT NOT NULL,
    pwd VARCHAR(255) NOT NULL,
    addressId INT NOT NULL,
    managerId INT,

    FOREIGN KEY (addressId) REFERENCES Address_(id_),
    FOREIGN KEY (managerId) REFERENCES Employee(id_)
);

CREATE TABLE Absence_type (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Absence (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    startDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate DATE,
    description_ VARCHAR(255),
    employeeId INT NOT NULL,
    absenceTypeId INT NOT NULL,
    
    FOREIGN KEY (employeeId) REFERENCES Employee(id_),
    FOREIGN KEY (absenceTypeId) REFERENCES Absence_type(id_),
    UNIQUE (employeeId, startDate, endDate),
    CHECK (endDate IS NULL OR endDate >= startDate)
);

CREATE TABLE Role_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Position_ (
    roleId INT NOT NULL,
    employeeId INT NOT NULL,

    PRIMARY KEY (employeeId, roleId),
    FOREIGN KEY (roleId) REFERENCES Role_(id_),
    FOREIGN KEY (employeeId) REFERENCES Employee(id_)
);

CREATE TABLE Pointing (
    date_ DATE NOT NULL DEFAULT (CURRENT_DATE),    
    startTime TIME NOT NULL DEFAULT (CURRENT_TIME),
    endTime TIME,
    employeeId INT NOT NULL,

    PRIMARY KEY (date_, employeeId),
    FOREIGN KEY (employeeId) REFERENCES Employee(id_),
    CHECK (endTime IS NULL OR endTime > startTime)
);

-- * Stock management tables --

CREATE TABLE ProductCategory (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE Product (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL,
    priceEVAT DECIMAL(10,2) NOT NULL CHECK (priceEVAT >= 0),
    VAT DECIMAL(5,2) NOT NULL CHECK (VAT >= 0 AND VAT <= 100),
    loyaltyPoints INT NOT NULL,
    isEdible BOOLEAN NOT NULL,
    minStockQuantity INT NOT NULL CHECK (minStockQuantity >= 0),
    categoryId INT NOT NULL,
    
    FOREIGN KEY (categoryId) REFERENCES ProductCategory(id_)
);

CREATE TABLE LocationProduct (
    shelf VARCHAR(255) NOT NULL,
    floor_ INT NOT NULL,
    isStock BOOLEAN NOT NULL,
    isFreezer BOOLEAN NOT NULL,
    PRIMARY KEY (shelf, floor_, isStock)
);

CREATE TABLE QuantityProduct (
    shelf VARCHAR(255) NOT NULL,
    floor_ INT NOT NULL,
    isStock BOOLEAN NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),

    PRIMARY KEY (shelf, floor_, isStock, productId),
    FOREIGN KEY (shelf, floor_, isStock) REFERENCES LocationProduct(shelf, floor_, isStock),
    FOREIGN KEY (productId) REFERENCES Product(id_)
);

CREATE TABLE Discount (
    discountPercentage DECIMAL(5,2) NOT NULL CHECK (discountPercentage BETWEEN 0 AND 100),
    requiredQuantity INT NOT NULL CHECK (requiredQuantity >= 1),
    startDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate DATE NOT NULL,
    name_ VARCHAR(255) NOT NULL,
    productId INT NOT NULL,

    PRIMARY KEY (startDate, endDate, requiredQuantity, discountPercentage),
    FOREIGN KEY (productId) REFERENCES Product(id_),
    CHECK (endDate >= startDate)
);

-- * Workflow and document management tables --

CREATE TABLE Client_supplier (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20) NOT NULL,
    isClient BOOLEAN NOT NULL,
    isSupplier BOOLEAN NOT NULL,
    isUs BOOLEAN NOT NULL,
    VATNumber VARCHAR(50),
    dateBecameClient DATE,
    
    addressId INT NOT NULL,
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
);

CREATE TABLE FidelityCard (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    points INT NOT NULL CHECK (points >= 0) DEFAULT 0,
    isValid BOOLEAN NOT NULL DEFAULT TRUE,

    clientId INT NOT NULL,
    FOREIGN KEY (clientId) REFERENCES Client_supplier(id_)
);

CREATE TABLE WorkFlowType (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE,
    isBuy BOOLEAN NOT NULL,
    isSupplier BOOLEAN NOT NULL,
    isInternal BOOLEAN NOT NULL
);

-- ? Add field to specify the next status
CREATE TABLE Status_ (
    name_ VARCHAR(255) PRIMARY KEY
);

CREATE TABLE WorkFlow (
    id_ INT AUTO_INCREMENT PRIMARY KEY,

    workFlowTypeId INT NOT NULL,
    statusId VARCHAR(255) NOT NULL,
    usId INT NOT NULL,
    otherId INT,
    FOREIGN KEY (workFlowTypeId) REFERENCES WorkFlowType(id_),
    FOREIGN KEY (usId) REFERENCES Client_supplier(id_),
    FOREIGN KEY (otherId) REFERENCES Client_supplier(id_),
    FOREIGN KEY (statusId) REFERENCES Status_(name_)
);

CREATE TABLE DocumentType (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE Document_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    date_ DATE NOT NULL DEFAULT (CURRENT_DATE),
    plannedSendingDate DATE,
    plannedReceiveDate DATE,
    effectiveSendingDate DATE,
    effectiveReceiveDate DATE,
    paymentDelay INT NOT NULL,
    commentary VARCHAR(255),
    isChecked BOOLEAN NOT NULL,

    workflowId INT NOT NULL,
    documentTypeId INT NOT NULL,
    addressId INT,
    FOREIGN KEY (documentTypeId) REFERENCES DocumentType(id_),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow(id_),
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
);

CREATE TABLE WorkFlowDocument (
    workflowId INT NOT NULL,
    documentId INT NOT NULL,

    PRIMARY KEY (workflowId, documentId),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow(id_),
    FOREIGN KEY (documentId) REFERENCES Document_(id_)
)

CREATE TABLE Recipe (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE,
    instructions TEXT NOT NULL,
    finalProductId INT NOT NULL,
    FOREIGN KEY (finalProductId) REFERENCES Product(id_)
);

CREATE TABLE RecipeComposition (
    recipeId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (recipeId, productId),
    FOREIGN KEY (recipeId) REFERENCES Recipe(id_),
    FOREIGN KEY (productId) REFERENCES Product(id_)
);

CREATE TABLE PreparationOrder (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    documentId INT NOT NULL,
    recipeId INT NOT NULL,
    FOREIGN KEY (documentId) REFERENCES Document_(id_),
    FOREIGN KEY (recipeId) REFERENCES Recipe(id_),
    UNIQUE (documentId, recipeId)
);

CREATE TABLE Detail (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    documentId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    priceVAT DECIMAL(10,2) NOT NULL CHECK (priceVAT >= 0),
    VAT DECIMAL(5,2) NOT NULL CHECK (VAT >= 0 AND VAT <= 100),
    fidelityPointsEarned INT NOT NULL CHECK (fidelityPointsEarned >= 0),
    FOREIGN KEY (productId) REFERENCES Product(id_),
    FOREIGN KEY (documentId) REFERENCES Document_(id_),
    UNIQUE (documentId, productId)
);

CREATE TABLE Batch (
    id_ INT AUTO_INCREMENT,
    detailId INT NOT NULL,
    productId INT NOT NULL,
    expirationDate DATE,
    originCountry VARCHAR(255) NOT NULL,
    PRIMARY KEY (detailId, productId, id_),
    FOREIGN KEY (detailId) REFERENCES Detail(id_),
    FOREIGN KEY (productId) REFERENCES Product(id_)
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
BEFORE INSERT ON Document_
FOR EACH ROW
BEGIN
    DECLARE documentTypeName VARCHAR(255);
    SELECT name_ INTO documentTypeName
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
BEFORE INSERT ON Batch
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
 

CREATE TRIGGER tgr_U_Batch_CheckExpirationDateRequirement
BEFORE UPDATE ON Batch
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
BEFORE INSERT ON Client_supplier
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
BEFORE UPDATE ON Client_supplier
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


CREATE TRIGGER tgr_C_FidelityCard_CheckIsClient
BEFORE INSERT ON FidelityCard
FOR EACH ROW
BEGIN
    DECLARE clientIsClient BOOLEAN;
    SELECT isClient INTO clientIsClient
        FROM Client_supplier WHERE id_ = NEW.clientId;
 
    IF clientIsClient = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A fidelity card can only be assigned to a client';
    END IF;
END


CREATE TRIGGER tgr_U_FidelityCard_CheckIsClient
BEFORE UPDATE ON FidelityCard
FOR EACH ROW
BEGIN
    DECLARE clientIsClient BOOLEAN;
    SELECT isClient INTO clientIsClient
        FROM Client_supplier WHERE id_ = NEW.clientId;
 
    IF clientIsClient = FALSE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A fidelity card can only be assigned to a client';
    END IF;
END


CREATE TRIGGER tgr_C_Employee_CheckNoSelfManager
BEFORE INSERT ON Employee
FOR EACH ROW
BEGIN
    IF NEW.managerId IS NOT NULL AND NEW.managerId = NEW.id_ THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'An employee cannot be their own manager';
    END IF;
END


CREATE TRIGGER tgr_U_Employee_CheckNoSelfManager
BEFORE UPDATE ON Employee
FOR EACH ROW
BEGIN
    IF NEW.managerId IS NOT NULL AND NEW.managerId = NEW.id_ THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'An employee cannot be their own manager';
    END IF;
END



CREATE TRIGGER tgr_C_Absence_CheckPaidDaysBalance
BEFORE INSERT ON Absence
FOR EACH ROW
BEGIN
    DECLARE absenceTypeName VARCHAR(255);
    DECLARE remainingDays   INT;
    DECLARE durationHalfDays INT;
 
    SELECT name_ INTO absenceTypeName
        FROM Absence_type WHERE id_ = NEW.absenceTypeId;
 
    IF absenceTypeName = 'Paid Leave' THEN
        SELECT nbPaidDaysHalfDay INTO remainingDays
            FROM Employee WHERE id_ = NEW.employeeId;
 
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
BEFORE INSERT ON Discount
FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;
 
    SELECT COUNT(*) INTO overlapCount
        FROM Discount
        WHERE productId = NEW.productId
          AND NEW.startDate <= endDate
          AND NEW.endDate   >= startDate;
 
    IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A discount already exists for this product during the requested period';
    END IF;
END



CREATE TRIGGER tgr_U_Discount_CheckNoOverlap
BEFORE UPDATE ON Discount
FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;
 
    SELECT COUNT(*) INTO overlapCount
        FROM Discount
        WHERE productId  = NEW.productId
          AND NEW.startDate <= endDate
          AND NEW.endDate   >= startDate
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
BEFORE INSERT ON RecipeComposition
FOR EACH ROW
BEGIN
    DECLARE finalProduct INT;
    SELECT finalProductId INTO finalProduct FROM Recipe WHERE id_ = NEW.recipeId;
 
    IF NEW.productId = finalProduct THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A recipe cannot contain its own final product as an ingredient';
    END IF;
END



CREATE TRIGGER tgr_U_RecipeComposition_CheckNoSelfReference
BEFORE UPDATE ON RecipeComposition
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
BEFORE INSERT ON Pointing
FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;
 
    SELECT COUNT(*) INTO overlapCount
        FROM Pointing
        WHERE employeeId = NEW.employeeId
          AND date_      = NEW.date_
          AND endTime    IS NOT NULL
          AND NEW.startTime < endTime
          AND (NEW.endTime IS NULL OR NEW.endTime > startTime);
 
    IF overlapCount > 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Overlapping time entry for this employee on this day';
    END IF;
END



CREATE TRIGGER tgr_U_Pointing_CheckNoOverlap
BEFORE UPDATE ON Pointing
FOR EACH ROW
BEGIN
    DECLARE overlapCount INT;
 
    SELECT COUNT(*) INTO overlapCount
        FROM Pointing
        WHERE employeeId = NEW.employeeId
          AND date_      = NEW.date_
          AND endTime    IS NOT NULL
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
FROM Product p, idx_ClientSupplier_isSupplier s, Document_ d, WorkFlow w, WorkFlowType wt, Detail dt
WHERE p.id_ = dt.productId
AND dt.documentId = d.id_
AND d.workflowId = w.id_
AND w.workFlowTypeId = wt.id_
AND wt.isSupplier = TRUE
AND w.otherId = s.id_;