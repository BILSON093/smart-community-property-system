-- 论坛功能升级脚本
USE `wuye`;

-- 1. 创建论坛分类表
DROP TABLE IF EXISTS `bus_forum_category`;
CREATE TABLE `bus_forum_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛分类';

-- 2. 创建论坛评论表
DROP TABLE IF EXISTS `bus_forum_comment`;
CREATE TABLE `bus_forum_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comment_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛评论';

-- 3. 创建论坛点赞表
DROP TABLE IF EXISTS `bus_forum_like`;
CREATE TABLE `bus_forum_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_forum_user` (`forum_id`, `user_id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_like_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛点赞';

-- 4. 修改bus_forum表，添加分类字段，将is_public改为is_anonymous
ALTER TABLE `bus_forum` 
ADD COLUMN `category_id` BIGINT DEFAULT NULL COMMENT '分类ID' AFTER `user_id`,
ADD COLUMN `title` VARCHAR(200) DEFAULT NULL COMMENT '帖子标题' AFTER `category_id`,
MODIFY COLUMN `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '0=匿名, 1=实名',
ADD KEY `idx_category_id` (`category_id`),
ADD CONSTRAINT `fk_forum_category` FOREIGN KEY (`category_id`) REFERENCES `bus_forum_category` (`id`) ON DELETE SET NULL;

-- 5. 插入默认论坛分类
INSERT INTO `bus_forum_category` (`name`, `description`, `sort_order`) VALUES
('社区活动', '社区各类活动讨论', 1),
('生活服务', '生活服务相关信息', 2),
('邻里交流', '邻里之间的交流互动', 3),
('意见建议', '对社区管理的意见和建议', 4),
('其他', '其他话题讨论', 5);
