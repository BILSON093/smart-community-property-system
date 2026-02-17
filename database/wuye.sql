/*
 Navicat Premium Data Transfer

 Source Server         : BILSON
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : wuye

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 18/02/2026 00:50:31
*/

-- 创建wuye数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS wuye DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- 使用wuye数据库
USE wuye;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_config`;
CREATE TABLE `ai_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `provider` varchar(50) DEFAULT NULL COMMENT '服务商: local, xiaomimimo, modelscope, dashscope, custom',
  `api_url` varchar(255) DEFAULT NULL COMMENT 'API URL',
  `api_key` varchar(255) DEFAULT NULL COMMENT 'API Key',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `supports_multimodal` tinyint DEFAULT '0' COMMENT '是否支持多模态: 0=不支持, 1=支持',
  `enabled` tinyint DEFAULT '0' COMMENT '是否启用: 0=禁用, 1=启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI配置表';

-- ----------------------------
-- Records of ai_config
-- ----------------------------
BEGIN;
INSERT INTO `ai_config` (`id`, `provider`, `api_url`, `api_key`, `model_name`, `supports_multimodal`, `enabled`, `create_time`, `update_time`) VALUES (2, 'local', 'http://localhost:1234/v1/chat/completions', NULL, 'local-model', 1, 0, '2026-02-11 20:02:37', '2026-02-11 20:24:43');
INSERT INTO `ai_config` (`id`, `provider`, `api_url`, `api_key`, `model_name`, `supports_multimodal`, `enabled`, `create_time`, `update_time`) VALUES (3, 'modelscope', 'https://api-inference.modelscope.cn/v1/chat/completions', 'ms-6ebb9a10-3f79-4603-a8a5-86346c1e2cec', 'ZhipuAI/GLM-4.7', 0, 0, '2026-02-11 03:58:30', '2026-02-11 20:24:43');
INSERT INTO `ai_config` (`id`, `provider`, `api_url`, `api_key`, `model_name`, `supports_multimodal`, `enabled`, `create_time`, `update_time`) VALUES (4, 'modelscope', 'https://api-inference.modelscope.cn/v1/chat/completions', 'ms-6ebb9a10-3f79-4603-a8a5-86346c1e2cec', 'Qwen/Qwen3-VL-8B-Instruct', 1, 1, '2026-02-11 20:04:40', '2026-02-11 20:24:43');
COMMIT;

