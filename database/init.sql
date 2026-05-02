

USE `PROJET_JAVA`;

SET FOREIGN_KEY_CHECKS = 0;  -- désactive les contraintes FK

DROP TABLE IF EXISTS Pointing;
DROP TABLE IF EXISTS Batch;
DROP TABLE IF EXISTS Detail;
DROP TABLE IF EXISTS RecipeComposition;
DROP TABLE IF EXISTS Recipe;
DROP TABLE IF EXISTS Discount;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS ProductCategory;
DROP TABLE IF EXISTS Document_;
DROP TABLE IF EXISTS WorkFlow;
DROP TABLE IF EXISTS Status_;
DROP TABLE IF EXISTS WorkFlowType;
DROP TABLE IF EXISTS Client_supplier;
DROP TABLE IF EXISTS Position_;
DROP TABLE IF EXISTS Role_;
DROP TABLE IF EXISTS Absence;
DROP TABLE IF EXISTS Absence_type;
DROP TABLE IF EXISTS Employee;
DROP TABLE IF EXISTS Address_;
DROP TABLE IF EXISTS Locality;

SET FOREIGN_KEY_CHECKS = 1;  -- réactive les contraintes FK



CREATE TABLE Locality (
    postalId INT PRIMARY KEY,
    city VARCHAR(255) NOT NULL
);


CREATE TABLE Address_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    streetName VARCHAR(255) NOT NULL,
    streetNumber INT NOT NULL,
    postalId INT NOT NULL,
    FOREIGN KEY (postalId) REFERENCES Locality(postalId)
);

CREATE TABLE Employee (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phoneNumber VARCHAR(20),
    iban VARCHAR(34),
    hourlyWage DECIMAL(10,2) NOT NULL,
    nbHoursPlannedWeek INT NOT NULL,
    hiringDate DATE NOT NULL,
    nbPaidDaysHalfDay INT NOT NULL,
    pwd VARCHAR(255) NOT NULL,
    addressId INT,
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
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

CREATE TABLE Client_supplier (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL,
    firstname VARCHAR(255),
    email VARCHAR(255) NOT NULL,
    phoneNumber VARCHAR(20),
    isClient BOOLEAN NOT NULL,
    isSupplier BOOLEAN NOT NULL,
    VATNumber VARCHAR(50),
    dateBecameClient DATE,
    addressId INT,
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
);

CREATE TABLE WorkFlowType (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE,
    isBuy BOOLEAN NOT NULL,
    isSupplier BOOLEAN NOT NULL,
    isInternal BOOLEAN NOT NULL
);

CREATE TABLE Status_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE WorkFlow (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    workFlowTypeId INT NOT NULL,
    statusId INT NOT NULL,
    FOREIGN KEY (workFlowTypeId) REFERENCES WorkFlowType(id_),
    FOREIGN KEY (statusId) REFERENCES Status_(id_)
);

CREATE TABLE Document_ (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    clientSupplierId INT NOT NULL,
    workflowId INT NOT NULL,
    addressId INT,
    date_ DATE NOT NULL DEFAULT (CURRENT_DATE),
    plannedSendingDate DATE,
    plannedReceiveDate DATE,
    effectiveSendingDate DATE,
    effectiveReceiveDate DATE,
    paymentDelay INT NOT NULL,
    commentary VARCHAR(255),
    isChecked BOOLEAN NOT NULL,
    FOREIGN KEY (clientSupplierId) REFERENCES Client_supplier(id_),
    FOREIGN KEY (workflowId) REFERENCES WorkFlow(id_),
    FOREIGN KEY (addressId) REFERENCES Address_(id_)
);

CREATE TABLE ProductCategory (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    name_ VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE Product (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    label_ VARCHAR(255) NOT NULL,
    priceEVAT DECIMAL(10,2) NOT NULL,
    VAT DECIMAL(5,2) NOT NULL,
    loyaltyPoints INT NOT NULL,
    isEdible BOOLEAN NOT NULL,
    minStockQuantity INT NOT NULL CHECK (minStockQuantity >= 0),
    minDiscountQuantity INT NOT NULL CHECK (minDiscountQuantity >= 0),
    categoryId INT NOT NULL,
    FOREIGN KEY (categoryId) REFERENCES ProductCategory(id_)
);

CREATE TABLE Discount (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    productId INT NOT NULL,
    startDate DATE NOT NULL DEFAULT (CURRENT_DATE),
    endDate DATE NOT NULL,
    requiredQuantity INT NOT NULL CHECK (requiredQuantity >= 1),
    discountPercentage DECIMAL(5,2) NOT NULL CHECK (discountPercentage BETWEEN 0 AND 100),
    label_ VARCHAR(255) NOT NULL,
    FOREIGN KEY (productId) REFERENCES Product(id_),
    CHECK (endDate >= startDate)
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

CREATE TABLE Detail (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    quantity INT NOT NULL CHECK (quantity > 0),
    price DECIMAL(10,2) NOT NULL
);

CREATE TABLE Batch (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    detailId INT NOT NULL,
    productId INT NOT NULL,
    expirationDate DATE NOT NULL,
    originCountry VARCHAR(255) NOT NULL,
    FOREIGN KEY (detailId) REFERENCES Detail(id_),
    FOREIGN KEY (productId) REFERENCES Product(id_)
);

CREATE TABLE Pointing (
    id_ INT AUTO_INCREMENT PRIMARY KEY,
    date_ DATE NOT NULL DEFAULT (CURRENT_DATE),
    employeeId INT NOT NULL,
    startTime TIME NOT NULL DEFAULT (CURRENT_TIME),
    endTime TIME,
    FOREIGN KEY (employeeId) REFERENCES Employee(id_),
    CHECK (endTime IS NULL OR endTime > startTime)
);