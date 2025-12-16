-- 插入10条用户测试数据
INSERT INTO z_user (id, username, pwd, phone, nickname, sex, birthday, logo, id_card, email, update_time, del_flag,
                    create_id, update_id)
VALUES (100000000000000001, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '张三', '男', '1990-05-15',
        NULL, '110101199005150012', 'zhangsan@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000002, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '李四', '女', '1992-08-22', NULL,
        '110101199208220021', 'lisi@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000003, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '13800138003', '王五', '男', '1988-12-03',
        NULL, '110101198812030015', 'wangwu@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000004, 'zhaoliu', 'e10adc3949ba59abbe56e057f20f883e', '13800138004', '赵六', '女', '1995-03-10',
        NULL, '110101199503100023', 'zhaoliu@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000005, 'sunqi', 'e10adc3949ba59abbe56e057f20f883e', '13800138005', '孙七', '未知', '1993-07-18',
        NULL, '110101199307180032', 'sunqi@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000006, 'zhouba', 'e10adc3949ba59abbe56e057f20f883e', '13800138006', '周八', '男', '1991-11-26',
        NULL, '110101199111260019', 'zhouba@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000007, 'wujiu', 'e10adc3949ba59abbe56e057f20f883e', '13800138007', '吴九', '女', '1994-01-14',
        NULL, '110101199401140027', 'wujiu@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000008, 'zhengshi', 'e10adc3949ba59abbe56e057f20f883e', '13800138008', '郑十', '男', '1989-09-30',
        NULL, '110101198909300016', 'zhengshi@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000009, 'liuxinyi', 'e10adc3949ba59abbe56e057f20f883e', '13800138009', '刘一一', '女', '1996-04-05',
        NULL, '110101199604050041', 'liuxinyi@example.com', NOW(), FALSE, NULL, NULL),
       (100000000000000010, 'chenery', 'e10adc3949ba59abbe56e057f20f883e', '13800138010', '陈二', '未知', '1997-06-12',
        NULL, '110101199706120053', 'chenery@example.com', NOW(), FALSE, NULL, NULL);


-- 修改所有表的主键类型为 DECIMAL(26,0)
ALTER TABLE z_user
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_role
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_menu
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_dept
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_role
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_role_menu
    ALTER COLUMN id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_dept
    ALTER COLUMN id TYPE DECIMAL(26, 0);

-- 修改外键关联字段
ALTER TABLE z_user_role
    ALTER COLUMN user_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_role
    ALTER COLUMN role_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role_menu
    ALTER COLUMN role_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role_menu
    ALTER COLUMN menu_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_dept
    ALTER COLUMN user_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_dept
    ALTER COLUMN dept_id TYPE DECIMAL(26, 0);

-- 修改 create_id、update_id 字段类型
ALTER TABLE z_user
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user
    ALTER COLUMN update_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role
    ALTER COLUMN update_id TYPE DECIMAL(26, 0);
ALTER TABLE z_menu
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_menu
    ALTER COLUMN update_id TYPE DECIMAL(26, 0);
ALTER TABLE z_dept
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_dept
    ALTER COLUMN update_id TYPE DECIMAL(26, 0);
ALTER TABLE z_dept
    ALTER COLUMN default_role_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_role
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role_menu
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);
ALTER TABLE z_user_dept
    ALTER COLUMN create_id TYPE DECIMAL(26, 0);

-- 修改 tenant_id 字段类型
ALTER TABLE z_user
    ALTER COLUMN tenant_id TYPE DECIMAL(26, 0);
ALTER TABLE z_role
    ALTER COLUMN tenant_id TYPE DECIMAL(26, 0);
ALTER TABLE z_menu
    ALTER COLUMN tenant_id TYPE DECIMAL(26, 0);
ALTER TABLE z_dept
    ALTER COLUMN tenant_id TYPE DECIMAL(26, 0);


-- 修改所有主键字段为 numeric(28)
ALTER TABLE public.z_user
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_user
    ALTER COLUMN create_id TYPE numeric(28);
