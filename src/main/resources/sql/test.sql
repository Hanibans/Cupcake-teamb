-- =========================================================
-- Testdata til Cupcake
-- Køres EFTER init.sql, på en frisk database for at id numre passer.
-- =========================================================

BEGIN;
-- ---------- Bunde (fra opgavebeskrivelsen) ----------
-- base_id 1-5
INSERT INTO public."CupcakeBase" (base_flavor, base_price) VALUES
    ('Chocolate', 5),   -- 1
    ('Vanilla',   5),   -- 2
    ('Nutmeg',    5),   -- 3
    ('Pistacio',  6),   -- 4
    ('Almond',    7);   -- 5

-- ---------- Toppe (fra opgavebeskrivelsen) ----------
-- topping_id 1-9
INSERT INTO public."CupcakeTopping" (topping_flavor, topping_price) VALUES
    ('Chocolate',   5), -- 1
    ('Blueberry',   5), -- 2
    ('Rasberry',    5), -- 3
    ('Crispy',      6), -- 4
    ('Strawberry',  6), -- 5
    ('Rum/Raisin',  7), -- 6
    ('Orange',      8), -- 7
    ('Lemon',       8), -- 8
    ('Blue cheese', 9); -- 9

-- ---------- Brugere ----------
-- Passwords følger regler: 8-15 tegn med både bogstaver og tal.
-- (Vi må selvfølgelig ikke gemme passwords i klartekst. Men nu er det bare en test.)
INSERT INTO public."User" ("isAdmin", name, email, password, phone_number, account_balance) VALUES
    (true,  'Admin',  'admin@cupcake.dk', 'admin1234', '11111111', 0),   -- user_id 1
    (false, 'Julie',  'julie@test.dk',    'julie1234', '22222222', 250), -- user_id 2
    (false, 'Mads',   'mads@test.dk',     'mads12345', '33333333', 50);  -- user_id 3

-- ---------- Julies ordre (betalt og afhentet) ----------
-- Indhold: 2 x Chocolate/Strawberry (5+6=11 kr. stk.) og 1 x Vanilla/Lemon (5+8=13 kr.)
-- Total: 2*11 + 1*13 = 35 kr.
INSERT INTO public."Order" (user_id, order_date, total_price, order_status) VALUES
    (2, NOW() - INTERVAL '1 month', 35, 'Completed');                  -- order_id 1

INSERT INTO public."Orderline" (order_id, base_id, topping_id, quantity) VALUES
    (1, 1, 5, 2),   -- 2 x Chocolate-bund med Strawberry-top
    (1, 2, 8, 1);   -- 1 x Vanilla-bund med Lemon-top

INSERT INTO public.payment (user_id, total_cost, order_id, is_paid) VALUES
    (2, 35, 1, true);

-- ---------- Mads' ordre (IKKE betalt endnu) ----------
-- Nyttig til at teste admin-siderne: US-5 skal markere ubetalte ordrer,
-- og US-8 skal kunne slette en ordre, som aldrig bliver betalt.
-- Indhold: 3 x Almond/Blue cheese (7+9=16 kr.) = 48 kr.
INSERT INTO public."Order" (user_id, order_date, total_price, order_status) VALUES
    (3, NOW(), 48, 'Pending');                                         -- order_id 2

INSERT INTO public."Orderline" (order_id, base_id, topping_id, quantity) VALUES
    (2, 5, 9, 3);

INSERT INTO public.payment (user_id, total_cost, order_id, is_paid) VALUES
    (3, 48, 2, false);

COMMIT;
