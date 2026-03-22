-- Migration para popular transações do usuário Admin (últimos 6 meses)
-- Data: 2026-03-22
-- Usuário Admin (ID: 1)
-- Renda Mensal: 4.800 (Categoria 1: Salário)

-- Outubro 2025
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Outubro', '2025-10-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2025-10-10', NOW()),
(1, 'EXPENSE', 850.50, 4, 'Supermercado', '2025-10-12', NOW()),
(1, 'EXPENSE', 200.00, 6, 'Gasolina', '2025-10-15', NOW()),
(1, 'EXPENSE', 150.00, 7, 'Cinema', '2025-10-20', NOW()),
(1, 'EXPENSE', 500.00, 4, 'Jantar', '2025-10-25', NOW());

-- Novembro 2025
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Novembro', '2025-11-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2025-11-10', NOW()),
(1, 'EXPENSE', 920.00, 4, 'Supermercado', '2025-11-12', NOW()),
(1, 'EXPENSE', 180.00, 6, 'Gasolina', '2025-11-15', NOW()),
(1, 'EXPENSE', 350.00, 8, 'Consulta Médica', '2025-11-18', NOW()),
(1, 'EXPENSE', 250.00, 10, 'Internet e Streaming', '2025-11-20', NOW());

-- Dezembro 2025
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Dezembro', '2025-12-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2025-12-10', NOW()),
(1, 'EXPENSE', 1200.00, 4, 'Compras Natal', '2025-12-15', NOW()),
(1, 'EXPENSE', 200.00, 6, 'Gasolina', '2025-12-20', NOW()),
(1, 'EXPENSE', 600.00, 7, 'Festas', '2025-12-24', NOW()),
(1, 'INCOME', 500.00, 2, 'Freela Natal', '2025-12-28', NOW());

-- Janeiro 2026
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Janeiro', '2026-01-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2026-01-10', NOW()),
(1, 'EXPENSE', 780.00, 4, 'Supermercado', '2026-01-12', NOW()),
(1, 'EXPENSE', 400.00, 9, 'Material Escolar', '2026-01-15', NOW()),
(1, 'EXPENSE', 200.00, 6, 'Gasolina', '2026-01-20', NOW()),
(1, 'EXPENSE', 100.00, 10, 'Spotify', '2026-01-25', NOW());

-- Fevereiro 2026
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Fevereiro', '2026-02-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2026-02-10', NOW()),
(1, 'EXPENSE', 850.00, 4, 'Supermercado', '2026-02-12', NOW()),
(1, 'EXPENSE', 180.00, 6, 'Gasolina', '2026-02-15', NOW()),
(1, 'EXPENSE', 300.00, 7, 'Carnaval', '2026-02-22', NOW()),
(1, 'INCOME', 250.00, 3, 'Dividendos', '2026-02-28', NOW());

-- Março 2026 (Mês Atual)
INSERT INTO transactions (user_id, type, amount, category_id, description, transaction_date, created_at) VALUES 
(1, 'INCOME', 4800.00, 1, 'Salário Março', '2026-03-05', NOW()),
(1, 'EXPENSE', 1500.00, 5, 'Aluguel', '2026-03-10', NOW()),
(1, 'EXPENSE', 620.00, 4, 'Supermercado', '2026-03-12', NOW()),
(1, 'EXPENSE', 220.00, 6, 'Gasolina', '2026-03-15', NOW());
