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



