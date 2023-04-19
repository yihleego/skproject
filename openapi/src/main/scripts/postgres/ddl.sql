create database sk;
\connect sk;

create table auction
(
    id                 bigint primary key    not null,
    item_id            text                  not null,
    buy_price          integer               null,
    bid_price          integer               null,
    bid_status         text                  not null,
    time_left          text                  not null,
    count              smallint              not null,
    featured           boolean default false not null,
    remaining          smallint              null,
    total              smallint              null,
    variant            jsonb                 null,
    accessory          jsonb                 null,
    accurate           boolean default false not null,
    created_time       timestamp             not null,
    updated_time       timestamp             not null,
    estimated_end_time timestamp             not null
);
create index idx_auction_item_id on auction (item_id);
create index idx_auction_buy_price on auction (buy_price);
create index idx_auction_bid_price on auction (bid_price);
create index idx_auction_bid_status on auction (bid_status);
create index idx_auction_time_left on auction (time_left);
create index idx_auction_estimated_end_time on auction (estimated_end_time);

create table auction_log
(
    id           bigserial primary key not null,
    auction_id   bigint                not null,
    bid_price    integer               null,
    bid_status   text                  not null,
    time_left    text                  not null,
    created_time timestamp             not null
);
create index idx_auction_log_auction_id on auction_log (auction_id);

create table item
(
    id           text primary key       not null,
    name         text                   not null,
    "group"      text                   not null,
    icon         text                   not null,
    description  text                   null,
    star         smallint default 0     not null,
    level        boolean  default false not null,
    location     text                   null,
    colorization jsonb                  null,
    created_time timestamp              not null,
    updated_time timestamp              null
);
create index idx_item_group on item ("group");
create index idx_item_name on item (name);

create table exchange
(
    id           bigserial primary key not null,
    last_price   integer               not null,
    buy_offers   jsonb                 null,
    sell_offers  jsonb                 null,
    created_time timestamp             not null
);
create index idx_exchange_created_time on exchange ("created_time");

create table config
(
    id           bigserial primary key not null,
    "group"      text                  not null,
    "key"        text                  not null,
    "value"      text                  null,
    version      integer  default 1    not null,
    status       smallint default 1    not null,
    created_time timestamp             not null,
    updated_time timestamp             null
);
create unique index uk_config_group_key on config ("group", "key");
insert into config("group", "key", "value", status, created_time, updated_time)
values ('auctionbot', 'autobid', '{"fallback":{"buyPrice":0,"bidPrice":11},"items":[{"rules":[{"key":"itemId","value":"Shard","operation":"endsWith"}],"buyPrice":0,"bidPrice":0},{"rules":[{"key":"itemId","value":"Sprite/","operation":"startsWith"}],"buyPrice":50000,"bidPrice":11},{"rules":[{"key":"itemId","value":"Upgrade/Lockboxes/Character","operation":"equals"}],"buyPrice":400000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Overcharged Mixmaster","operation":"equals"}],"buyPrice":1000000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Celestial Orbitgun","operation":"equals"}],"buyPrice":1000000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Gear/Trinket/Somnambulist''s Totem","operation":"equals"}],"buyPrice":1000000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Caladbolg","operation":"equals"}],"buyPrice":1000000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Spiral Soaker","operation":"equals"}],"buyPrice":1000000,"bidPrice":100000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Brandish","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Acheron","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Combuster","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Glacius","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Ice Axe","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Caladbolg","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Obsidian Edge","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Voltedge","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Divine Avenger","operation":"equals"},{"key":"variantName","value":"ATTACK_SPEED_INCREASED","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":30000},{"rules":[{"key":"itemId","value":"Weapon/Sword/Gran Faust","operation":"equals"},{"key":"variantName","value":"ATTACK_SPEED_INCREASED","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":30000},{"rules":[{"key":"itemId","value":"Weapon/Bomb/Electron Vortex","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Bomb/Celestial Vortex","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Bomb/Graviton Vortex","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Autogun","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Blitz Needle","operation":"equals"},{"key":"variantName","value":"CHARGE_TIME_REDUCTION","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":500000,"bidPrice":50000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Blaster","operation":"equals"},{"key":"variantName","value":"ATTACK_SPEED_INCREASED","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":30000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Winter Grave","operation":"equals"},{"key":"variantName","value":"ATTACK_SPEED_INCREASED","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":30000},{"rules":[{"key":"itemId","value":"Weapon/Handgun/Arcana","operation":"equals"},{"key":"variantName","value":"ATTACK_SPEED_INCREASED","operation":"equals"},{"key":"variantValue","value":"VERY_HIGH","operation":"equals"}],"buyPrice":300000,"bidPrice":30000}]}', 1, now(), null),
       ('marketbot', 'autooffer', '{"buyPrice":0,"sellPrice":7000}', 1, now(), null);
