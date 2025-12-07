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
    update_id   BIGINT       DEFAULT 0
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

-- 创建索引
CREATE INDEX del_flag_index ON z_user USING btree (del_flag);
CREATE INDEX username_index ON z_user USING btree (username);


-- 插入10条测试数据
INSERT INTO z_user (id, username, pwd, phone, nickname, sex, birthday, logo, id_card, email, update_time, del_flag,
                    create_id, update_id)
VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', '管理员', '男', '1990-01-01', '',
        '110101199001011234', 'admin@example.com', NOW(), FALSE, 0, 0),
       (2, 'testuser1', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', '测试用户1', '女', '1992-05-15', '',
        '110101199205151234', 'test1@example.com', NOW(), FALSE, 1, 1),
       (3, 'testuser2', 'e10adc3949ba59abbe56e057f20f883e', '13800138003', '测试用户2', '男', '1988-12-20', '',
        '110101198812201234', 'test2@example.com', NOW(), FALSE, 1, 1),
       (4, 'testuser3', 'e10adc3949ba59abbe56e057f20f883e', '13800138004', '测试用户3', '未知', '1995-08-10', '',
        '110101199508101234', 'test3@example.com', NOW(), FALSE, 1, 1),
       (5, 'testuser4', 'e10adc3949ba59abbe56e057f20f883e', '13800138005', '测试用户4', '女', '1993-03-25', '',
        '110101199303251234', 'test4@example.com', NOW(), FALSE, 1, 1),
       (6, 'testuser5', 'e10adc3949ba59abbe56e057f20f883e', '13800138006', '测试用户5', '男', '1991-11-30', '',
        '110101199111301234', 'test5@example.com', NOW(), FALSE, 1, 1),
       (7, 'testuser6', 'e10adc3949ba59abbe56e057f20f883e', '13800138007', '测试用户6', '女', '1994-07-14', '',
        '110101199407141234', 'test6@example.com', NOW(), FALSE, 1, 1),
       (8, 'testuser7', 'e10adc3949ba59abbe56e057f20f883e', '13800138008', '测试用户7', '未知', '1989-09-05', '',
        '110101198909051234', 'test7@example.com', NOW(), FALSE, 1, 1),
       (9, 'testuser8', 'e10adc3949ba59abbe56e057f20f883e', '13800138009', '测试用户8', '男', '1996-02-18', '',
        '110101199602181234', 'test8@example.com', NOW(), FALSE, 1, 1),
       (10, 'testuser9', 'e10adc3949ba59abbe56e057f20f883e', '13800138010', '测试用户9', '女', '1997-06-22', '',
        '110101199706221234', 'test9@example.com', NOW(), TRUE, 1, 1);
