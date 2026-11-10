BEGIN;

CREATE SCHEMA IF NOT EXISTS test;

DROP TABLE IF EXISTS test.payment CASCADE;
DROP TABLE IF EXISTS test.orderline CASCADE;
DROP TABLE IF EXISTS test.orders CASCADE;
DROP TABLE IF EXISTS test.users CASCADE;
DROP TABLE IF EXISTS test.cupcake_topping CASCADE;
DROP TABLE IF EXISTS test.cupcake_base CASCADE;

CREATE TABLE test.cupcake_base
(
    base_id     serial            NOT NULL,
    base_flavor character varying NOT NULL,
    base_price  integer           NOT NULL,
    CONSTRAINT cupcake_base_pkey PRIMARY KEY (base_id)
);

CREATE TABLE test.cupcake_topping
(
    topping_id     serial            NOT NULL,
    topping_flavor character varying NOT NULL,
    topping_price  integer           NOT NULL,
    CONSTRAINT cupcake_topping_pkey PRIMARY KEY (topping_id)
);

CREATE TABLE test.users
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

CREATE TABLE test.orders
(
    order_id     serial                   NOT NULL,
    user_id      integer                  NOT NULL,
    order_date   timestamp with time zone NOT NULL,
    total_price  integer                  NOT NULL,
    order_status character varying        NOT NULL,
    CONSTRAINT orders_pkey PRIMARY KEY (order_id),
    CONSTRAINT orders_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES test.users (user_id)
);

CREATE TABLE test.orderline
(
    orderline_id serial  NOT NULL,
    order_id     integer NOT NULL,
    base_id      integer NOT NULL,
    topping_id   integer NOT NULL,
    quantity     integer NOT NULL,
    CONSTRAINT orderline_pkey PRIMARY KEY (orderline_id),
    CONSTRAINT orderline_order_id_fkey FOREIGN KEY (order_id)
        REFERENCES test.orders (order_id) ON DELETE CASCADE,
    CONSTRAINT orderline_base_id_fkey FOREIGN KEY (base_id)
        REFERENCES test.cupcake_base (base_id),
    CONSTRAINT orderline_topping_id_fkey FOREIGN KEY (topping_id)
        REFERENCES test.cupcake_topping (topping_id)
);

CREATE TABLE test.payment
(
    payment_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    integer NOT NULL,
    order_id   integer NOT NULL,
    total_cost integer NOT NULL,
    is_paid    boolean NOT NULL DEFAULT false,
    CONSTRAINT payment_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES test.users (user_id),
    CONSTRAINT payment_order_id_fkey FOREIGN KEY (order_id)
        REFERENCES test.orders (order_id) ON DELETE CASCADE
);

COMMIT;