-- ----------------------------
-- Table structure for bus_activity
-- ----------------------------
DROP TABLE IF EXISTS `bus_activity`;
CREATE TABLE `bus_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '活动描述',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '活动地点',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `images` text COLLATE utf8mb4_unicode_ci COMMENT '活动图片JSON',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区活动';

-- ----------------------------
-- Records of bus_activity
-- ----------------------------
BEGIN;
INSERT INTO `bus_activity` (`id`, `title`, `cover_image`, `description`, `start_time`, `end_time`, `location`, `create_time`, `images`) VALUES (2, '你好', '/upload/686700cc-8cb0-47a7-be5f-f841ce58af1a.png', '好好', '2026-02-03 00:00:00', '2026-02-09 00:00:00', '门口', '2026-01-22 23:14:01', NULL);
INSERT INTO `bus_activity` (`id`, `title`, `cover_image`, `description`, `start_time`, `end_time`, `location`, `create_time`, `images`) VALUES (3, '不就好', '/upload/53d4bace-f38b-4cab-97d8-44f324dca4be.jpg', '不晕', '2026-01-21 00:00:00', '2026-01-29 00:00:00', '牛军', '2026-01-27 12:01:03', '[\"/upload/c46fd60f-a917-4312-9669-d1902ef2b432.jpg\",\"/upload/ae424f9f-e364-4012-a5e7-7c8f7765a275.jpg\",\"/upload/dbacd5c2-fa80-4143-93ba-e6f96218e98a.jpg\",\"/upload/bf658592-f14b-46f2-8350-2f128d95f5cf.jpg\",\"/upload/ba04d0ab-67dc-4a63-90fb-0cfd954cc8db.jpg\",\"/upload/a94dae14-7457-4e1b-aa50-d0945a64c90d.jpg\",\"/upload/d8414a78-8801-4e5c-830e-03a2a8c30a82.jpg\",\"/upload/2eb497ce-0fe7-4fac-95c6-3b2394946eeb.jpg\",\"/upload/86efe5f0-cef9-4590-ae29-dbb269756d94.jpg\",\"/upload/0cfd6dda-7b7b-4691-841e-774ea6073b0e.jpg\"]');
INSERT INTO `bus_activity` (`id`, `title`, `cover_image`, `description`, `start_time`, `end_time`, `location`, `create_time`, `images`) VALUES (4, 'V 雕 ASV 收到 vs', '/upload/6298a075-840b-41a3-948a-ecb93189adf2.jpg', '啊方法撒发啊', '2026-01-07 00:00:00', '2026-01-23 00:00:00', '啊发发啊', '2026-01-27 13:35:13', '[\"/upload/fe05abf1-dc41-406f-859b-3dea86c82e45.jpg\",\"/upload/288daee5-231f-4a90-85d8-44a2074ece65.jpg\",\"/upload/92497118-19d1-4197-97ab-63ec6d64446c.jpg\",\"/upload/ad616eac-96ef-48f5-b051-9bb06ea54c67.jpg\",\"/upload/3a2ecb3f-c867-4f59-91b3-1bfa1a3af66a.jpg\",\"/upload/194ac50f-9426-40b9-a6ff-d2b6c40a2f87.jpg\",\"/upload/5ea61a95-92fe-4a33-9fd2-1e6da6ec217d.jpg\",\"/upload/6f7e3590-a653-4978-9e7f-2d057668b07b.jpg\",\"/upload/2362a53f-fa66-46c0-80aa-1b237763bea8.jpg\",\"/upload/b976dfcc-c2ad-4cb3-891e-d9dd2105290b.png\"]');
INSERT INTO `bus_activity` (`id`, `title`, `cover_image`, `description`, `start_time`, `end_time`, `location`, `create_time`, `images`) VALUES (5, '来个二创', '/upload/09dd2854-c42d-4875-a7cf-b97e1899f941.jpg', '二游活动', '2026-02-10 00:00:00', '2026-02-18 00:00:00', 'a 栋楼下', '2026-02-17 21:19:52', '[\"/upload/386e1604-410c-499a-afe1-4357c79a3629.jpg\",\"/upload/6f5f997e-a3f9-40c6-9651-cd574418d2cb.jpg\",\"/upload/a8ed8214-7e8c-4594-bda2-1ff4874b8a21.jpg\"]');
COMMIT;

-- ----------------------------
-- Table structure for bus_carousel
-- ----------------------------
DROP TABLE IF EXISTS `bus_carousel`;
CREATE TABLE `bus_carousel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片链接',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `is_show` tinyint NOT NULL DEFAULT '1' COMMENT '是否展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图管理';

-- ----------------------------
-- Records of bus_carousel
-- ----------------------------
BEGIN;
INSERT INTO `bus_carousel` (`id`, `image_url`, `sort_order`, `is_show`, `create_time`) VALUES (1, '/upload/4119faf0-43d5-43f6-95e1-ed7079a59050.jpg', 1, 1, '2026-01-22 00:56:38');
INSERT INTO `bus_carousel` (`id`, `image_url`, `sort_order`, `is_show`, `create_time`) VALUES (2, '/upload/b7b37c1b-eca6-4060-a17d-7a3f431e855a.jpg', 2, 1, '2026-01-22 00:56:38');
INSERT INTO `bus_carousel` (`id`, `image_url`, `sort_order`, `is_show`, `create_time`) VALUES (3, '/upload/ca02f8cb-d5bc-4e23-a3bc-d535979dccc8.jpg', 3, 1, '2026-01-22 00:56:38');
INSERT INTO `bus_carousel` (`id`, `image_url`, `sort_order`, `is_show`, `create_time`) VALUES (4, '/upload/f754cb55-1ec7-42c8-a55b-138115020426.png', 0, 1, '2026-01-27 10:51:35');
INSERT INTO `bus_carousel` (`id`, `image_url`, `sort_order`, `is_show`, `create_time`) VALUES (5, '/upload/de4b8b8f-121d-47f4-9caf-53eefad243ee.jpg', 4, 1, '2026-02-17 21:27:26');
COMMIT;

-- ----------------------------
-- Table structure for bus_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `bus_evaluation`;
CREATE TABLE `bus_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `repair_id` bigint NOT NULL COMMENT '关联工单ID',
  `score` tinyint NOT NULL COMMENT '评分(1-5)',
  `comment` text COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_repair_id` (`repair_id`),
  CONSTRAINT `fk_evaluation_repair` FOREIGN KEY (`repair_id`) REFERENCES `bus_repair` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务评价';

