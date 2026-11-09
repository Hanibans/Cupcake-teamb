-- =========================================================
-- Test data for Cupcake
-- Run AFTER init.sql, on a fresh database, so the id numbers line up.
-- =========================================================

BEGIN;

-- ---------- Bases ----------
INSERT INTO public.cupcake_base (base_flavor, base_price) VALUES
    ('Chocolate', 5),   -- 1
    ('Vanilla',   5),   -- 2
    ('Nutmeg',    5),   -- 3
    ('Pistacio',  6),   -- 4
    ('Almond',    7);   -- 5

-- ---------- Toppings ----------
INSERT INTO public.cupcake_topping (topping_flavor, topping_price) VALUES
    ('Chocolate',   5), -- 1
    ('Blueberry',   5), -- 2
    ('Rasberry',    5), -- 3
    ('Crispy',      6), -- 4
    ('Strawberry',  6), -- 5
    ('Rum/Raisin',  7), -- 6
    ('Orange',      8), -- 7
    ('Lemon',       8), -- 8
    ('Blue cheese', 9); -- 9

-- ---------- Users ----------
-- Plain-text passwords are only OK because this is test data.
INSERT INTO public.users (is_admin, name, email, password, phone_number, account_balance) VALUES
    (true,  'Admin', 'admin@cupcake.dk', 'admin1234', '11111111', 0),   -- user_id 1
    (false, 'Julie', 'julie@test.dk',    'julie1234', '22222222', 250), -- user_id 2
    (false, 'Mads',  'mads@test.dk',     'mads12345', '33333333', 50);  -- user_id 3

-- ---------- Julie's order (paid and picked up) ----------
-- 2 x Chocolate/Strawberry (11 kr.) + 1 x Vanilla/Lemon (13 kr.) = 35 kr.
INSERT INTO public.orders (user_id, order_date, total_price, order_status) VALUES
    (2, NOW() - INTERVAL '1 month', 35, 'Completed');  -- order_id 1

INSERT INTO public.orderline (order_id, base_id, topping_id, quantity) VALUES
    (1, 1, 5, 2),
    (1, 2, 8, 1);

INSERT INTO public.payment (user_id, order_id, total_cost, is_paid) VALUES
    (2, 1, 35, true);

-- ---------- Mads' order (NOT paid) ----------
-- For testing US-5 (see unpaid orders) and US-8 (delete order).
-- 3 x Almond/Blue cheese (16 kr.) = 48 kr.
INSERT INTO public.orders (user_id, order_date, total_price, order_status) VALUES
    (3, NOW(), 48, 'Pending');  -- order_id 2

INSERT INTO public.orderline (order_id, base_id, topping_id, quantity) VALUES
    (2, 5, 9, 3);

INSERT INTO public.payment (user_id, order_id, total_cost, is_paid) VALUES
    (3, 2, 48, false);

COMMIT;
