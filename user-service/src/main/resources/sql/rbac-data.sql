-- RBAC权限管理系统初始化数据
-- 参考Go项目的角色权限设计

-- 初始化基础角色
INSERT INTO `roles` (`name`, `description`) VALUES 
('USER', '普通用户,可以查看课程、选课、评论'),
('TEACHER', '教师,可以创建和管理自己的课程'),
('ADMIN', '管理员,拥有所有权限'),
('MODERATOR', '版主,可以管理评论和内容')
ON DUPLICATE KEY UPDATE 
`description` = VALUES(`description`),
`updated_time` = CURRENT_TIMESTAMP;

-- 初始化基础权限
INSERT INTO `permissions` (`name`, `action`, `resource`, `description`) VALUES 
-- 课程相关权限
('course_read', 'read', 'course', '查看课程信息'),
('course_create', 'create', 'course', '创建课程'),
('course_update', 'update', 'course', '更新课程'),
('course_delete', 'delete', 'course', '删除课程'),

-- 评论相关权限
('comment_read', 'read', 'comment', '查看评论'),
('comment_create', 'create', 'comment', '发表评论'),
('comment_delete', 'delete', 'comment', '删除评论'),

-- 用户相关权限
('user_read', 'read', 'user', '查看用户信息'),
('user_update', 'update', 'user', '更新用户信息'),
('user_delete', 'delete', 'user', '删除用户'),

-- 选课相关权限
('enrollment_create', 'create', 'enrollment', '选课'),
('enrollment_cancel', 'cancel', 'enrollment', '退课')
ON DUPLICATE KEY UPDATE 
`description` = VALUES(`description`),
`updated_time` = CURRENT_TIMESTAMP;

-- 为角色分配权限
-- USER角色权限：基础的查看和创建权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'USER' AND p.name IN (
    'course_read', 
    'comment_read', 
    'comment_create', 
    'user_read', 
    'enrollment_create', 
    'enrollment_cancel'
)
ON DUPLICATE KEY UPDATE `created_time` = `created_time`;

-- TEACHER角色权限：包括USER权限 + 课程管理权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'TEACHER' AND p.name IN (
    'course_read', 
    'course_create', 
    'course_update', 
    'course_delete',
    'comment_read', 
    'comment_create', 
    'user_read', 
    'user_update',
    'enrollment_create', 
    'enrollment_cancel'
)
ON DUPLICATE KEY UPDATE `created_time` = `created_time`;

-- MODERATOR角色权限：包括USER权限 + 内容管理权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'MODERATOR' AND p.name IN (
    'course_read', 
    'comment_read', 
    'comment_create', 
    'comment_delete',
    'user_read', 
    'enrollment_create', 
    'enrollment_cancel'
)
ON DUPLICATE KEY UPDATE `created_time` = `created_time`;

-- ADMIN角色权限：所有权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`)
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'ADMIN'
ON DUPLICATE KEY UPDATE `created_time` = `created_time`;

-- 创建示例用户的角色分配
-- 为ID为1的用户分配USER角色（如果存在）
INSERT INTO `user_roles` (`user_id`, `role_id`)
SELECT 1, r.id FROM `roles` r 
WHERE r.name = 'USER' 
AND EXISTS (SELECT 1 FROM `users` WHERE `id` = 1)
ON DUPLICATE KEY UPDATE `created_time` = `created_time`;

-- 查询语句验证数据
-- SELECT 'Roles:', count(*) as role_count FROM roles;
-- SELECT 'Permissions:', count(*) as permission_count FROM permissions;
-- SELECT 'Role-Permissions:', count(*) as rp_count FROM role_permissions;
-- SELECT 'User-Roles:', count(*) as ur_count FROM user_roles;
