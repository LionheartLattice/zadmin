create function update_updated_at_column() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$;

alter function update_updated_at_column() owner to postgres;



create table public.z_user
(
    id          decimal(26,0)              not null
        primary key,
    username    varchar(32)                not null,
    pwd         varchar(512)               not null,
    phone       varchar(20)  default ''::character varying,
    nickname    varchar(32)  default ''::character varying,
    sex         varchar(16)  default '未知'::character varying,
    birthday    date,
    logo        text         default ''::text,
    id_card     varchar(32)  default ''::character varying,
    email       varchar(256) default ''::character varying,
    update_time timestamp    default now() not null,
    del_flag    boolean      default false,
    create_id   decimal(26,0) default 0,
    update_id   decimal(26,0) default 0,
    tenant_id   decimal(26,0) default 0
);

comment on table public.z_user is '系统用户表';

comment on column public.z_user.id is '用户ID';

comment on column public.z_user.username is '用户名';

comment on column public.z_user.pwd is '密码';

comment on column public.z_user.phone is '手机号';

comment on column public.z_user.nickname is '昵称';

comment on column public.z_user.sex is '性别 未知-男-女';

comment on column public.z_user.birthday is '生日';

comment on column public.z_user.logo is '头像base64';

comment on column public.z_user.id_card is '身份证';

comment on column public.z_user.email is '邮箱地址';

comment on column public.z_user.update_time is '更新时间';

comment on column public.z_user.del_flag is '是否删除';

comment on column public.z_user.create_id is '创建人ID';

comment on column public.z_user.update_id is '更新人ID';

comment on column public.z_user.tenant_id is '租户ID';

alter table public.z_user
    owner to postgres;

create index del_flag_index
    on public.z_user (del_flag);

create index username_index
    on public.z_user (username);

create index tenant_id_index
    on public.z_user (tenant_id);

create table public.z_role
(
    id          decimal(26,0)                              not null
        primary key,
    role_name   varchar(64)                                not null,
    remark      varchar(255) default ''::character varying not null,
    del_flag    boolean      default false                 not null,
    permissions varchar(64)  default ''::character varying not null
        unique,
    update_time timestamp    default now()                 not null,
    create_id   decimal(26,0) default 0,
    update_id   decimal(26,0) default 0,
    is_lock     boolean      default false                 not null,
    tenant_id   decimal(26,0) default 0
);

comment on table public.z_role is '系统角色表';

comment on column public.z_role.id is '角色ID';

comment on column public.z_role.role_name is '角色名称';

comment on column public.z_role.remark is '简介';

comment on column public.z_role.del_flag is '删除与否';

comment on column public.z_role.permissions is '标识，唯一';

comment on column public.z_role.update_time is '更新时间';

comment on column public.z_role.create_id is '创建人ID';

comment on column public.z_role.update_id is '更新人ID';

comment on column public.z_role.is_lock is '是否锁定';

comment on column public.z_role.tenant_id is '租户ID';

alter table public.z_role
    owner to postgres;

create table public.z_menu
(
    id           decimal(26,0)                              not null
        primary key,
    pid          decimal(26,0)                              not null,
    path         varchar(255) default ''::character varying not null,
    name         varchar(64)  default ''::character varying not null,
    title        varchar(64)                                not null,
    icon         text         default ''::text              not null,
    component    varchar(255) default ''::character varying not null,
    redirect     varchar(255) default ''::character varying not null,
    sort         integer                                    not null,
    deep         integer                                    not null,
    menu_type_cd varchar(32)  default ''::character varying not null,
    permissions  varchar(64)  default ''::character varying not null,
    is_hidden    boolean      default false,
    has_children boolean      default false                 not null,
    is_link      boolean      default false,
    is_full      boolean      default false,
    is_affix     boolean      default false,
    update_time  timestamp    default now()                 not null,
    create_id    decimal(26,0) default 0,
    update_id    decimal(26,0) default 0,
    del_flag     boolean      default false                 not null,
    tenant_id    decimal(26,0) default 0
);

comment on table public.z_menu is '系统菜单表';

comment on column public.z_menu.id is '菜单表id';

comment on column public.z_menu.pid is '父级id';

comment on column public.z_menu.path is '路径';

comment on column public.z_menu.name is '路由名称';

comment on column public.z_menu.title is '标题';

comment on column public.z_menu.icon is 'icon图标';

comment on column public.z_menu.component is '组件路径';

comment on column public.z_menu.redirect is '外链地址';

