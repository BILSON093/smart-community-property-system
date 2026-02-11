-- 智慧社区物业管理系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `wuye` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `wuye`;

-- 1. 系统用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` VARCHAR(50) NOT NULL COMMENT '账号/手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系方式',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` TINYINT NOT NULL DEFAULT 0 COMMENT '0=管理员, 1=业主, 2=维修员',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 2. 维修员扩展表
DROP TABLE IF EXISTS `sys_repair_worker`;
CREATE TABLE `sys_repair_worker` (
  `user_id` BIGINT NOT NULL COMMENT '关联sys_user',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用',
  `skill` VARCHAR(255) DEFAULT NULL COMMENT '维修技能（如：水电、泥瓦）',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_repair_worker_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修员扩展表';

-- 3. 业主扩展表
DROP TABLE IF EXISTS `sys_owner`;
CREATE TABLE `sys_owner` (
  `user_id` BIGINT NOT NULL COMMENT '关联sys_user',
  `building` VARCHAR(20) DEFAULT NULL COMMENT '楼栋号',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单元号',
  `room` VARCHAR(20) DEFAULT NULL COMMENT '房号',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_owner_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业主扩展表';

-- 4. 轮播图管理
DROP TABLE IF EXISTS `bus_carousel`;
CREATE TABLE `bus_carousel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `image_url` VARCHAR(255) NOT NULL COMMENT '图片链接',
  `target_url` VARCHAR(255) DEFAULT NULL COMMENT '跳转链接',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `is_show` TINYINT NOT NULL DEFAULT 1 COMMENT '是否展示',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图管理';

-- 5. 通知公告
DROP TABLE IF EXISTS `bus_notice`;
CREATE TABLE `bus_notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '富文本内容',
  `type` TINYINT NOT NULL DEFAULT 0 COMMENT '0=普通, 1=紧急',
  `publish_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告';

-- 6. 社区活动
DROP TABLE IF EXISTS `bus_activity`;
CREATE TABLE `bus_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `description` TEXT DEFAULT NULL COMMENT '活动描述',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '活动地点',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区活动';

-- 7. 缴费信息
DROP TABLE IF EXISTS `bus_fee`;
CREATE TABLE `bus_fee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `type` VARCHAR(50) NOT NULL COMMENT '费用类型（水/电/物业）',
  `month` VARCHAR(20) NOT NULL COMMENT '月份',
  `amount` DECIMAL(10, 2) NOT NULL COMMENT '金额',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=未缴, 1=已缴',
  `pay_time` DATETIME DEFAULT NULL COMMENT '缴费时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_month` (`month`),
  CONSTRAINT `fk_fee_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费信息';

-- 8. 报修工单
DROP TABLE IF EXISTS `bus_repair`;
CREATE TABLE `bus_repair` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` BIGINT NOT NULL COMMENT '业主ID',
  `content` TEXT NOT NULL COMMENT '故障描述',
  `images` JSON DEFAULT NULL COMMENT '图片数组（多张图片URL）',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0=待审核, 1=已派单, 2=维修中, 3=已完成',
  `worker_id` BIGINT DEFAULT NULL COMMENT '维修员ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_repair_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_repair_worker` FOREIGN KEY (`worker_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修工单';

-- 9. 服务评价
DROP TABLE IF EXISTS `bus_evaluation`;
CREATE TABLE `bus_evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `repair_id` BIGINT NOT NULL COMMENT '关联工单ID',
  `score` TINYINT NOT NULL COMMENT '评分(1-5)',
  `comment` TEXT DEFAULT NULL COMMENT '评价内容',
  `images` JSON DEFAULT NULL COMMENT '评价图片',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_repair_id` (`repair_id`),
  CONSTRAINT `fk_evaluation_repair` FOREIGN KEY (`repair_id`) REFERENCES `bus_repair` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价';

-- 10. 论坛帖子
DROP TABLE IF EXISTS `bus_forum`;
CREATE TABLE `bus_forum` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `images` JSON DEFAULT NULL COMMENT '图片数组',
  `is_public` TINYINT NOT NULL DEFAULT 1 COMMENT '0=私密, 1=公开',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_public` (`is_public`),
  CONSTRAINT `fk_forum_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子';

-- 11. 聊天记录
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` BIGINT NOT NULL COMMENT '会话ID（用户ID）',
  `sender` TINYINT NOT NULL COMMENT '0=AI/System, 1=User, 2=Admin',
  `content` TEXT NOT NULL COMMENT '内容',
  `msg_type` VARCHAR(20) NOT NULL DEFAULT 'text' COMMENT '消息类型（text/image/link）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天记录';

-- 插入测试数据：默认管理员账号
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `role`) VALUES
(1, 'admin', '$2a$10$8E4R.XGTJaTJO.Tqi2QzoepaB99JnWnvAAV6LEm3a8Aw9rBPYHw/.', '系统管理员', '13800138000', 0);
-- 默认密码：12345678 (BCrypt加密)

-- 插入测试数据：轮播图
INSERT INTO `bus_carousel` (`image_url`, `target_url`, `sort_order`, `is_show`) VALUES
('http://localhost:8080/upload/banner1.jpg', '/activity/detail/1', 1, 1),
('http://localhost:8080/upload/banner2.jpg', '/activity/detail/2', 2, 1),
('http://localhost:8080/upload/banner3.jpg', '/news/detail/1', 3, 1);

-- 插入测试数据：通知公告
INSERT INTO `bus_notice` (`title`, `content`, `type`) VALUES
('关于小区水费缴费的通知', '<p>尊敬的业主：</p><p>本月水费将于25日开始缴纳，请各位业主及时缴纳。</p>', 0),
('紧急通知：停水维护', '<p>各位业主：</p><p>因管道维护，明日9:00-12:00将停水，请提前做好储水准备。</p>', 1);

-- 插入测试数据：社区活动
INSERT INTO `bus_activity` (`title`, `cover_image`, `description`, `start_time`, `end_time`, `location`) VALUES
('社区中秋晚会', 'http://localhost:8080/upload/banner1.jpg', '一年一度的中秋晚会，欢迎大家参加！', '2026-02-20 18:00:00', '2026-02-20 21:00:00', '小区中心广场');
