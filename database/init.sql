

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
    postalId INT PRIMARY KEY,
    city VARCHAR(255) NOT NULL,
    PRIMARY KEY (postalId, city) as localityId
);


CREATE TABLE Address_ (
    streetName VARCHAR(255) NOT NULL,
    streetNumber INT NOT NULL,
    localityId INT NOT NULL,
    PRIMARY KEY (localityId, streetName, streetNumber) as addressId,
    FOREIGN KEY (localityId) REFERENCES Locality(localityId)
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
    employeeId INT NOT NULL,
    absenceTypeId INT NOT NULL,
    startDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate DATE,
    description_ VARCHAR(255),
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
    employeeId INT NOT NULL,
    PRIMARY KEY (date_, employeeId),
    startTime TIME NOT NULL DEFAULT (CURRENT_TIME),
    endTime TIME,
    FOREIGN KEY (employeeId) REFERENCES Employee(id_),
    CHECK (endTime IS NULL OR endTime > startTime)
);

-- * Stock management tables --

CREATE TABLE ProductCategory (
    name_ VARCHAR(64) PRIMARY KEY
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
    FOREIGN KEY (categoryId) REFERENCES ProductCategory(name_)
);

CREATE TABLE LocationProduct (
    shelf VARCHAR(255) NOT NULL,
    floor_ INT NOT NULL,
    isStock BOOLEAN NOT NULL,
    isFreezer BOOLEAN NOT NULL,
    PRIMARY KEY (shelf, floor_, isStock) as locationProductId
);

CREATE TABLE QuantityProduct (
    locationProductId INT NOT NULL,
    productId INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity >= 0),
    PRIMARY KEY (locationProductId, productId),
    FOREIGN KEY (locationProductId) REFERENCES LocationProduct(locationProductId),
    FOREIGN KEY (productId) REFERENCES Product(id_)
);

CREATE TABLE Discount (
    discountPercentage DECIMAL(5,2) NOT NULL CHECK (discountPercentage BETWEEN 0 AND 100),
    requiredQuantity INT NOT NULL CHECK (requiredQuantity >= 1),
    startDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate DATE NOT NULL,
    productId INT NOT NULL,
    name_ VARCHAR(255) NOT NULL,
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
    clientId INT NOT NULL,
    points INT NOT NULL CHECK (points >= 0) DEFAULT 0,
    isValid BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (clientId) REFERENCES Client_supplier(id_)
);

CREATE TABLE WorkFlowType (
    name_ VARCHAR(255) PRIMARY KEY,
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
    FOREIGN KEY (workFlowTypeId) REFERENCES WorkFlowType(id_),
    FOREIGN KEY (statusId) REFERENCES Status_(name_)
);

CREATE TABLE DocumentType (
    name_ VARCHAR(255) PRIMARY KEY
)

CREATE TABLE Document_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    workflowId INT NOT NULL,
    documentTypeId INT NOT NULL,
    addressId INT NOT NULL,
    date_ DATE NOT NULL DEFAULT (CURRENT_DATE),
    plannedSendingDate DATE,
    plannedReceiveDate DATE,
    effectiveSendingDate DATE,
    effectiveReceiveDate DATE,
    paymentDelay INT NOT NULL,
    commentary VARCHAR(255),
    isChecked BOOLEAN NOT NULL,
    FOREIGN KEY (documentTypeId) REFERENCES DocumentType(id_),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow(id_),
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
);

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
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    detailId INT NOT NULL,
    productId INT NOT NULL,
    expirationDate DATE,
    originCountry VARCHAR(255) NOT NULL,
    FOREIGN KEY (detailId) REFERENCES Detail(id_),
    FOREIGN KEY (productId) REFERENCES Product(id_)
    PRIMARY KEY (detailId, productId, id_) as batchId
);

-- Triggers --

/**
 * Trigger notation: 
 * tgr_{CRUD}_{Target table}_{Trigger name or purpose}
*/

/**
 * This trigger ensures that all document created respect there respective 
 * requirements regarding optional fields. 
*/
CREATE TRIGGER tgr_C_Document_IntegrityCheck
BEFORE INSERT ON Document_
FOR EACH ROW
BEGIN
    -- TODO: Check DocumenttType table when implemented
    DECLARE documentTypeName VARCHAR(255);
    SELECT name_ INTO documentTypeName
        FROM DocumentType 
        WHERE id_ = NEW.documentTypeId;
    
    IF      documentTypeName = 'Purchase Order'
            AND new.plannedSendingDate >= CURRENT_DATE 
            AND new.plannedReceiveDate >= new.plannedSendingDate
            AND new.paymentDelay >= 0
            THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid planned dates or payment delay for Purchase workflow';
    
    ELSEIF  new.documentTypeName = 'Delivery Note'
            AND new.commentary IS NOT NULL
            AND new.addressId IS NOT NULL
            THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'Delivery Note should not have commentary or address';
    
    ELSEIF  documentTupeName = 'Preparation Order'
            AND new.commentary IS NOT NULL
            THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'Preparation Order should have commentary';
    END IF;
END;

/**
 * This trigger ensure that the expiration date of a batch is fullfilled
 * when it concerns an edible product.
*/
CREATE TRIGGER tgr_CU_Batch_CheckExpirationDateRequirement
BEFORE INSERT, UPDATE ON Batch
FOR EACH ROW
BEGIN
    DECLARE isEdible BOOLEAN;
    SELECT isEdible INTO isEdible FROM Product WHERE id_ = NEW.productId;
    
    IF  isEdible 
        AND NEW.expirationDate IS NOT NULL 
        AND NEW.expirationDate <= CURRENT_DATE 
        THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'Expiration date must be in the future for edible products';
    END IF;
END;

/**
 * This trigger ensures that a client or supplier cannot be us.
*/
CREATE TRIGGER tgr_CU_ClientSupplier_CheckIsClientIsSupplier
BEFORE INSERT, UPDATE ON Client_supplier
FOR EACH ROW
BEGIN
    IF      NEW.isUs = TRUE
            AND (NEW.isClient = TRUE OR NEW.isSupplier = TRUE)
            THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'US can not be a client nor a supplier';
    
    ELSEIF  NEW.isClient = FALSE 
            AND NEW.isSupplier = FALSE 
            THEN
        RAISE SQLSTATE '45000' SET MESSAGE_TEXT = 'A client/supplier must be either a client or a supplier';
    
    END IF;
END;


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