-- ----------------------------
-- Records of bus_evaluation
-- ----------------------------
BEGIN;
INSERT INTO `bus_evaluation` (`id`, `repair_id`, `score`, `comment`, `create_time`) VALUES (1, 7, 5, '你好\n', '2026-01-24 23:36:29');
INSERT INTO `bus_evaluation` (`id`, `repair_id`, `score`, `comment`, `create_time`) VALUES (2, 6, 5, 'haixing', '2026-01-27 10:39:36');
INSERT INTO `bus_evaluation` (`id`, `repair_id`, `score`, `comment`, `create_time`) VALUES (3, 5, 4, '你好', '2026-01-28 12:11:42');
INSERT INTO `bus_evaluation` (`id`, `repair_id`, `score`, `comment`, `create_time`) VALUES (4, 8, 5, '不错', '2026-01-28 12:42:21');
INSERT INTO `bus_evaluation` (`id`, `repair_id`, `score`, `comment`, `create_time`) VALUES (5, 9, 4, '这个对', '2026-02-17 21:09:45');
COMMIT;

-- ----------------------------
-- Table structure for bus_fee
-- ----------------------------
DROP TABLE IF EXISTS `bus_fee`;
CREATE TABLE `bus_fee` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用类型（水/电/物业）',
  `month` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '月份',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=未缴, 1=已缴',
  `pay_time` datetime DEFAULT NULL COMMENT '缴费时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `reminded` tinyint NOT NULL DEFAULT '0' COMMENT '是否已提醒（0未提醒，1已提醒）',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_month` (`month`),
  CONSTRAINT `fk_fee_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费信息';

-- ----------------------------
-- Records of bus_fee
-- ----------------------------
BEGIN;
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (1, 2, '物业费', '2026-01', 100.00, 1, '2026-01-22 11:28:34', '2026-01-22 11:28:22', NULL, 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (2, 2, '水费', '2026-01', 2000.00, 1, '2026-01-22 21:23:39', '2026-01-22 11:30:22', NULL, 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (3, 2, '物业费', '2026-01', 228.00, 1, '2026-01-28 13:30:12', '2026-01-24 01:12:26', '', 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (4, 8, '电费', '2026-01', 66.60, 0, NULL, '2026-01-25 01:17:21', '', 1);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (5, 19, '水费', '2026-01', 4188.47, 1, '2026-01-25 01:59:41', '2026-01-25 01:17:44', NULL, 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (6, 2, '其他', '2026-01', 30.00, 1, '2026-02-17 21:09:14', '2026-01-27 11:06:52', '无敌了你', 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (7, 2, '水费', '2026-02', 418.47, 0, NULL, '2026-02-17 21:16:34', 'aaaa', 1);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (8, 2, '燃气费', '2026-02', 244.20, 0, NULL, '2026-02-17 22:09:22', '', 0);
INSERT INTO `bus_fee` (`id`, `owner_id`, `type`, `month`, `amount`, `status`, `pay_time`, `create_time`, `remark`, `reminded`) VALUES (9, 2, '电费', '2026-02', 66.60, 0, NULL, '2026-02-17 22:09:35', NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for bus_fee_settings
-- ----------------------------
DROP TABLE IF EXISTS `bus_fee_settings`;
CREATE TABLE `bus_fee_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `property_fee` decimal(10,2) NOT NULL DEFAULT '2.50' COMMENT '物业费单价（元/㎡）',
  `water_fee` decimal(10,2) NOT NULL DEFAULT '3.80' COMMENT '水费单价（元/吨）',
  `electricity_fee` decimal(10,2) NOT NULL DEFAULT '0.60' COMMENT '电费单价（元/度）',
  `gas_fee` decimal(10,2) NOT NULL DEFAULT '2.20' COMMENT '燃气费单价（元/立方）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bus_fee_settings
-- ----------------------------
BEGIN;
INSERT INTO `bus_fee_settings` (`id`, `property_fee`, `water_fee`, `electricity_fee`, `gas_fee`) VALUES (1, 1.20, 3.77, 0.60, 2.20);
COMMIT;

-- ----------------------------
-- Table structure for bus_feedback
-- ----------------------------
DROP TABLE IF EXISTS `bus_feedback`;
CREATE TABLE `bus_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容',
  `user_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户姓名',
  `status` int DEFAULT '0' COMMENT '状态：0-待处理，1-已处理',
  `reply` text COLLATE utf8mb4_unicode_ci COMMENT '回复内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of bus_feedback
