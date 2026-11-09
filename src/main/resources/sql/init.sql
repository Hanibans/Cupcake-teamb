-- =========================================================
-- Cupcake schema
-- Table and column names are lowercase snake_case so they match the Java mappers.
-- WARNING: drops all tables first. All data is deleted.
-- =========================================================

BEGIN;

-- CASCADE also drops foreign keys in OTHER tables (e.g. the test schema) that point here.
-- It does not drop those other tables.
DROP TABLE IF EXISTS public.payment CASCADE;
DROP TABLE IF EXISTS public.orderline CASCADE;
DROP TABLE IF EXISTS public.orders CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.cupcake_topping CASCADE;
DROP TABLE IF EXISTS public.cupcake_base CASCADE;

CREATE TABLE public.cupcake_base
(
    base_id     serial            NOT NULL,
    base_flavor character varying NOT NULL,
    base_price  integer           NOT NULL,
    CONSTRAINT cupcake_base_pkey PRIMARY KEY (base_id)
);

CREATE TABLE public.cupcake_topping
(
    topping_id     serial            NOT NULL,
    topping_flavor character varying NOT NULL,
    topping_price  integer           NOT NULL,
    CONSTRAINT cupcake_topping_pkey PRIMARY KEY (topping_id)
);

CREATE TABLE public.users
(
    user_id         serial                 NOT NULL,
    is_admin        boolean                NOT NULL DEFAULT false,
    name            character varying(55)  NOT NULL,
    email           character varying(55)  NOT NULL,
    password        character varying(255) NOT NULL,
    phone_number    character varying(20)  NOT NULL,
    account_balance integer                NOT NULL DEFAULT 0,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_email_key UNIQUE (email)
);

CREATE TABLE public.orders
(
    order_id     serial                   NOT NULL,
    user_id      integer                  NOT NULL,
    order_date   timestamp with time zone NOT NULL,
    total_price  integer                  NOT NULL,
    order_status character varying        NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (order_id),
    CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (user_id)
);

CREATE TABLE public.orderline
(
    orderline_id serial  NOT NULL,
    order_id     integer NOT NULL,
    base_id      integer NOT NULL,
    topping_id   integer NOT NULL,
    quantity     integer NOT NULL,
    CONSTRAINT orderline_pkey PRIMARY KEY (orderline_id),
    CONSTRAINT orderline_order_id_fkey FOREIGN KEY (order_id)
        REFERENCES public.orders (order_id) ON DELETE CASCADE,
    CONSTRAINT orderline_base_id_fkey FOREIGN KEY (base_id)
        REFERENCES public.cupcake_base (base_id),
    CONSTRAINT orderline_topping_id_fkey FOREIGN KEY (topping_id)
        REFERENCES public.cupcake_topping (topping_id)
);

CREATE TABLE public.payment
(
    payment_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    integer NOT NULL,
    order_id   integer NOT NULL,
    total_cost integer NOT NULL,
    is_paid    boolean NOT NULL DEFAULT false,
    CONSTRAINT payment_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users (user_id),
    CONSTRAINT payment_order_id_fkey FOREIGN KEY (order_id)
        REFERENCES public.orders (order_id) ON DELETE CASCADE
);

COMMIT;
