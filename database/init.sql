

Create table employee (
    id numeric(10) primary key,
    lastname varchar(255) not null,
    address_id numeric(10),
    firstname varchar(255) not null,
    phone_number numeric(12) not null,
    email varchar(255) not null,
    Iban varchar(255) not null,
    hourly_wage decimal(10, 2) not null,
    nb_hours_planned_week numeric(2) not null,
    hiring_date date not null,
    nb_paid_nails_half_day numeric(2) not null,
    pwd varchar(255) not null,
    FOREIGN KEY (address_id) REFERENCES address(street_number, street_name),
);

Create table absence (
    id numeric(10) primary key,
    employee_id numeric(10) not null,
    start_date date not null,
    end_date date,
    description varchar(255) not null,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
);

Create table address (
    street_name varchar(255) not null,
    street_number numeric(5) not null,
);

Create table pointing (
    date date not null,
    employee_id numeric(10) not null,
    start_time time not null,
    end_time time,
    FOREIGN KEY (employee_id) REFERENCES employee(id),
);

Create table position (
    function_id numeric(10) not null,
    employee_id numeric(10) not null,
    FOREIGN KEY (function_id) REFERENCES function(id),
    FOREIGN KEY (employee_id) REFERENCES employee(id),
);

Create table role (
    name varchar(255) not null,
);






CREATE TABLE ProductCategory (
    name varchar(64) not null primary key
);

CREATE TABLE StoreLocation (
    shelf int not null constraint shelf_positive check (shelf > 0),
    floor int not null constraint floor_positive check (floor > 0),
    isStock boolean not null,
    isRefrigerated boolean not null

    primary key (shelf, floor, isStock)
);

CREATE TABLE Discount (
    productCode varchar(64) not null,
    startDate date not null,
    endDate date not null,
    requiredQuantity int not null constraint required_quantity_positive check (requiredQuantity >= 1),
    discountPercentage decimal(5, 2) not null constraint discount_percentage_range check (discountPercentage >= 0 and discountPercentage <= 100),
    label varchar(255) not null,

    primary key (startDate, endDate, discountPercentage),
    foreign key (productCode) references Product(code)
);

CREATE TABLE Product (
    code varchar(64) not null primary key,
    label varchar(255) not null,
    priceEVAT decimal(10, 2) not null,
    VAT decimal(5, 2) not null,
    loyaltyPoints int not null,
    isEdible boolean not null,
    minStockQuantity int not null constraint min_stock_quantity_positive check (minStockQuantity >= 0),
    minDiscountQuantity int not null constraint min_discount_quantity_positive check (minDiscountQuantity >= 0),
    categoryName varchar(64) not null,

    foreign key (categoryName) references ProductCategory(name)
);

CREATE TABLE Recipe (
    name varchar(255) not null primary key,
    instructions text not null,
    finalProductCode varchar(64) not null,

    foreign key (finalProductCode) references Product(code)
);

CREATE TABLE RecipeComposition (
    recipeName varchar(255) not null,
    productCode varchar(64) not null,
    quantity int not null constraint quantity_positive check (quantity > 0),

    primary key (recipeName, productCode),
    foreign key (recipeName) references Recipe(name),
    foreign key (productCode) references Product(code)
);
