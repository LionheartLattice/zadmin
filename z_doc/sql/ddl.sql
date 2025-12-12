-- 创建触发器函数，用于自动更新 update_time
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 创建系统用户表
CREATE TABLE z_user
(
    id          BIGINT                     NOT NULL PRIMARY KEY,
    username    VARCHAR(32)                NOT NULL,
    pwd         VARCHAR(512)               NOT NULL,
    phone       VARCHAR(20)  DEFAULT '',
    nickname    VARCHAR(32)  DEFAULT '',
    sex         VARCHAR(16)  DEFAULT '未知',
    birthday    DATE,
    logo        TEXT         DEFAULT '',
    id_card     VARCHAR(32)  DEFAULT '',
    email       VARCHAR(256) DEFAULT '',
    update_time TIMESTAMP    DEFAULT NOW() NOT NULL, -- 数据库自动维护
    del_flag    BOOLEAN      DEFAULT FALSE,
    create_id   BIGINT       DEFAULT 0,
    update_id   BIGINT       DEFAULT 0,
    tenant_id   BIGINT       DEFAULT 0               -- 租户ID，默认 0
);

COMMENT ON TABLE z_user IS '系统用户表';
COMMENT ON COLUMN z_user.id IS '用户ID';
COMMENT ON COLUMN z_user.username IS '用户名';
COMMENT ON COLUMN z_user.pwd IS '密码';
COMMENT ON COLUMN z_user.phone IS '手机号';
COMMENT ON COLUMN z_user.nickname IS '昵称';
COMMENT ON COLUMN z_user.sex IS '性别 未知-男-女';
COMMENT ON COLUMN z_user.birthday IS '生日';
COMMENT ON COLUMN z_user.logo IS '头像base64';
COMMENT ON COLUMN z_user.id_card IS '身份证';
COMMENT ON COLUMN z_user.email IS '邮箱地址';
COMMENT ON COLUMN z_user.update_time IS '更新时间';
COMMENT ON COLUMN z_user.del_flag IS '是否删除';
COMMENT ON COLUMN z_user.create_id IS '创建人ID';
COMMENT ON COLUMN z_user.update_id IS '更新人ID';
COMMENT ON COLUMN z_user.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS del_flag_index ON z_user (del_flag);
CREATE INDEX IF NOT EXISTS username_index ON z_user (username);
CREATE INDEX IF NOT EXISTS tenant_id_index ON z_user (tenant_id);

-- 为 z_user 表添加触发器
CREATE TRIGGER update_z_user_updated_at
    BEFORE UPDATE
    ON z_user
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 创建系统角色表（表前缀改为 z_）
CREATE TABLE z_role
(
    id          BIGINT                     NOT NULL PRIMARY KEY,
    role_name   VARCHAR(64)                NOT NULL,
    remark      VARCHAR(255) DEFAULT ''    NOT NULL,
    del_flag    BOOLEAN      DEFAULT FALSE NOT NULL,
    permissions VARCHAR(64)  DEFAULT ''    NOT NULL UNIQUE, -- 唯一约束
    update_time TIMESTAMP    DEFAULT NOW() NOT NULL,        -- 数据库自动维护
    create_id   BIGINT       DEFAULT 0,
    update_id   BIGINT       DEFAULT 0,
    is_lock     BOOLEAN      DEFAULT FALSE NOT NULL,
    tenant_id   BIGINT       DEFAULT 0                      -- 租户ID，默认 0
);

COMMENT ON TABLE z_role IS '系统角色表';
COMMENT ON COLUMN z_role.id IS '角色ID';
COMMENT ON COLUMN z_role.role_name IS '角色名称';
COMMENT ON COLUMN z_role.remark IS '简介';
COMMENT ON COLUMN z_role.del_flag IS '删除与否';
COMMENT ON COLUMN z_role.permissions IS '标识，唯一';
COMMENT ON COLUMN z_role.update_time IS '更新时间';
COMMENT ON COLUMN z_role.create_id IS '创建人ID';
COMMENT ON COLUMN z_role.update_id IS '更新人ID';
COMMENT ON COLUMN z_role.is_lock IS '是否锁定';
COMMENT ON COLUMN z_role.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS del_flag_index ON z_role (del_flag);
CREATE INDEX IF NOT EXISTS tenant_id_index ON z_role (tenant_id);

