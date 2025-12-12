-- 创建触发器函数，用于自动更新 update_time
CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.update_time = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


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



