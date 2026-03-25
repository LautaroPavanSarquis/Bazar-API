CREATE TABLE customers (
    id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_customers_dni UNIQUE (dni),
    CONSTRAINT uk_customers_email UNIQUE (email)
);

CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL,
    stock DOUBLE NOT NULL,
    active BIT(1) NOT NULL DEFAULT b'1',
    PRIMARY KEY (id)
);

CREATE TABLE sales (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sale_date DATE NOT NULL,
    total DOUBLE NOT NULL,
    customer_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sales_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE sale_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DOUBLE NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sale_items_sale
        FOREIGN KEY (sale_id) REFERENCES sales (id),
    CONSTRAINT fk_sale_items_product
        FOREIGN KEY (product_id) REFERENCES products (id)
);
