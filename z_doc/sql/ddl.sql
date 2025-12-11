-- 创建系统用户表
CREATE TABLE z_user
(
    id          BIGINT       NOT NULL PRIMARY KEY,
    username    VARCHAR(32)  NOT NULL,
    pwd         VARCHAR(512) NOT NULL,
    phone       VARCHAR(20)  DEFAULT '',
    nickname    VARCHAR(32)  DEFAULT '',
    sex         VARCHAR(16)  DEFAULT '未知',
    birthday    DATE,
    logo        TEXT         DEFAULT '',
    id_card     VARCHAR(32)  DEFAULT '',
    email       VARCHAR(256) DEFAULT '',
    update_time TIMESTAMP    NOT NULL,
    del_flag    BOOLEAN      DEFAULT FALSE,
    create_id   BIGINT       DEFAULT 0,
    update_id   BIGINT       DEFAULT 0,
    tenant_id   BIGINT       DEFAULT 0 -- 租户ID，默认 0
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

-- 创建系统角色表（表前缀改为 z_）
CREATE TABLE z_role
(
    id          BIGINT                     NOT NULL PRIMARY KEY,
    role_name   VARCHAR(64)                NOT NULL,
    remark      VARCHAR(255) DEFAULT ''    NOT NULL,
    del_flag    BOOLEAN      DEFAULT FALSE NOT NULL,
    permissions VARCHAR(64)  DEFAULT ''    NOT NULL UNIQUE, -- 唯一约束
    update_time TIMESTAMP                  NOT NULL,
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
    update_time  TIMESTAMP                  NOT NULL,
    create_id    BIGINT       DEFAULT 0,
    update_id    BIGINT       DEFAULT 0,
    del_flag     BOOLEAN      DEFAULT FALSE NOT NULL,
    tenant_id    BIGINT       DEFAULT 0 -- 租户ID，默认 0
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
    update_time     TIMESTAMP                  NOT NULL, -- 规范：update_time 字段为 NOT NULL
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
