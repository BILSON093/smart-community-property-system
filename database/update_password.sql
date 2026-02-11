USE wuye;

-- 更新管理员密码为12345678
UPDATE sys_user SET password = '$2a$10$8E4R.XGTJaTJO.Tqi2QzoepaB99JnWnvAAV6LEm3a8Aw9rBPYHw/.' WHERE username = 'admin';

SELECT id, username, password, real_name FROM sys_user WHERE username = 'admin';
