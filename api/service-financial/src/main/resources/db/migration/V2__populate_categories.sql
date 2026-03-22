-- Migration para popular categorias do usuário Admin
-- Data: 2026-03-22
-- Usuário Admin (ID: 1)

INSERT INTO categories (user_id, name, type, created_at) VALUES 
(1, 'Salário', 'INCOME', NOW()),
(1, 'Freelance', 'INCOME', NOW()),
(1, 'Investimentos', 'INCOME', NOW()),
(1, 'Alimentação', 'EXPENSE', NOW()),
(1, 'Moradia', 'EXPENSE', NOW()),
(1, 'Transporte', 'EXPENSE', NOW()),
(1, 'Lazer', 'EXPENSE', NOW()),
(1, 'Saúde', 'EXPENSE', NOW()),
(1, 'Educação', 'EXPENSE', NOW()),
(1, 'Assinaturas e Serviços', 'EXPENSE', NOW());