ALTER TABLE public.z_user
    ALTER COLUMN update_id TYPE numeric(28);
ALTER TABLE public.z_user
    ALTER COLUMN tenant_id TYPE numeric(28);

ALTER TABLE public.z_role
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_role
    ALTER COLUMN create_id TYPE numeric(28);
ALTER TABLE public.z_role
    ALTER COLUMN update_id TYPE numeric(28);
ALTER TABLE public.z_role
    ALTER COLUMN tenant_id TYPE numeric(28);

ALTER TABLE public.z_menu
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_menu
    ALTER COLUMN create_id TYPE numeric(28);
ALTER TABLE public.z_menu
    ALTER COLUMN update_id TYPE numeric(28);
ALTER TABLE public.z_menu
    ALTER COLUMN tenant_id TYPE numeric(28);

ALTER TABLE public.z_dept
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_dept
    ALTER COLUMN default_role_id TYPE numeric(28);
ALTER TABLE public.z_dept
    ALTER COLUMN create_id TYPE numeric(28);
ALTER TABLE public.z_dept
    ALTER COLUMN update_id TYPE numeric(28);
ALTER TABLE public.z_dept
    ALTER COLUMN tenant_id TYPE numeric(28);

ALTER TABLE public.z_user_role
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_user_role
    ALTER COLUMN user_id TYPE numeric(28);
ALTER TABLE public.z_user_role
    ALTER COLUMN role_id TYPE numeric(28);
ALTER TABLE public.z_user_role
    ALTER COLUMN create_id TYPE numeric(28);

ALTER TABLE public.z_role_menu
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_role_menu
    ALTER COLUMN role_id TYPE numeric(28);
ALTER TABLE public.z_role_menu
    ALTER COLUMN menu_id TYPE numeric(28);
ALTER TABLE public.z_role_menu
    ALTER COLUMN create_id TYPE numeric(28);

ALTER TABLE public.z_user_dept
    ALTER COLUMN id TYPE numeric(28);
ALTER TABLE public.z_user_dept
    ALTER COLUMN user_id TYPE numeric(28);
ALTER TABLE public.z_user_dept
    ALTER COLUMN dept_id TYPE numeric(28);
ALTER TABLE public.z_user_dept
    ALTER COLUMN create_id TYPE numeric(28);


-- 修改 z_user 表
ALTER TABLE public.z_user
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_role 表
ALTER TABLE public.z_role
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_menu 表
ALTER TABLE public.z_menu
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_dept 表
ALTER TABLE public.z_dept
    ALTER COLUMN default_role_id DROP DEFAULT,
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_user_role 表
ALTER TABLE public.z_user_role
    ALTER COLUMN create_id DROP DEFAULT;

-- 修改 z_role_menu 表
ALTER TABLE public.z_role_menu
    ALTER COLUMN create_id DROP DEFAULT;

-- 修改 z_user_dept 表
ALTER TABLE public.z_user_dept
    ALTER COLUMN create_id DROP DEFAULT;


-- 修改 z_user 表
ALTER TABLE public.z_user
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_role 表
ALTER TABLE public.z_role
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_menu 表
ALTER TABLE public.z_menu
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_dept 表
ALTER TABLE public.z_dept
    ALTER COLUMN default_role_id DROP DEFAULT,
    ALTER COLUMN create_id DROP DEFAULT,
    ALTER COLUMN update_id DROP DEFAULT,
    ALTER COLUMN tenant_id DROP DEFAULT;

-- 修改 z_user_role 表
ALTER TABLE public.z_user_role
    ALTER COLUMN create_id DROP DEFAULT;

-- 修改 z_role_menu 表
ALTER TABLE public.z_role_menu
    ALTER COLUMN create_id DROP DEFAULT;

-- 修改 z_user_dept 表
ALTER TABLE public.z_user_dept
    ALTER COLUMN create_id DROP DEFAULT;


-- 删除原有的条件唯一索引
DROP INDEX IF EXISTS public.uk_z_user_username_not_deleted;

