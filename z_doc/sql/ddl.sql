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
    update_time TIMESTAMP,
    del_flag    BOOLEAN      DEFAULT FALSE,
    create_id   BIGINT       DEFAULT 0,
    update_id   BIGINT       DEFAULT 0,
    tenant_id   BIGINT       NOT NULL -- 租户ID
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
CREATE INDEX del_flag_index ON z_user (del_flag);
CREATE INDEX username_index ON z_user (username);


CREATE TABLE sys_role
(
    id          BIGINT                     NOT NULL PRIMARY KEY,
    role_name   VARCHAR(64)                NOT NULL,
    remark      VARCHAR(255) DEFAULT ''    NOT NULL,
    del_flag    BOOLEAN      DEFAULT FALSE NOT NULL,
    permissions VARCHAR(64)  DEFAULT ''    NOT NULL UNIQUE, -- 添加唯一约束
    update_time TIMESTAMP                  NULL,
    create_id   BIGINT                     NULL,
    update_id   BIGINT                     NULL,
    is_lock     BOOLEAN      DEFAULT FALSE NOT NULL,
    tenant_id   BIGINT                     NOT NULL         -- 租户ID
);

-- 添加表注释
COMMENT ON TABLE sys_role IS '系统角色表';

-- 添加列注释
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.remark IS '简介';
COMMENT ON COLUMN sys_role.del_flag IS '删除与否';
COMMENT ON COLUMN sys_role.permissions IS '标识，唯一';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.create_id IS '创建人ID';
COMMENT ON COLUMN sys_role.update_id IS '更新人ID';
COMMENT ON COLUMN sys_role.is_lock IS '是否锁定';
COMMENT ON COLUMN sys_role.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX del_flag_index ON sys_role (del_flag);