comment on column public.z_menu.sort is '排序';

comment on column public.z_menu.deep is '层级';

comment on column public.z_menu.menu_type_cd is '菜单类型 （字典表menu_type）';

comment on column public.z_menu.permissions is '按钮权限';

comment on column public.z_menu.is_hidden is '是否隐藏';

comment on column public.z_menu.has_children is '是否有子级';

comment on column public.z_menu.is_link is '路由外链时填写的访问地址';

comment on column public.z_menu.is_full is '菜单是否全屏';

comment on column public.z_menu.is_affix is '菜单是否固定在标签页';

comment on column public.z_menu.update_time is '更新时间';

comment on column public.z_menu.create_id is '创建人ID';

comment on column public.z_menu.update_id is '更新人ID';

comment on column public.z_menu.del_flag is '是否删除';

comment on column public.z_menu.tenant_id is '租户ID';

alter table public.z_menu
    owner to postgres;

create table public.z_dept
(
    id              decimal(26,0)              not null
        primary key,
    name            varchar(64)                not null,
    pid             decimal(26,0)              not null,
    default_role_id decimal(26,0) default 0,
    deep            integer,
    sort            integer,
    has_children    boolean      default false,
    is_lock         boolean      default false not null,
    del_flag        boolean      default false not null,
    remark          varchar(128) default ''::character varying,
    create_id       decimal(26,0) default 0,
    update_id       decimal(26,0) default 0,
    update_time     timestamp    default now() not null,
    tenant_id       decimal(26,0) default 0
);

comment on table public.z_dept is '部门表';

comment on column public.z_dept.id is '部门ID';

comment on column public.z_dept.name is '部门名称';

comment on column public.z_dept.pid is '父级ID';

comment on column public.z_dept.default_role_id is '默认分配角色ID';

comment on column public.z_dept.deep is '层级';

comment on column public.z_dept.sort is '排序';

comment on column public.z_dept.has_children is '是否有子级';

comment on column public.z_dept.is_lock is '是否锁定';

comment on column public.z_dept.del_flag is '删除标识';

comment on column public.z_dept.remark is '备注';

comment on column public.z_dept.create_id is '创建人ID';

comment on column public.z_dept.update_id is '更新人ID';

comment on column public.z_dept.update_time is '更新时间';

comment on column public.z_dept.tenant_id is '租户ID';

alter table public.z_dept
    owner to postgres;

create table public.z_user_role
(
    id        decimal(26,0) not null
        primary key,
    user_id   decimal(26,0) not null,
    role_id   decimal(26,0) not null,
    create_id decimal(26,0) default 0,
    constraint unique_user_role
        unique (user_id, role_id)
);

comment on table public.z_user_role is '用户角色关联表';

comment on column public.z_user_role.id is '关联ID';

comment on column public.z_user_role.user_id is '用户ID';

comment on column public.z_user_role.role_id is '角色ID';

comment on column public.z_user_role.create_id is '创建人ID';

alter table public.z_user_role
    owner to postgres;

create index user_id_index
    on public.z_user_role (user_id);

create index role_id_index
    on public.z_user_role (role_id);

create table public.z_role_menu
(
    id        decimal(26,0) not null
        primary key,
    role_id   decimal(26,0) not null,
    menu_id   decimal(26,0) not null,
    create_id decimal(26,0) default 0,
    constraint unique_role_menu
        unique (role_id, menu_id)
);

comment on table public.z_role_menu is '角色菜单关联表';

comment on column public.z_role_menu.id is '关联ID';

comment on column public.z_role_menu.role_id is '角色ID';

comment on column public.z_role_menu.menu_id is '菜单ID';

comment on column public.z_role_menu.create_id is '创建人ID';

alter table public.z_role_menu
    owner to postgres;

create index menu_id_index
    on public.z_role_menu (menu_id);

create table public.z_user_dept
(
    id        bigint not null
        primary key,
    user_id   bigint not null,
    dept_id   bigint not null,
    create_id bigint default 0,
    constraint unique_user_dept
        unique (user_id, dept_id)
);

comment on table public.z_user_dept is '用户部门关联表';

comment on column public.z_user_dept.id is '关联ID';

comment on column public.z_user_dept.user_id is '用户ID';

comment on column public.z_user_dept.dept_id is '部门ID';

comment on column public.z_user_dept.create_id is '创建人ID';

alter table public.z_user_dept
    owner to postgres;

create index dept_id_index
    on public.z_user_dept (dept_id);

