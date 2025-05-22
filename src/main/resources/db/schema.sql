create table if not exists "order" (
    id bigserial primary key
);

create table if not exists "item" (
    id BIGSERIAL PRIMARY KEY,
    title varchar(100),
    description varchar(600),
    price numeric(10, 2),
    count numeric(10, 2),
    image_id varchar(100) not null
);

create table if not exists "order_item" (
    id BIGSERIAL PRIMARY KEY,
    count numeric(10, 2),
    item_id bigint references item(id) on delete cascade not null,
    order_id bigint references "order"(id) on delete cascade not null
);