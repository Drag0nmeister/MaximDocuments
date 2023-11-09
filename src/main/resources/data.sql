INSERT INTO invoices (number, date, user_name, amount, currency, currency_rate, product, quantity) VALUES
    ('INV001', '2023-01-01', 'User1', 1000.00, 'USD', 1.00, 'Product1', 10);

INSERT INTO payments (number, date, user_name, amount, employee) VALUES
    ('PAY001', '2023-01-02', 'User2', 200.00, 'Employee1');

INSERT INTO payment_orders (number, date, user_name, amount, contractor, currency, currency_rate, commission) VALUES
    ('PO001', '2023-01-03', 'User3', 3000.00, 'Contractor1', 'EUR', 1.10, 30.00);