-- 创建全局唯一索引
CREATE UNIQUE INDEX uk_z_user_username ON public.z_user (username);


-- 修改 z_user 表
ALTER TABLE public.z_user
    ALTER COLUMN create_id SET DEFAULT 0;
ALTER TABLE public.z_user
    ALTER COLUMN update_id SET DEFAULT 0;
ALTER TABLE public.z_user
    ALTER COLUMN tenant_id SET DEFAULT 0;

-- 修改 z_role 表
ALTER TABLE public.z_role
    ALTER COLUMN create_id SET DEFAULT 0;
ALTER TABLE public.z_role
    ALTER COLUMN update_id SET DEFAULT 0;
ALTER TABLE public.z_role
    ALTER COLUMN tenant_id SET DEFAULT 0;

-- 修改 z_menu 表
ALTER TABLE public.z_menu
    ALTER COLUMN create_id SET DEFAULT 0;
ALTER TABLE public.z_menu
    ALTER COLUMN update_id SET DEFAULT 0;
ALTER TABLE public.z_menu
    ALTER COLUMN tenant_id SET DEFAULT 0;

-- 修改 z_dept 表
ALTER TABLE public.z_dept
    ALTER COLUMN create_id SET DEFAULT 0;
ALTER TABLE public.z_dept
    ALTER COLUMN update_id SET DEFAULT 0;
ALTER TABLE public.z_dept
    ALTER COLUMN tenant_id SET DEFAULT 0;

-- 修改 z_user_role 表 (只有 create_id)
ALTER TABLE public.z_user_role
    ALTER COLUMN create_id SET DEFAULT 0;

-- 修改 z_role_menu 表 (只有 create_id)
ALTER TABLE public.z_role_menu
    ALTER COLUMN create_id SET DEFAULT 0;

-- 修改 z_user_dept 表 (只有 create_id)
ALTER TABLE public.z_user_dept
    ALTER COLUMN create_id SET DEFAULT 0;



create table public.z_tenant
(
    id             numeric(28)                                not null
        primary key,
    name           varchar(64)                                not null,
    contact_person varchar(32)  default ''::character varying not null,
    contact_phone  varchar(20)  default ''::character varying not null,
    is_lock        boolean      default false                 not null,
    expire_time    timestamp,
    remark         varchar(255) default ''::character varying not null,
    update_time    timestamp    default now()                 not null,
    del_flag       boolean      default false                 not null,
    create_id      numeric(28)  default 0,
    update_id      numeric(28)  default 0
);

comment on table public.z_tenant is '租户表';

comment on column public.z_tenant.id is '租户ID';

comment on column public.z_tenant.name is '租户名称';

comment on column public.z_tenant.contact_person is '联系人';

comment on column public.z_tenant.contact_phone is '联系电话';

comment on column public.z_tenant.is_lock is '锁定';

comment on column public.z_tenant.expire_time is '过期时间';

comment on column public.z_tenant.remark is '备注';

comment on column public.z_tenant.update_time is '更新时间';

comment on column public.z_tenant.del_flag is '是否删除';

comment on column public.z_tenant.create_id is '创建人ID';

comment on column public.z_tenant.update_id is '更新人ID';

alter table public.z_tenant
    owner to postgres;

create index idx_z_tenant_del_flag
    on public.z_tenant (del_flag);


-- 文件存储表
create table public.z_file
(
    id            numeric(28)                                not null
        primary key,
    original_name varchar(512) default ''::character varying not null,
    file_key      varchar(512) default ''::character varying not null,
    extension     varchar(32)  default ''::character varying not null,
    file_size     bigint       default 0                     not null,
    content_type  varchar(128) default ''::character varying not null,
    update_time   timestamp    default now()                 not null,
    create_id     numeric(28)  default 0,
    update_id     numeric(28)  default 0,
    del_flag      boolean      default false                 not null
);

