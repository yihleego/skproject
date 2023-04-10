create database if not exists sk;
use sk;

create table auction
(
    id                 bigint primary key   not null,
    item_id            varchar(191)         not null,
    buy_price          int                  null,
    bid_price          int                  null,
    bid_status         varchar(20)          not null comment 'NO_BID,BID,HIGH_BIDDER,OUTBID,WATCHING',
    time_left          varchar(20)          not null comment 'VERY_SHORT,SHORT,MEDIUM,LONG,VERY_LONG,ENDED,CANCELLED',
    count              smallint             not null,
    featured           tinyint(1) default 0 not null comment '0:no 1:yes',
    remaining          smallint             null comment 'for featured',
    total              smallint             null comment 'for featured',
    variant            json                 null,
    accessory          json                 null,
    accurate           tinyint(1) default 0 not null comment '0:no 1:yes',
    created_time       timestamp            not null,
    updated_time       timestamp            not null,
    estimated_end_time timestamp            not null
);
create index idx_auction_item_id on auction (item_id);
create index idx_auction_buy_price on auction (buy_price);
create index idx_auction_bid_price on auction (bid_price);
create index idx_auction_bid_status on auction (bid_status);
create index idx_auction_time_left on auction (time_left);
create index idx_auction_estimated_end_time on auction (estimated_end_time);

create table auction_log
(
    id           bigint auto_increment primary key not null,
    auction_id   bigint                            not null,
    buy_price    int                               null,
    bid_price    int                               null,
    status       tinyint                           not null comment '0:bid 1:buy',
    created_time timestamp                         not null,
    updated_time timestamp                         null
);

create table item
(
    id           varchar(191) primary key not null,
    name         varchar(191)             not null,
    `group`      varchar(40)              not null,
    icon         varchar(191)             not null,
    description  varchar(512)             null,
    star         tinyint    default 0     not null comment '0-5',
    level        tinyint(1) default 0     not null comment '0:item 1:level-item',
    location     varchar(40)              null,
    colorization json                     null,
    created_time timestamp                not null,
    updated_time timestamp                null
);
create index idx_item_group on item (`group`);
create index idx_item_name on item (name);

create table exchange
(
    id           bigint auto_increment primary key not null,
    last_price   int                               not null,
    buy_offers   json                              null,
    sell_offers  json                              null,
    created_time timestamp                         not null
);
create index idx_exchange_created_time on exchange (`created_time`);

create table config
(
    id           bigint auto_increment primary key not null,
    `group`      varchar(191)                      not null comment 'basic,auction,exchange',
    `key`        varchar(191)                      not null,
    `value`      text                              null,
    version      int     default 1                 not null,
    status       tinyint default 1                 not null comment '0:disabled 1:enabled',
    created_time timestamp                         not null,
    updated_time timestamp                         null
);
create unique index uk_config_key on config (`key`);
insert into config(`group`, `key`, `value`, status, created_time, updated_time)
values ('auction', 'auction_auto_bid', '[{"buyPrice":200000,"bidPrice":11,"fallback":true},{"exprs":[{"key":"itemId","value":"Upgrade/Lockboxes/Character","expr":"equals"}],"buyPrice":200000,"bidPrice":11},{"exprs":[{"key":"itemId","value":"Weapon/Handgun/Overcharged Mixmaster","expr":"equals"}],"buyPrice":1000000,"bidPrice":11},{"exprs":[{"key":"itemId","value":"Gear/Trinket/Somnambulist''s Totem","expr":"equals"}],"buyPrice":1000000,"bidPrice":11},{"exprs":[{"key":"itemId","value":"Weapon/Sword/Caladbolg","expr":"equals"}],"buyPrice":200000,"bidPrice":11},{"exprs":[{"key":"itemId","value":"Sprite/","expr":"startsWith"}],"buyPrice":50000,"bidPrice":11},{"exprs":[{"key":"variantName","value":"CHARGE_TIME_REDUCTION","expr":"equals"},{"key":"variantValue","value":"VERY_HIGH","expr":"equals"}],"buyPrice":50000,"bidPrice":11},{"exprs":[{"key":"variantName","value":"ATTACK_SPEED_INCREASED","expr":"equals"},{"key":"variantValue","value":"HIGH","expr":"equals"}],"buyPrice":30000,"bidPrice":11}]',1, now(), null),
       ('exchange', 'exchange_auto_offer', '{"buyPrice":0,"sellPrice":7000}', 1, now(), null);