-- ----------------------------
BEGIN;
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (1, '你好', '2', '张三', 1, '好的', '2026-01-26 00:17:28', '2026-01-26 00:18:08');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (2, '哈哈哈哈哈', '2', '张三', 1, 'nnn\n', '2026-01-26 00:27:17', '2026-01-26 00:49:33');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (3, '太好啦\n', '2', '张三', 1, '77777', '2026-01-26 00:46:53', '2026-01-26 00:48:44');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (4, '你好 6666', '2', '张三', 1, '听哈', '2026-01-26 00:48:57', '2026-01-26 00:49:38');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (5, '你好', '2', '张三', 0, NULL, '2026-01-28 11:52:27', '2026-01-28 11:52:27');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (6, '你好哦好久\n', '2', '张三', 0, NULL, '2026-01-28 14:35:36', '2026-01-28 14:35:36');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (7, '反馈\n', '2', '张三', 0, NULL, '2026-02-03 04:59:19', '2026-02-03 04:59:19');
INSERT INTO `bus_feedback` (`id`, `content`, `user_id`, `user_name`, `status`, `reply`, `create_time`, `update_time`) VALUES (8, '芽一为真', '2', '张三', 1, '芽芽琪', '2026-02-17 21:11:11', '2026-02-17 21:29:24');
COMMIT;