-- 为 z_role 表添加触发器
CREATE TRIGGER update_z_role_updated_at
    BEFORE UPDATE
    ON z_role
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 创建系统菜单表（表前缀改为 z_，PostgreSQL 建表语句，已移除不需要的字段，添加 tenant_id，并符合项目规范）
CREATE TABLE z_menu
(
    id           BIGINT                     NOT NULL PRIMARY KEY,
    pid          BIGINT                     NOT NULL,
    path         VARCHAR(255) DEFAULT ''    NOT NULL,
    name         VARCHAR(64)  DEFAULT ''    NOT NULL,
    title        VARCHAR(64)                NOT NULL,
    icon         TEXT         DEFAULT ''    NOT NULL,
    component    VARCHAR(255) DEFAULT ''    NOT NULL,
    redirect     VARCHAR(255) DEFAULT ''    NOT NULL,
    sort         INTEGER                    NOT NULL,
    deep         INTEGER                    NOT NULL,
    menu_type_cd VARCHAR(32)  DEFAULT ''    NOT NULL,
    permissions  VARCHAR(64)  DEFAULT ''    NOT NULL,
    is_hidden    BOOLEAN      DEFAULT FALSE,
    has_children BOOLEAN      DEFAULT FALSE NOT NULL,
    is_link      BOOLEAN      DEFAULT FALSE,
    is_full      BOOLEAN      DEFAULT FALSE,
    is_affix     BOOLEAN      DEFAULT FALSE,
    update_time  TIMESTAMP    DEFAULT NOW() NOT NULL, -- 数据库自动维护
    create_id    BIGINT       DEFAULT 0,
    update_id    BIGINT       DEFAULT 0,
    del_flag     BOOLEAN      DEFAULT FALSE NOT NULL,
    tenant_id    BIGINT       DEFAULT 0               -- 租户ID，默认 0
);

COMMENT ON TABLE z_menu IS '系统菜单表';
COMMENT ON COLUMN z_menu.id IS '菜单表id';
COMMENT ON COLUMN z_menu.pid IS '父级id';
COMMENT ON COLUMN z_menu.path IS '路径';
COMMENT ON COLUMN z_menu.name IS '路由名称';
COMMENT ON COLUMN z_menu.title IS '标题';
COMMENT ON COLUMN z_menu.icon IS 'icon图标';
COMMENT ON COLUMN z_menu.component IS '组件路径';
COMMENT ON COLUMN z_menu.redirect IS '外链地址';
COMMENT ON COLUMN z_menu.sort IS '排序';
COMMENT ON COLUMN z_menu.deep IS '层级';
COMMENT ON COLUMN z_menu.menu_type_cd IS '菜单类型 （字典表menu_type）';
COMMENT ON COLUMN z_menu.permissions IS '按钮权限';
COMMENT ON COLUMN z_menu.is_hidden IS '是否隐藏';
COMMENT ON COLUMN z_menu.has_children IS '是否有子级';
COMMENT ON COLUMN z_menu.is_link IS '路由外链时填写的访问地址';
COMMENT ON COLUMN z_menu.is_full IS '菜单是否全屏';
COMMENT ON COLUMN z_menu.is_affix IS '菜单是否固定在标签页';
COMMENT ON COLUMN z_menu.update_time IS '更新时间';
COMMENT ON COLUMN z_menu.create_id IS '创建人ID';
COMMENT ON COLUMN z_menu.update_id IS '更新人ID';
COMMENT ON COLUMN z_menu.del_flag IS '是否删除';
COMMENT ON COLUMN z_menu.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS del_flag_index ON z_menu (del_flag);
CREATE INDEX IF NOT EXISTS tenant_id_index ON z_menu (tenant_id);