comment on table public.z_file is '文件存储表';
comment on column public.z_file.id is '文件ID(雪花算法，作为文件名主体)';
comment on column public.z_file.original_name is '原始文件名';
comment on column public.z_file.file_key is '对象存储Key(ID.后缀)';
comment on column public.z_file.extension is '文件后缀';
comment on column public.z_file.file_size is '文件大小(字节)';
comment on column public.z_file.content_type is 'MIME类型';
comment on column public.z_file.update_time is '更新时间';
comment on column public.z_file.create_id is '创建人ID';
comment on column public.z_file.update_id is '更新人ID';
comment on column public.z_file.del_flag is '是否删除';

alter table public.z_file owner to postgres;

create index idx_z_file_del_flag on public.z_file (del_flag);
create index idx_z_file_file_key on public.z_file (file_key);





-- 1. 创建租户数据
INSERT INTO z_tenant (id, name, manager_user_id, manager_user_phone, is_lock, expire_time, remark, update_time, del_flag, create_id, update_id)
VALUES (2025121300000000000001000000, '默认租户', '2025121314461087000001000000', '13800138000', false, '2026-12-31 23:59:59', '系统默认租户', now(), false, 0, 0);

-- 2. 创建部门数据
INSERT INTO z_dept (id, name, pid, default_role_id, deep, sort, has_children, is_lock, del_flag, remark, create_id, update_id, update_time, tenant_id)
VALUES
    (2025121300000000000001000001, '蜀汉集团', 0, null, 1, 1, true, false, false, '刘备集团总部', 0, 0, now(), 2025121300000000000001000000),
    (2025121300000000000001000002, '技术部', 2025121300000000000001000001, null, 2, 1, false, false, false, '负责技术研发', 0, 0, now(), 2025121300000000000001000000),
    (2025121300000000000001000003, '市场部', 2025121300000000000001000001, null, 2, 2, false, false, false, '负责市场推广', 0, 0, now(), 2025121300000000000001000000),
    (2025121300000000000001000004, '曹魏集团', 0, null, 1, 2, false, false, false, '曹操集团', 0, 0, now(), 2025121300000000000001000000);

-- 3. 创建角色数据
INSERT INTO z_role (id, role_name, remark, del_flag, permissions, update_time, create_id, update_id, is_lock, tenant_id)
VALUES
    (2025121300000000000001000001, '超级管理员', '拥有所有权限', false, 'admin', now(), 0, 0, false, 2025121300000000000001000000),
    (2025121300000000000001000002, '部门经理', '部门管理权限', false, 'manager', now(), 0, 0, false, 2025121300000000000001000000),
    (2025121300000000000001000003, '普通员工', '基础权限', false, 'employee', now(), 0, 0, false, 2025121300000000000001000000),
    (2025121300000000000001000004, '访客', '只读权限', false, 'guest', now(), 0, 0, false, 2025121300000000000001000000);