-- ----------------------------
-- Table structure for bus_forum
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum`;
CREATE TABLE `bus_forum` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '帖子标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子内容',
  `images` json DEFAULT NULL COMMENT '图片数组',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '0=匿名, 1=实名',
  `is_pinned` tinyint NOT NULL DEFAULT '0' COMMENT '0=普通, 1=置顶',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_public` (`is_public`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_pinned` (`is_pinned`),
  CONSTRAINT `fk_forum_category` FOREIGN KEY (`category_id`) REFERENCES `bus_forum_category` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_forum_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛帖子';

-- ----------------------------
-- Records of bus_forum
-- ----------------------------
BEGIN;
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (11, 1, 2, 'nihao', 'nihao ', '[\"/upload/f11a08a5-3b7f-4431-a5f0-2665aa4acf72.jpg\"]', 1, 0, '2026-01-26 21:34:45');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (12, 1, 1, '你好', '哈哈哈哈', '[\"/upload/44bf23ce-4fa4-4c6f-9b7d-0a2d893c6b29.png\"]', 1, 0, '2026-01-26 21:45:29');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (37, 1, 1, 'Test Post with Correct Image', 'This is a test post with the correct image format', '[\"/upload/44bf23ce-4fa4-4c6f-9b7d-0a2d893c6b29.png\"]', 1, 0, '2026-01-27 02:16:46');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (43, 1, 3, '里秘鲁币本来', '故意离开', '[\"/upload/3f338371-81db-4732-85c2-62c4a7d23c10.png\"]', 1, 0, '2026-01-27 09:19:54');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (44, 2, 1, '会发货', '比律师', '[\"/upload/e5f44a50-1ed8-403b-b600-c4c65f95853e.jpg\"]', 1, 0, '2026-01-27 09:20:33');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (46, 2, 2, '我真日啦', '真的好吗', '[]', 1, 0, '2026-02-03 04:25:36');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (48, 2, 4, '哎还是方法', '发货方哈', '[\"/upload/6ae38b40-3cc3-4528-bf20-41ec40c8f3c2.png\"]', 1, 0, '2026-02-03 04:49:50');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (49, 2, 1, '111122222233333', '付后期', '[\"/upload/965551e1-1e5c-46ca-b220-9c1923461f3d.png\"]', 1, 0, '2026-02-03 05:36:55');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (50, 2, 1, '1我', '11 嗯嗯', '[\"/upload/54b8a44d-9b25-47c3-a803-b38ad18c9f99.png\"]', 0, 0, '2026-02-03 05:38:57');
INSERT INTO `bus_forum` (`id`, `user_id`, `category_id`, `title`, `content`, `images`, `is_public`, `is_pinned`, `create_time`) VALUES (51, 2, 2, '这好吗', '芽一', '[\"/upload/c9e4f619-1168-4862-83bb-b1b4146bfdf1.jpg\"]', 1, 0, '2026-02-17 21:09:03');
COMMIT;

-- ----------------------------
-- Table structure for bus_forum_category
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_category`;
CREATE TABLE `bus_forum_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛分类';

-- ----------------------------
-- Records of bus_forum_category
-- ----------------------------
BEGIN;
INSERT INTO `bus_forum_category` (`id`, `name`, `description`, `sort_order`, `create_time`) VALUES (1, '社区活动', '社区各类活动讨论', 1, '2026-01-26 21:13:08');
INSERT INTO `bus_forum_category` (`id`, `name`, `description`, `sort_order`, `create_time`) VALUES (2, '生活服务', '生活服务相关信息', 2, '2026-01-26 21:13:08');
INSERT INTO `bus_forum_category` (`id`, `name`, `description`, `sort_order`, `create_time`) VALUES (3, '邻里交流', '邻里之间的交流互动', 3, '2026-01-26 21:13:08');
INSERT INTO `bus_forum_category` (`id`, `name`, `description`, `sort_order`, `create_time`) VALUES (4, '意见建议', '对社区管理的意见和建议', 4, '2026-01-26 21:13:08');
INSERT INTO `bus_forum_category` (`id`, `name`, `description`, `sort_order`, `create_time`) VALUES (5, '其他', '其他话题讨论', 5, '2026-01-26 21:13:08');
COMMIT;

-- ----------------------------
-- Table structure for bus_forum_comment
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_comment`;
CREATE TABLE `bus_forum_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `images` json DEFAULT NULL COMMENT '评论图片列表，存储图片URL数组',
  `is_public` int DEFAULT '1' COMMENT '是否公开 0-匿名 1-实名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_comment_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛评论';

-- ----------------------------
-- Records of bus_forum_comment
-- ----------------------------
BEGIN;
INSERT INTO `bus_forum_comment` (`id`, `forum_id`, `user_id`, `content`, `images`, `is_public`, `create_time`) VALUES (6, 44, 2, '11111', '[]', 1, '2026-02-03 04:16:52');
INSERT INTO `bus_forum_comment` (`id`, `forum_id`, `user_id`, `content`, `images`, `is_public`, `create_time`) VALUES (8, 48, 2, '来点东西', '[\"/upload/e1592491-87bf-44f0-ad4d-5eb6e951acc9.png\"]', 1, '2026-02-03 04:58:42');
INSERT INTO `bus_forum_comment` (`id`, `forum_id`, `user_id`, `content`, `images`, `is_public`, `create_time`) VALUES (9, 48, 2, '2222', '[\"/upload/a821ddbf-a2c8-422b-8196-6a35738704ea.png\"]', 1, '2026-02-03 05:33:27');
INSERT INTO `bus_forum_comment` (`id`, `forum_id`, `user_id`, `content`, `images`, `is_public`, `create_time`) VALUES (10, 50, 2, '11111', '[\"/upload/e3cdee22-c81f-46d3-b033-ad82942adf46.png\"]', 0, '2026-02-03 05:39:22');
INSERT INTO `bus_forum_comment` (`id`, `forum_id`, `user_id`, `content`, `images`, `is_public`, `create_time`) VALUES (11, 50, 2, '这是对的', '[\"/upload/680f8ccf-2a70-4c26-ae8e-5de6191ac757.png\"]', 1, '2026-02-17 21:05:35');
COMMIT;

-- ----------------------------
-- Table structure for bus_forum_like
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_like`;
CREATE TABLE `bus_forum_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_forum_user` (`forum_id`,`user_id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `fk_like_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='论坛点赞';

-- ----------------------------
-- Records of bus_forum_like
-- ----------------------------
BEGIN;
INSERT INTO `bus_forum_like` (`id`, `forum_id`, `user_id`, `create_time`) VALUES (5, 12, 2, '2026-01-27 00:22:31');
INSERT INTO `bus_forum_like` (`id`, `forum_id`, `user_id`, `create_time`) VALUES (23, 44, 2, '2026-01-28 11:53:09');
INSERT INTO `bus_forum_like` (`id`, `forum_id`, `user_id`, `create_time`) VALUES (28, 50, 2, '2026-02-17 20:59:07');
INSERT INTO `bus_forum_like` (`id`, `forum_id`, `user_id`, `create_time`) VALUES (29, 48, 2, '2026-02-17 22:54:55');
COMMIT;

-- ----------------------------
-- Table structure for bus_notice
-- ----------------------------
DROP TABLE IF EXISTS `bus_notice`;
CREATE TABLE `bus_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '富文本内容',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '0=普通, 1=紧急',
  `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片',
  `attachment` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告';

-- ----------------------------
-- Records of bus_notice
-- ----------------------------
BEGIN;
INSERT INTO `bus_notice` (`id`, `title`, `content`, `type`, `publish_time`, `image`, `attachment`) VALUES (1, '关于小区水费缴费的通知', '尊敬的业主：本月水费将于25日开始缴纳，请各位业主及时缴纳。', 1, '2026-01-21 08:56:38', '', '');
INSERT INTO `bus_notice` (`id`, `title`, `content`, `type`, `publish_time`, `image`, `attachment`) VALUES (2, '紧急通知：停水维护', '各位业主：因管道维护，明日9:00-12:00将停水，请提前做好储水准备。', 1, '2026-01-22 00:56:38', NULL, NULL);
INSERT INTO `bus_notice` (`id`, `title`, `content`, `type`, `publish_time`, `image`, `attachment`) VALUES (14, '你好', '你好', 0, '2026-01-03 00:00:00', '/upload/b153e6a2-afdc-466a-ac7a-28eb7f1c00a2.png,/upload/f47601a9-48e1-485c-9648-b9df153f67f7.png', '/upload/b80a6611-bdb0-424b-97f2-14605cee4997.pdf');
INSERT INTO `bus_notice` (`id`, `title`, `content`, `type`, `publish_time`, `image`, `attachment`) VALUES (15, '哈哈哈哈', '哈哈哈哈', 0, '2026-01-05 00:00:00', '/upload/655cd090-24b0-4694-a66e-8a1be85ef908.png', '');
INSERT INTO `bus_notice` (`id`, `title`, `content`, `type`, `publish_time`, `image`, `attachment`) VALUES (16, '这个好吗', '安徽省', 0, '2026-02-16 16:00:00', '/upload/8b6872a5-8de7-41bb-a7dc-a209fc76a938.png', '/upload/c56b69a6-2b56-4d09-abc6-e6e3e8eafa6f.pdf');
COMMIT;

-- ----------------------------
-- Table structure for bus_repair
-- ----------------------------
DROP TABLE IF EXISTS `bus_repair`;
CREATE TABLE `bus_repair` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` bigint NOT NULL COMMENT '业主ID',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '故障描述',
  `images` json DEFAULT NULL COMMENT '图片数组（多张图片URL）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=待审核, 1=已派单, 2=维修中, 3=已完成',
  `worker_id` bigint DEFAULT NULL COMMENT '维修员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_owner_id` (`owner_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_repair_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_repair_worker` FOREIGN KEY (`worker_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修工单';

-- ----------------------------
-- Records of bus_repair
-- ----------------------------
BEGIN;
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (1, 2, '这个位置坏了', '[null]', 3, 4, '2026-01-22 11:38:55', '2026-01-22 16:44:30', NULL);
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (2, 2, '这个也坏了', '[null]', 3, 4, '2026-01-22 13:49:23', '2026-01-22 14:09:25', NULL);
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (4, 2, '来个人修一下', '[\"/upload/f3649a7a-bf23-476b-98c3-b131cb748b7b.jpg\"]', 3, 4, '2026-01-22 21:42:27', '2026-01-24 22:06:55', NULL);
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (5, 2, '这里不行了', '[\"/upload/924caa6f-af34-4029-8676-6c522ec4e88b.jpg\"]', 3, 4, '2026-01-22 21:51:09', '2026-01-24 23:42:24', '2026-01-28 11:56:41');
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (6, 2, '这里坏了', '[\"/upload/741b8e0f-79d3-4bd9-84ca-078fc7a5dd99.jpg\"]', 3, 4, '2026-01-22 22:28:13', '2026-01-23 12:01:26', NULL);
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (7, 2, '这里坏了\n', '[\"/upload/f463c69b-ee4e-4c02-8777-458dc5f04bc7.jpg\"]', 3, 4, '2026-01-23 23:40:27', '2026-01-24 23:26:44', '2026-01-24 23:28:36');
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (8, 2, '啦啦啦啦啦', '[\"/upload/9963a980-5ad0-4b0c-9202-5345677006e6.jpg\"]', 3, 4, '2026-01-28 12:32:56', '2026-01-28 12:34:24', '2026-01-28 12:41:54');
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (9, 2, '开始从农村农村', '[\"/upload/f76d4b3c-716d-4f34-a1c2-074ab26ee8c9.jpg\"]', 3, 4, '2026-01-28 12:39:45', '2026-01-28 12:41:40', '2026-02-03 23:40:39');
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (10, 2, '这个号码', '[\"/upload/217da7a3-212e-4793-a5e2-c1b9a9aabc07.png\"]', 3, 4, '2026-02-17 21:09:33', '2026-02-17 21:14:45', '2026-02-17 21:15:11');
INSERT INTO `bus_repair` (`id`, `owner_id`, `content`, `images`, `status`, `worker_id`, `create_time`, `update_time`, `complete_time`) VALUES (11, 2, '11111', '[]', 1, 4, '2026-02-18 00:05:38', '2026-02-18 00:05:49', NULL);
COMMIT;

-- ----------------------------
-- Table structure for chat_record
-- ----------------------------
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` bigint NOT NULL COMMENT '会话ID（用户ID）',
  `sender` tinyint NOT NULL COMMENT '0=AI/System, 1=User, 2=Admin',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `msg_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT '消息类型（text/image/link）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天记录';

-- ----------------------------
-- Records of chat_record
-- ----------------------------
BEGIN;
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (1, 2, 1, '你好', 'text', '2026-01-22 11:26:46');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (2, 1, 2, '你好', 'text', '2026-01-22 11:27:17');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (3, 2, 1, '通知', 'text', '2026-01-22 11:29:43');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (4, 2, 1, '你好', 'text', '2026-01-22 13:31:11');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (5, 2, 1, '你好', 'text', '2026-01-22 13:36:36');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (6, 2, 1, '你好我的问题是', 'text', '2026-01-22 13:53:49');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (7, 2, 1, '查一下欠费', 'text', '2026-01-22 16:39:55');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (8, 2, 1, '你好', 'text', '2026-01-22 21:07:39');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (9, 8, 1, '你好啊', 'text', '2026-01-23 09:13:55');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (11, 2, 2, '对', 'text', '2026-01-28 14:14:22');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (12, 2, 2, '对啦就是这样', 'text', '2026-02-03 04:06:22');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (13, 2, 2, '是哪', 'text', '2026-02-03 04:10:33');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (14, 2, 1, '111', 'text', '2026-02-03 19:09:06');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (15, 2, 1, '222', 'text', '2026-02-03 19:09:38');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (16, 2, 1, '缴费', 'text', '2026-02-03 19:10:31');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (17, 2, 1, '你好', 'text', '2026-02-03 21:58:21');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (18, 2, 1, '你好', 'text', '2026-02-03 21:58:26');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (19, 2, 1, '你是谁', 'text', '2026-02-03 21:58:45');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (20, 2, 1, '好好好', 'text', '2026-02-17 21:09:55');
INSERT INTO `chat_record` (`id`, `session_id`, `sender`, `content`, `msg_type`, `create_time`) VALUES (21, 2, 2, '这个对吗', 'text', '2026-02-17 21:10:26');
COMMIT;

-- ----------------------------
-- Table structure for sys_owner
-- ----------------------------
DROP TABLE IF EXISTS `sys_owner`;
CREATE TABLE `sys_owner` (
  `user_id` bigint NOT NULL COMMENT '关联sys_user',
  `building` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '楼栋号',
  `unit` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单元号',
  `room` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '房号',
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '身份证号',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_owner_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业主扩展表';

-- ----------------------------
-- Records of sys_owner
-- ----------------------------
BEGIN;
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (2, 'A 栋', '2 单元', '1001室', '440692200202283279');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (5, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (6, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (7, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (8, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (10, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (19, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (20, '', '', '', '');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (21, 'A 栋', 'A 单元', '1234', '440768200803032876');
INSERT INTO `sys_owner` (`user_id`, `building`, `unit`, `room`, `id_card`) VALUES (23, 'A 栋', '3 单元', '3003 室', '440852200002020202');
COMMIT;

-- ----------------------------
-- Table structure for sys_repair_worker
-- ----------------------------
DROP TABLE IF EXISTS `sys_repair_worker`;
CREATE TABLE `sys_repair_worker` (
  `user_id` bigint NOT NULL COMMENT '关联sys_user',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '0=待审核, 1=正常, 2=禁用',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_repair_worker_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修员扩展表';

-- ----------------------------
-- Records of sys_repair_worker
-- ----------------------------
BEGIN;
INSERT INTO `sys_repair_worker` (`user_id`, `status`) VALUES (3, 1);
INSERT INTO `sys_repair_worker` (`user_id`, `status`) VALUES (4, 1);
INSERT INTO `sys_repair_worker` (`user_id`, `status`) VALUES (24, 1);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号/手机号',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系方式',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '0=管理员, 1=业主, 2=维修员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (1, 'admin', '$2a$10$8E4R.XGTJaTJO.Tqi2QzoepaB99JnWnvAAV6LEm3a8Aw9rBPYHw/.', '系统管理员', '13800138000', '/upload/dedeef45-7031-4cd3-9ae8-a51f9bebcad6.jpg', 0, '2026-01-22 00:56:38');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (2, 'bilson666', '$2a$10$kshCV8KNQW5NPMBYhrppSeopIfn9sazQmgEeYrJnQ2pygNxcVu5P6', '张三', '13800138001', 'http://localhost:8080/api/upload/3ad15e6b-c347-4f5c-9c6b-8d06ce759d43.jpg', 1, '2026-01-22 01:00:18');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (3, '11111111111', '$2a$10$FF6jdLeIAIzg8OkIfbQ03.poRVSI3ipZBT5HJj1N9i1HlCABkJoMG', '两', '11111111111', '/upload/7967f637-8510-4ca9-a338-bf0b118efadf.jpg', 2, '2026-01-22 09:35:09');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (4, '123456', '$2a$10$6ut3v3dTsxtElDJBLAR2E.WcMT0xk0.sErOn5ZAeouQop4Cqp11Xa', 'bil', '11122222222', '/upload/9e16b529-8600-47dc-80c5-276197b0b383.png', 2, '2026-01-22 13:41:54');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (5, '13800138002', '$2a$10$IzKwic4ZoPzr0MTbBSTtyuKBnekeBVelw6/G.PvHYxxxIz.xj/eh.', 'Test Owner', '13800138002', NULL, 1, '2026-01-22 20:19:55');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (6, 'test123', '$2a$10$vQYJn8V07k8TRvrJW.Y5lursoL8YbauS2SEyizSV1adauXWKH8g.i', '测试用户', '13800138002', NULL, 1, '2026-01-23 09:08:36');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (7, 'test456', '$2a$10$KsHvN4CRn66WWT1q3tfBb.XY687TXAcSaAVZZKdCPy2VOlu//BTe6', '测试用户2', '13800138003', NULL, 1, '2026-01-23 09:09:29');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (8, '11111110000', '$2a$10$P/oQlLsjcAc/seNBgs.TT.ypeRac.Mi2tfwU9sUxbZb1CElW6zh9i', '姚22', NULL, '/upload/edd5be0f-fddf-4ec9-be08-7f9bea194ef4.jpg', 1, '2026-01-23 09:10:48');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (9, 'bilson', '$2a$10$PCm5ufjAB.vinPeT7FIOGub/dAIPP7.1gPYV1tu7GksDRCcnMKFPe', '姚qq', '13908485876', '/upload/977fccdd-2982-4414-befe-671d29ff8dc8.jpg', 0, '2026-01-23 11:00:27');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (10, 'BILSON093', '$2a$10$UOvlvaNVnLSylBst4TLQ2OWDiZpzIwZOj2YpA0U9GcKzSciDgprsu', '姚33', '13700099876', '/upload/2a8d9469-2e52-46d6-b228-149adbfb86c9.png', 1, '2026-01-23 11:36:56');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (19, 'elysia', '$2a$10$L8jlUXzKT5NalwyoGPaj6.yDmo7R9JLBYAxJOSFeteUFvt09wMbk2', '姚55', '13000009874', '/upload/520d68cb-c36a-468c-badb-0f8b1d9d704e.jpg', 1, '2026-01-23 15:45:26');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (20, 'testuser', '$2a$10$yeeNJqMWAdHl7Z7t67F59ev5AMsYjlzR4t0G/L2Rg2Z8mgWvTtRHC', 'Test User', '13800138003', NULL, 1, '2026-01-27 09:28:52');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (21, 'bilson1', '$2a$10$QAqStYYiuvzed.dQUL5AL.CcdkYhO4Wb0J4muHglwOaFCN5gkfB.6', '姚33', '13780987463', NULL, 1, '2026-01-28 01:12:45');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (22, 'BILSONOVO', '$2a$10$LLWYFwUdC392OO3ZW6xzguCPqT6zEyD6JGIUkD0lnIn48ftBPApMi', 'elysia', '13999999999', '/upload/f89034b1-79f4-4d35-aace-77cda4b9ed98.jpg', 0, '2026-02-17 20:40:42');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (23, 'philia093', '$2a$10$XcCZkJvBLyjoioklg.qOP.7VnvLhY0rjDXO4C1gTm0NJV9SP5bTT2', '两只老', '13999999988', '/upload/5c154fa7-7549-4f32-8144-88e01ea997eb.jpg', 1, '2026-02-17 21:13:07');
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `avatar`, `role`, `create_time`) VALUES (24, 'paduo', '$2a$10$j9/BPmOTYZsDH7f5CAXnqOf5i58atBZ9crc/hmZRG2FFVkEs7cyhW', '帕朵菲莉丝', '13489999999', '/upload/f41ba24f-1909-41eb-9d3a-0dd6a8317926.jpg', 2, '2026-02-17 21:14:25');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
