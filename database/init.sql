
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

CREATE TABLE DiscountPromotion (
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
    priceHTVA decimal(10, 2) not null,
    tva decimal(5, 2) not null,
    loyaltyPoints int not null,
    isEdible boolean not null,
    minStockQuantity int not null constraint min_stock_quantity_positive check (minStockQuantity >= 0),
    minPromotionQuantity int not null constraint min_promotion_quantity_positive check (minPromotionQuantity >= 0),
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
