-- auto-generated definition
create table z_user
(
    id          bigint                    not null comment '用户ID'
        primary key,
    username    varchar(32)               not null comment '用户名',
    pwd         varchar(512)              not null comment '密码',
    phone       varchar(20)               null comment '手机号',
    nickname    varchar(32)               null comment '昵称',
    sex         enum ('未知', '男', '女') null comment '性别',
    birthday    date                      null comment '生日',
    logo        mediumtext                null comment '头像base64',
    id_card     varchar(32)               null comment '身份证',
    email       varchar(256)              null comment '邮箱地址',
    update_time datetime                  null comment '更新时间',
    del_flag    boolean default false comment '是否删除',
    create_id   bigint                    null comment '创建人ID',
    update_id   bigint                    null comment '更新人ID'
)
    comment '系统用户表' row_format = DYNAMIC;


create index username_index
    on z_user (username);
create index del_flag_index
    on z_user (del_flag);