-- 为 z_menu 表添加触发器
CREATE TRIGGER update_z_menu_updated_at
    BEFORE UPDATE
    ON z_menu
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 创建部门表（PostgreSQL 建表语句，表前缀改为 z_，符合项目规范）
CREATE TABLE z_dept
(
    id              BIGINT                     NOT NULL PRIMARY KEY,
    name            VARCHAR(64)                NOT NULL,
    pid             BIGINT                     NOT NULL,
    default_role_id BIGINT       DEFAULT 0,
    deep            INTEGER                    NULL,
    sort            INTEGER                    NULL,
    has_children    BOOLEAN      DEFAULT FALSE,
    is_lock         BOOLEAN      DEFAULT FALSE NOT NULL,
    del_flag        BOOLEAN      DEFAULT FALSE NOT NULL,
    remark          VARCHAR(128) DEFAULT ''    NULL,
    create_id       BIGINT       DEFAULT 0,
    update_id       BIGINT       DEFAULT 0,
    update_time     TIMESTAMP    DEFAULT NOW() NOT NULL, -- 数据库自动维护
    tenant_id       BIGINT       DEFAULT 0               -- 租户ID，默认 0
);

COMMENT ON TABLE z_dept IS '部门表';
COMMENT ON COLUMN z_dept.id IS '部门ID';
COMMENT ON COLUMN z_dept.name IS '部门名称';
COMMENT ON COLUMN z_dept.pid IS '父级ID';
COMMENT ON COLUMN z_dept.default_role_id IS '默认分配角色ID';
COMMENT ON COLUMN z_dept.deep IS '层级';
COMMENT ON COLUMN z_dept.sort IS '排序';
COMMENT ON COLUMN z_dept.has_children IS '是否有子级';
COMMENT ON COLUMN z_dept.is_lock IS '是否锁定';
COMMENT ON COLUMN z_dept.del_flag IS '删除标识';
COMMENT ON COLUMN z_dept.remark IS '备注';
COMMENT ON COLUMN z_dept.create_id IS '创建人ID';
COMMENT ON COLUMN z_dept.update_id IS '更新人ID';
COMMENT ON COLUMN z_dept.update_time IS '更新时间';
COMMENT ON COLUMN z_dept.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS del_flag_index ON z_dept (del_flag);
CREATE INDEX IF NOT EXISTS tenant_id_index ON z_dept (tenant_id);

-- 为 z_dept 表添加触发器
CREATE TRIGGER update_z_dept_updated_at
    BEFORE UPDATE
    ON z_dept
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- 创建用户角色关联表（多对多）
CREATE TABLE z_user_role
(
    id        BIGINT NOT NULL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    role_id   BIGINT NOT NULL,
    create_id BIGINT DEFAULT 0
);

COMMENT ON TABLE z_user_role IS '用户角色关联表';
COMMENT ON COLUMN z_user_role.id IS '关联ID';
COMMENT ON COLUMN z_user_role.user_id IS '用户ID';
COMMENT ON COLUMN z_user_role.role_id IS '角色ID';
COMMENT ON COLUMN z_user_role.create_id IS '创建人ID';

-- 创建唯一约束防止重复关联
ALTER TABLE z_user_role
    ADD CONSTRAINT unique_user_role UNIQUE (user_id, role_id);

-- 创建索引
CREATE INDEX IF NOT EXISTS user_id_index ON z_user_role (user_id);
CREATE INDEX IF NOT EXISTS role_id_index ON z_user_role (role_id);

-- 创建角色菜单关联表（多对多）
CREATE TABLE z_role_menu
(
    id        BIGINT NOT NULL PRIMARY KEY,
    role_id   BIGINT NOT NULL,
    menu_id   BIGINT NOT NULL,
    create_id BIGINT DEFAULT 0
);

COMMENT ON TABLE z_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN z_role_menu.id IS '关联ID';
COMMENT ON COLUMN z_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN z_role_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN z_role_menu.create_id IS '创建人ID';

-- 创建唯一约束防止重复关联
ALTER TABLE z_role_menu
    ADD CONSTRAINT unique_role_menu UNIQUE (role_id, menu_id);

-- 创建索引
CREATE INDEX IF NOT EXISTS role_id_index ON z_role_menu (role_id);
CREATE INDEX IF NOT EXISTS menu_id_index ON z_role_menu (menu_id);

-- 创建用户部门关联表（多对多）
CREATE TABLE z_user_dept
(
    id        BIGINT NOT NULL PRIMARY KEY,
    user_id   BIGINT NOT NULL,
    dept_id   BIGINT NOT NULL,
    create_id BIGINT DEFAULT 0
);