-- 4. 创建菜单数据
INSERT INTO z_menu (id, pid, path, name, title, icon, component, redirect, sort, deep, menu_type_cd, permissions, is_hidden, has_children, is_link, is_full, is_affix, update_time, create_id, update_id, del_flag, tenant_id)
VALUES
-- 一级菜单
(2025121300000000000001000001, 0, '/system', 'system', '系统管理', 'setting', 'LAYOUT', '', 1, 1, 'CATALOG', '', false, true, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000002, 0, '/dashboard', 'dashboard', '工作台', 'home-filled', '/dashboard/index', '', 0, 1, 'MENU', '', false, false, false, false, true, now(), 0, 0, false, 2025121300000000000001000000),
-- 二级菜单
(2025121300000000000001000003, 2025121300000000000001000001, '/system/user', 'user', '用户管理', 'user', '/system/user/index', '', 1, 2, 'MENU', 'system:user:list', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000004, 2025121300000000000001000001, '/system/role', 'role', '角色管理', 'avatar', '/system/role/index', '', 2, 2, 'MENU', 'system:role:list', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000005, 2025121300000000000001000001, '/system/menu', 'menu', '菜单管理', 'menu', '/system/menu/index', '', 3, 2, 'MENU', 'system:menu:list', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000006, 2025121300000000000001000001, '/system/dept', 'dept', '部门管理', 'office-building', '/system/dept/index', '', 4, 2, 'MENU', 'system:dept:list', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
-- 按钮权限
(2025121300000000000001000007, 2025121300000000000001000003, '', '', '新增用户', '', '', '', 1, 3, 'BUTTON', 'system:user:add', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000008, 2025121300000000000001000003, '', '', '编辑用户', '', '', '', 2, 3, 'BUTTON', 'system:user:edit', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000),
(2025121300000000000001000009, 2025121300000000000001000003, '', '', '删除用户', '', '', '', 3, 3, 'BUTTON', 'system:user:delete', false, false, false, false, false, now(), 0, 0, false, 2025121300000000000001000000);

-- 5. 用户-角色关联
INSERT INTO z_user_role (id, user_id, role_id, create_id)
VALUES
-- 刘备: 超级管理员
(2025121300000000000001000001, 2025121314461087000001000000, 2025121300000000000001000001, 0),
-- 曹操: 超级管理员
(2025121300000000000001000002, 2025121317433889000001000000, 2025121300000000000001000001, 0),
-- 关羽: 部门经理
(2025121300000000000001000003, 2025121317211139900001000000, 2025121300000000000001000002, 0),
-- 张飞: 普通员工
(2025121300000000000001000004, 2025121317265330200001000000, 2025121300000000000001000003, 0);

-- 6. 用户-部门关联
INSERT INTO z_user_dept (id, user_id, dept_id, create_id)
VALUES
-- 刘备: 蜀汉集团
(2025121300000000000001000001, 2025121314461087000001000000, 2025121300000000000001000001, 0),
-- 曹操: 曹魏集团
(2025121300000000000001000002, 2025121317433889000001000000, 2025121300000000000001000004, 0),
-- 关羽: 技术部
(2025121300000000000001000003, 2025121317211139900001000000, 2025121300000000000001000002, 0),
-- 张飞: 市场部
(2025121300000000000001000004, 2025121317265330200001000000, 2025121300000000000001000003, 0);

-- 7. 角色-菜单关联
INSERT INTO z_role_menu (id, role_id, menu_id, create_id)
VALUES
-- 超级管理员拥有所有菜单
(2025121300000000000001000001, 2025121300000000000001000001, 2025121300000000000001000001, 0),
(2025121300000000000001000002, 2025121300000000000001000001, 2025121300000000000001000002, 0),
(2025121300000000000001000003, 2025121300000000000001000001, 2025121300000000000001000003, 0),
(2025121300000000000001000004, 2025121300000000000001000001, 2025121300000000000001000004, 0),
(2025121300000000000001000005, 2025121300000000000001000001, 2025121300000000000001000005, 0),
(2025121300000000000001000006, 2025121300000000000001000001, 2025121300000000000001000006, 0),
(2025121300000000000001000007, 2025121300000000000001000001, 2025121300000000000001000007, 0),
(2025121300000000000001000008, 2025121300000000000001000001, 2025121300000000000001000008, 0),
(2025121300000000000001000009, 2025121300000000000001000001, 2025121300000000000001000009, 0),
-- 部门经理拥有部分菜单
(2025121300000000000001000010, 2025121300000000000001000002, 2025121300000000000001000002, 0),
(2025121300000000000001000011, 2025121300000000000001000002, 2025121300000000000001000003, 0),
(2025121300000000000001000012, 2025121300000000000001000002, 2025121300000000000001000006, 0),
-- 普通员工只有工作台
(2025121300000000000001000013, 2025121300000000000001000003, 2025121300000000000001000002, 0);

-- 8. 更新用户表的租户ID
UPDATE z_user SET tenant_id = 2025121300000000000001000000
WHERE id IN (2025121314461087000001000000, 2025121317433889000001000000, 2025121317211139900001000000, 2025121317265330200001000000);
