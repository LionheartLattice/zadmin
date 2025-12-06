CREATE TABLE `sys_client`
(
    `client_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
    `client_key`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '客户端key',
    `client_secret`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '客户端秘钥',
    `grant_type_cd`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT 'password' COMMENT '授权类型',
    `device_type_cd`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '设备类型',
    `active_timeout`   int                                                      DEFAULT '1800' COMMENT 'token活跃超时时间',
    `timeout`          int                                                      DEFAULT '604800' COMMENT 'token固定超时',
    `client_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT '0' COMMENT '状态（正常 禁用）',
    `del_flag`         enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'F' COMMENT '删除标志',
    `create_dept`      bigint                                                   DEFAULT NULL COMMENT '创建部门',
    `create_id`        bigint                                                   DEFAULT NULL COMMENT '创建者',
    `create_time`      datetime                                                 DEFAULT NULL COMMENT '创建时间',
    `update_id`        bigint                                                   DEFAULT NULL COMMENT '更新者',
    `update_time`      datetime                                                 DEFAULT NULL COMMENT '更新时间',
    `remark`           varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '备注',
    `is_lock`          enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT 'F' COMMENT '是否锁定',
    PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin
  ROW_FORMAT = DYNAMIC COMMENT ='系统授权表';

CREATE TABLE `sys_config`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '参数配置ID',
    `config_name`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '参数名',
    `config_key`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '参数key',
    `config_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '参数value',
    `is_lock`      enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '是否锁定',
    `create_id`    bigint                                                          DEFAULT NULL COMMENT '创建人ID',
    `create_time`  datetime                                                        DEFAULT NULL COMMENT '创建时间',
    `update_id`    bigint                                                          DEFAULT NULL COMMENT '更新人ID',
    `update_time`  datetime                                                        DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 16
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='参数配置表';

CREATE TABLE `sys_data_role`
(
    `id`            bigint                                                          NOT NULL AUTO_INCREMENT COMMENT '数据角色ID',
    `role_name`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '角色名称',
    `data_scope_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '数据权限，data_scope字典',
    `remark`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '简介',
    `del_flag`      enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
    `is_lock`       enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '是否锁定',
    `create_time`   datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    `create_id`     bigint                                                                   DEFAULT NULL COMMENT '创建人ID',
    `update_id`     bigint                                                                   DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统数据角色表【废弃, from 2025-v1.3.0】';

CREATE TABLE `sys_data_role_menu`
(
    `id`      bigint                                                       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` bigint DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）',
    `menu_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 73
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统数据角色-菜单表【废弃, from 2025-v1.3.0】';

CREATE TABLE `sys_data_role_relation`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`          bigint                                                       DEFAULT NULL COMMENT 'sys_data_role_id （数据角色表ID）',
    `relation_type_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联类型，data_scope_relation_type',
    `relation_id`      bigint                                                       DEFAULT NULL COMMENT '关联表id，联动relation_type_cd（部门ID或个人ID）',
    `menu_id`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 77
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统数据角色-关联表';

CREATE TABLE `sys_dept`
(
    `id`           bigint                                                          NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `name`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '部门名称',
    `pid`          bigint                                                          NOT NULL COMMENT '父级ID',
    `deep`         int                                                                      DEFAULT NULL COMMENT '层级',
    `sort`         int                                                                      DEFAULT NULL COMMENT '排序',
    `has_children` enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '是否有子级',
    `is_lock`      enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
    `del_flag`     enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
    `remark`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '备注',
    `create_id`    bigint                                                                   DEFAULT NULL COMMENT '创建人ID',
    `update_id`    bigint                                                                   DEFAULT NULL COMMENT '更新人ID',
    `create_time`  datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 20
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='部门表';

CREATE TABLE `sys_dept_closure`
(
    `ancestor_id`   bigint NOT NULL COMMENT '祖先节点ID',
    `descendant_id` bigint NOT NULL COMMENT '后代节点ID',
    `depth`         int    NOT NULL COMMENT '祖先节点到后代节点的距离'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='部门祖籍关系表';

CREATE TABLE `sys_dept_leader`
(
    `id`        bigint NOT NULL AUTO_INCREMENT COMMENT '部门领导人ID',
    `dept_id`   int DEFAULT NULL,
    `leader_id` bigint NOT NULL COMMENT '领导人ID（sys_user_id）',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='部门领导人表';

CREATE TABLE `sys_dept_role`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '部门-角色关联ID',
    `dept_id` bigint DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
    `role_id` bigint DEFAULT NULL COMMENT '角色ID(sys_role_id)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统部门-角色关联表';

CREATE TABLE `sys_dict`
(
    `id`                  bigint                                                          NOT NULL COMMENT '字典ID(规则)',
    `sys_dict_type_id`    bigint                                                          NOT NULL COMMENT '关联sys_dict_type ID',
    `code_name`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '字典名称',
    `alias`               varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '字典（Key）别名，某些情况下如果不想使用id作为key',
    `sort`                int                                                             NOT NULL COMMENT '排序(正序)',
    `callback_show_style` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            DEFAULT NULL COMMENT '回显样式',
    `remark`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '备注',
    `is_lock`             enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
    `is_show`             enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'T' COMMENT '是否展示',
    `del_flag`            enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
    `create_time`         datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time`         datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    `delete_time`         datetime                                                                 DEFAULT NULL COMMENT '删除时间',
    `create_id`           bigint                                                                   DEFAULT NULL COMMENT '创建人ID',
    `update_id`           bigint                                                                   DEFAULT NULL COMMENT '更新人ID',
    `delete_id`           bigint                                                                   DEFAULT NULL COMMENT '删除人ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='字典表';

CREATE TABLE `sys_dict_type`
(
    `id`          bigint                                                                      NOT NULL AUTO_INCREMENT COMMENT '字典类型ID',
    `type_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                NOT NULL DEFAULT '' COMMENT '字典类型名(中文)',
    `type_code`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                NOT NULL COMMENT '字典类型码(英文)',
    `is_lock`     enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      DEFAULT 'F' COMMENT '是否锁定，锁定的属性无法在页面进行修改',
    `is_show`     enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      DEFAULT 'T' COMMENT '显示与否',
    `del_flag`    enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                      DEFAULT 'F' COMMENT '删除与否',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci                        DEFAULT '' COMMENT '描述',
    `create_time` datetime                                                                             DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                                             DEFAULT NULL COMMENT '更新时间',
    `delete_time` datetime                                                                             DEFAULT NULL COMMENT '删除时间',
    `create_id`   bigint                                                                               DEFAULT NULL COMMENT '创建人ID',
    `update_id`   bigint                                                                               DEFAULT NULL COMMENT '更新人ID',
    `delete_id`   bigint                                                                               DEFAULT NULL COMMENT '删除人ID',
    `type`        enum ('system','business') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'business' COMMENT '字典类型: system 系统, business 业务',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1010
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='字典类型';

CREATE TABLE `sys_export_info`
(
    `id`               int NOT NULL AUTO_INCREMENT,
    `file_name`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '导出的文件名称',
    `export_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '导出状态,关联字典表export_status',
    `create_id`        int                                                          DEFAULT NULL,
    `create_time`      datetime                                                     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='导出信息';

CREATE TABLE `sys_file`
(
    `id`           bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `filename`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件名',
    `dir_tag`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '目录标识',
    `size`         bigint                                                                 DEFAULT NULL COMMENT '文件大小',
    `url`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '文件域名',
    `create_time`  datetime                                                               DEFAULT NULL COMMENT '创建时间',
    `object_name`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对象名（唯一）',
    `context_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '文件类型',
    `e_tag`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT 'eTag标识',
    `create_id`    bigint                                                                 DEFAULT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 101
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='文件表';

CREATE TABLE `sys_login_log`
(
    `id`             bigint                                                          NOT NULL AUTO_INCREMENT COMMENT '登陆ID',
    `del_flag`       enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
    `user_name`      varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '用户名',
    `login_status`   varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '登陆状态',
    `login_time`     datetime                                                                 DEFAULT NULL COMMENT '登陆时间',
    `ip_address`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '登陆ip地址',
    `login_location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '登陆地点',
    `browser`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '浏览器类型',
    `os`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '操作系统',
    `msg`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT NULL COMMENT '提示消息',
    `remark`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 24
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='登陆日志表';

CREATE TABLE `sys_menu`
(
    `id`             varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '菜单表id',
    `pid`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '父级id',
    `path`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '路径',
    `name`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT '' COMMENT '路由名称',
    `title`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '标题',
    `icon`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT '' COMMENT 'icon图标',
    `component`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            DEFAULT '' COMMENT '组件路径',
    `redirect`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            DEFAULT '' COMMENT '外链地址',
    `sort`           int                                                             NOT NULL COMMENT '排序',
    `deep`           int                                                             NOT NULL COMMENT '层级',
    `menu_type_cd`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '菜单类型 （字典表menu_type）',
    `permissions`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT '' COMMENT '按钮权限',
    `is_hidden`      enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '是否隐藏',
    `has_children`   enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否有子级',
    `is_link`        enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '路由外链时填写的访问地址',
    `is_full`        enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '菜单是否全屏',
    `is_affix`       enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '菜单是否固定在标签页',
    `is_keep_alive`  enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '当前路由是否缓存',
    `create_time`    datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    `create_id`      bigint                                                                   DEFAULT NULL COMMENT '创建人ID',
    `update_id`      bigint                                                                   DEFAULT NULL COMMENT '更新人ID',
    `del_flag`       enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否删除',
    `delete_id`      bigint                                                                   DEFAULT NULL COMMENT '删除人ID',
    `delete_time`    datetime                                                                 DEFAULT NULL COMMENT '删除时间',
    `use_data_scope` enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT 'F' COMMENT '菜单是否开启数据权限',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统菜单表';
CREATE TABLE `sys_message`
(
    `id`              bigint                                                          NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `message_type_cd` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '消息类型',
    `sender_id`       bigint                                                          NOT NULL COMMENT '发送人ID',
    `title`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '消息标题',
    `content`         text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           NOT NULL COMMENT '消息内容',
    `del_flag`        enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
    `create_time`     datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    `create_id`       bigint                                                                   DEFAULT NULL COMMENT '创建人 ID',
    `update_id`       bigint                                                                   DEFAULT NULL COMMENT '更新人 ID',
    `menu_router`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci            DEFAULT NULL COMMENT '菜单路由地址，冗余',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='消息管理';

CREATE TABLE `sys_message_user`
(
    `id`          bigint                                                          NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `message_id`  bigint                                                          NOT NULL COMMENT '消息ID',
    `receiver_id` bigint                                                          NOT NULL COMMENT '接收人ID',
    `is_read`     enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否已读',
    `read_time`   datetime                                                                 DEFAULT NULL COMMENT '阅读时间',
    `del_flag`    enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除标识',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='消息接收用户表';

CREATE TABLE `sys_role`
(
    `id`          bigint                                                          NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '角色名称',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '' COMMENT '简介',
    `del_flag`    enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '删除与否',
    `create_time` datetime                                                                 DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime                                                                 DEFAULT NULL COMMENT '更新时间',
    `create_id`   bigint                                                                   DEFAULT NULL COMMENT '创建人ID',
    `update_id`   bigint                                                                   DEFAULT NULL COMMENT '更新人ID',
    `is_lock`     enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'F' COMMENT '是否锁定',
    `permissions` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci             DEFAULT '' COMMENT '标识，唯一',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 8
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统角色表';

CREATE TABLE `sys_role_menu`
(
    `id`              bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '系统角色-菜单ID',
    `menu_id`         varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'sys_menu_id （菜单表）',
    `role_id`         bigint                                                                 DEFAULT NULL COMMENT 'sys_role_id （角色表）',
    `permission_type` enum ('menu','scope') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'menu' COMMENT '权限类型（功能权限；数据权限）',
    `data_scope_cd`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '数据权限范围，data_scope字典',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 139
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统角色-菜单表';

CREATE TABLE `sys_temp_file`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `sys_file_id` bigint                                                          DEFAULT NULL COMMENT '文件ID',
    `temp_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '模版名',
    `remark`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '备注',
    `del_flag`    enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '逻辑删除',
    `create_id`   bigint                                                          DEFAULT NULL COMMENT '创建人',
    `create_time` datetime                                                        DEFAULT NULL COMMENT '创建时间',
    `update_id`   bigint                                                          DEFAULT NULL COMMENT '更新人',
    `update_time` datetime                                                        DEFAULT NULL COMMENT '更新时间',
    `url`         json                                                            DEFAULT NULL COMMENT '地址(JSON数组对象)',
    `alias`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    DEFAULT '' COMMENT '标识，唯一',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='模版文件表';

CREATE TABLE `sys_temp_file_history`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `sys_temp_file_id` bigint                                                        DEFAULT NULL COMMENT '模版文件ID',
    `sys_file_id`      bigint                                                        DEFAULT NULL COMMENT '文件ID',
    `temp_name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模版名',
    `url`              json                                                          DEFAULT NULL COMMENT '地址(JSON数组对象)',
    `remark`           varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
    `create_id`        bigint                                                        DEFAULT NULL COMMENT '创建人',
    `create_time`      datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_id`        bigint                                                        DEFAULT NULL COMMENT '更新人',
    `update_time`      datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 4
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='模版文件历史表';

CREATE TABLE `sys_user`
(
    `id`                bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '用户名',
    `pwd`               varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
    `phone`             varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    DEFAULT NULL COMMENT '手机号',
    `nickname`          varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    DEFAULT NULL COMMENT '昵称',
    `sex`               tinyint(1)                                                      DEFAULT NULL COMMENT '性别(0 未知 1 男 2 女)',
    `birthday`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '生日',
    `logo`              varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '头像地址',
    `age`               int                                                             DEFAULT NULL COMMENT '年龄，--废弃，以生日为主',
    `id_card`           varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    DEFAULT NULL COMMENT '身份证',
    `email`             varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '邮箱地址',
    `account_status_cd` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    DEFAULT NULL COMMENT '账户状态 (如 冻结；禁言；正常。 关联字典表account_status)',
    `user_tag_cd`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '标签（自定义关联到字典表）',
    `last_login_time`   datetime                                                        DEFAULT NULL COMMENT '最近一次登录时间',
    `create_time`       datetime                                                        DEFAULT NULL COMMENT '创建时间',
    `update_time`       datetime                                                        DEFAULT NULL COMMENT '更新时间',
    `del_flag`          enum ('T','F') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'F' COMMENT '是否删除',
    `create_id`         bigint                                                          DEFAULT NULL COMMENT '创建人ID',
    `update_id`         bigint                                                          DEFAULT NULL COMMENT '更新人ID',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `username_index` (`username`) USING BTREE,
    KEY `create_time_index` (`create_time` DESC) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统用户表';

CREATE TABLE `sys_user_data_role`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '用户-数据角色关联ID',
    `role_id` bigint DEFAULT NULL COMMENT '数据角色ID (sys_data_role_id)',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统用户-数据角色关联表';

CREATE TABLE `sys_user_dept`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '用户-部门关系ID',
    `dept_id` bigint DEFAULT NULL COMMENT '部门ID (sys_dept_id)',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='用户-部门关系表';

CREATE TABLE `sys_user_role`
(
    `id`      bigint NOT NULL AUTO_INCREMENT COMMENT '用户-角色关联ID',
    `role_id` bigint DEFAULT NULL COMMENT '角色ID (sys_role_id)',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID(sys_user_id)',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = DYNAMIC COMMENT ='系统用户-角色关联表'