COMMENT ON TABLE z_user_dept IS '用户部门关联表';
COMMENT ON COLUMN z_user_dept.id IS '关联ID';
COMMENT ON COLUMN z_user_dept.user_id IS '用户ID';
COMMENT ON COLUMN z_user_dept.dept_id IS '部门ID';
COMMENT ON COLUMN z_user_dept.create_id IS '创建人ID';

-- 创建唯一约束防止重复关联
ALTER TABLE z_user_dept
    ADD CONSTRAINT unique_user_dept UNIQUE (user_id, dept_id);

-- 创建索引
CREATE INDEX IF NOT EXISTS user_id_index ON z_user_dept (user_id);
CREATE INDEX IF NOT EXISTS dept_id_index ON z_user_dept (dept_id);


-- 插入用户测试数据
INSERT INTO z_user (id, username, pwd, phone, nickname, sex, birthday, logo, id_card, email, del_flag, create_id,
                    update_id, tenant_id)
VALUES (1, 'zhangsan', 'hashedpassword123', '13800000001', '张三', '男', '1990-01-01', '', '110101199001011234',
        'zhangsan@example.com', FALSE, 0, 0, 1),
       (2, 'lisi', 'hashedpassword123', '13800000002', '李四', '女', '1992-02-02', '', '110101199202022345',
        'lisi@example.com', FALSE, 0, 0, 1);

-- 插入角色测试数据
INSERT INTO z_role (id, role_name, remark, del_flag, permissions, create_id, update_id, is_lock, tenant_id)
VALUES (3, '管理员', '系统管理员角色', FALSE, 'admin', 0, 0, FALSE, 1),
       (4, '普通用户', '普通用户角色', FALSE, 'user', 0, 0, FALSE, 1);

-- 插入菜单测试数据
INSERT INTO z_menu (id, pid, path, name, title, icon, component, redirect, sort, deep, menu_type_cd, permissions,
                    is_hidden, has_children, is_link, is_full, is_affix, create_id, update_id, del_flag, tenant_id)
VALUES (5, 0, '/home', 'home', '首页', 'home', '/home', '', 1, 1, 'page', 'home:view', FALSE, FALSE, FALSE, FALSE, TRUE,
        0, 0, FALSE, 1),
       (6, 0, '/user', 'user', '用户管理', 'user', '/user', '', 2, 1, 'page', 'user:view', FALSE, TRUE, FALSE, FALSE,
        FALSE, 0, 0, FALSE, 1),
       (7, 6, '/user/list', 'user-list', '用户列表', 'list', '/user/list', '', 1, 2, 'page', 'user:list', FALSE, FALSE,
        FALSE, FALSE, FALSE, 0, 0, FALSE, 1),
       (8, 0, '/role', 'role', '角色管理', 'role', '/role', '', 3, 1, 'page', 'role:view', FALSE, FALSE, FALSE, FALSE,
        FALSE, 0, 0, FALSE, 1);

-- 插入部门测试数据
INSERT INTO z_dept (id, name, pid, default_role_id, deep, sort, has_children, is_lock, del_flag, remark, create_id,
                    update_id, tenant_id)
VALUES (9, '技术部', 0, 3, 1, 1, FALSE, FALSE, FALSE, '技术部门', 0, 0, 1);

-- 插入用户角色关联测试数据
INSERT INTO z_user_role (id, user_id, role_id, create_id)
VALUES (10, 1, 3, 0), -- 张三分配管理员角色
       (11, 2, 4, 0);
-- 李四分配普通用户角色

-- 插入角色菜单关联测试数据
INSERT INTO z_role_menu (id, role_id, menu_id, create_id)
VALUES (12, 3, 5, 0), -- 管理员分配首页
       (13, 3, 6, 0), -- 管理员分配用户管理
       (14, 3, 7, 0), -- 管理员分配用户列表
       (15, 3, 8, 0), -- 管理员分配角色管理
       (16, 4, 5, 0);
-- 普通用户分配首页

-- 插入用户部门关联测试数据
INSERT INTO z_user_dept (id, user_id, dept_id, create_id)
VALUES (17, 1, 9, 0), -- 张三分配到技术部
       (18, 2, 9, 0); -- 李四分配到技术部


select * from z_user;



