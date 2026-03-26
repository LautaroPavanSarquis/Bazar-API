CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           first_name VARCHAR(100) NOT NULL,
                           last_name VARCHAR(100) NOT NULL,
                           dni VARCHAR(20) NOT NULL,
                           email VARCHAR(255) NOT NULL,
                           CONSTRAINT uk_customers_dni UNIQUE (dni),
                           CONSTRAINT uk_customers_email UNIQUE (email)
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          brand VARCHAR(100) NOT NULL,
                          price DOUBLE PRECISION NOT NULL,
                          stock DOUBLE PRECISION NOT NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE sales (
                       id BIGSERIAL PRIMARY KEY,
                       sale_date DATE NOT NULL,
                       total DOUBLE PRECISION NOT NULL,
                       customer_id BIGINT NOT NULL,
                       CONSTRAINT fk_sales_customer
                           FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE sale_items (
                            id BIGSERIAL PRIMARY KEY,
                            sale_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            unit_price DOUBLE PRECISION NOT NULL,
                            CONSTRAINT fk_sale_items_sale
                                FOREIGN KEY (sale_id) REFERENCES sales (id),
                            CONSTRAINT fk_sale_items_product
                                FOREIGN KEY (product_id) REFERENCES products (id)
);