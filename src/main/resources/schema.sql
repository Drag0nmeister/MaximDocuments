CREATE TABLE IF NOT EXISTS invoices (
  id SERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(255),
    currency_rate DECIMAL(10, 2),
    product VARCHAR(255),
    quantity DECIMAL(10, 2)
    );

CREATE TABLE IF NOT EXISTS payments (
    id SERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    employee VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS payment_orders (
    id SERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    contractor VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(255),
    currency_rate DECIMAL(10, 2),
    commission DECIMAL(10, 2)
    );
