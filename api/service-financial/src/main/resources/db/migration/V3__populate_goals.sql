-- Migration para popular metas financeiras (Goals) do usuário Admin
-- Data: 2026-03-22
-- Usuário Admin (ID: 1)

INSERT INTO goals (user_id, name, target_amount, current_amount, start_date, target_date, created_at) VALUES 
(1, 'Reserva de Emergência', 15000.00, 4500.00, '2025-10-01', '2026-12-31', NOW()),
(1, 'Viagem de Férias', 8000.00, 1200.00, '2026-01-01', '2026-12-15', NOW()),
(1, 'Novo Laptop', 6000.00, 0.00, '2026-03-01', '2026-09-30', NOW());
