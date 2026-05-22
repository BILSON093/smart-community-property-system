/*
 Navicat Premium Data Transfer

 Source Server         : elysia
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : wuye

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 19/05/2026 17:39:34
*/
CREATE DATABASE IF NOT EXISTS wuye CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

/* 自动切换到 wuye 数据库 */
USE wuye;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_config
-- ----------------------------
DROP TABLE IF EXISTS `ai_config`;
CREATE TABLE `ai_config`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务商: local, xiaomimimo, modelscope, dashscope, custom',
  `api_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API URL',
  `api_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'API Key',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模型名称',
  `supports_multimodal` tinyint(4) NULL DEFAULT 0 COMMENT '是否支持多模态: 0=不支持, 1=支持',
  `enabled` tinyint(4) NULL DEFAULT 0 COMMENT '是否启用: 0=禁用, 1=启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_activity
-- ----------------------------
DROP TABLE IF EXISTS `bus_activity`;
CREATE TABLE `bus_activity`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '封面图',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动描述',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '活动图片JSON',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '社区活动' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_carousel
-- ----------------------------
DROP TABLE IF EXISTS `bus_carousel`;
CREATE TABLE `bus_carousel`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片链接',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `is_show` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '轮播图管理' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `bus_evaluation`;
CREATE TABLE `bus_evaluation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `repair_id` bigint(20) NOT NULL COMMENT '关联工单ID',
  `score` tinyint(4) NOT NULL COMMENT '评分(1-5)',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '评价内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_repair_id`(`repair_id` ASC) USING BTREE,
  CONSTRAINT `fk_evaluation_repair` FOREIGN KEY (`repair_id`) REFERENCES `bus_repair` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '服务评价' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_fee
-- ----------------------------
DROP TABLE IF EXISTS `bus_fee`;
CREATE TABLE `bus_fee`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '费用类型（水/电/物业）',
  `month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '月份',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=未缴, 1=已缴',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '缴费时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `reminded` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否已提醒（0未提醒，1已提醒）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_month`(`month` ASC) USING BTREE,
  CONSTRAINT `fk_fee_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '缴费信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_payment_order
-- ----------------------------
DROP TABLE IF EXISTS `bus_payment_order`;
CREATE TABLE `bus_payment_order`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '支付订单号',
  `fee_id` bigint(20) NOT NULL COMMENT '账单ID',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=待支付, 1=支付成功, 2=已关闭',
  `channel` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'mock' COMMENT '支付渠道',
  `trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方流水号',
  `paid_time` datetime NULL DEFAULT NULL COMMENT '支付完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_trade_no`(`trade_no` ASC) USING BTREE,
  INDEX `idx_fee_id`(`fee_id` ASC) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_payment_fee` FOREIGN KEY (`fee_id`) REFERENCES `bus_fee` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_payment_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '支付订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_fee_settings
-- ----------------------------
DROP TABLE IF EXISTS `bus_fee_settings`;
CREATE TABLE `bus_fee_settings`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `property_fee` decimal(10, 2) NOT NULL DEFAULT 2.50 COMMENT '物业费单价（元/㎡）',
  `water_fee` decimal(10, 2) NOT NULL DEFAULT 3.80 COMMENT '水费单价（元/吨）',
  `electricity_fee` decimal(10, 2) NOT NULL DEFAULT 0.60 COMMENT '电费单价（元/度）',
  `gas_fee` decimal(10, 2) NOT NULL DEFAULT 2.20 COMMENT '燃气费单价（元/立方）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_feedback
-- ----------------------------
DROP TABLE IF EXISTS `bus_feedback`;
CREATE TABLE `bus_feedback`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '反馈内容',
  `user_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户姓名',
  `status` int(11) NULL DEFAULT 0 COMMENT '状态：0-待处理，1-已处理',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '回复内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_forum
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum`;
CREATE TABLE `bus_forum`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '分类ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '帖子内容',
  `images` json NULL COMMENT '图片数组',
  `is_public` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=匿名, 1=实名',
  `is_pinned` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=普通, 1=置顶',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '0=待审核, 1=通过, 2=拒绝',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_public`(`is_public` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_is_pinned`(`is_pinned` ASC) USING BTREE,
  CONSTRAINT `fk_forum_category` FOREIGN KEY (`category_id`) REFERENCES `bus_forum_category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_forum_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛帖子' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_forum_category
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_category`;
CREATE TABLE `bus_forum_category`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_forum_comment
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_comment`;
CREATE TABLE `bus_forum_comment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '评论用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `images` json NULL COMMENT '评论图片列表，存储图片URL数组',
  `is_public` int(11) NULL DEFAULT 1 COMMENT '是否公开 0-匿名 1-实名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_forum_id`(`forum_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛评论' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_forum_like
-- ----------------------------
DROP TABLE IF EXISTS `bus_forum_like`;
CREATE TABLE `bus_forum_like`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `forum_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_forum_user`(`forum_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `idx_forum_id`(`forum_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_like_forum` FOREIGN KEY (`forum_id`) REFERENCES `bus_forum` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '论坛点赞' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_notice
-- ----------------------------
DROP TABLE IF EXISTS `bus_notice`;
CREATE TABLE `bus_notice`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '富文本内容',
  `type` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=普通, 1=紧急',
  `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图片',
  `attachment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '通知公告' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_repair
-- ----------------------------
DROP TABLE IF EXISTS `bus_repair`;
CREATE TABLE `bus_repair`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `owner_id` bigint(20) NOT NULL COMMENT '业主ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '故障描述',
  `images` json NULL COMMENT '图片数组（多张图片URL）',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=待审核, 1=已派单, 2=维修中, 3=已完成',
  `worker_id` bigint(20) NULL DEFAULT NULL COMMENT '维修员ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '其他' COMMENT '报修分类',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_owner_id`(`owner_id` ASC) USING BTREE,
  INDEX `idx_worker_id`(`worker_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_repair_owner` FOREIGN KEY (`owner_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_repair_worker` FOREIGN KEY (`worker_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '报修工单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for chat_record
-- ----------------------------
DROP TABLE IF EXISTS `chat_record`;
CREATE TABLE `chat_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` bigint(20) NOT NULL COMMENT '会话ID（用户ID）',
  `sender` tinyint(4) NOT NULL COMMENT '0=AI/System, 1=User, 2=Admin',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '内容',
  `msg_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'text' COMMENT '消息类型（text/image/link）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_session_id`(`session_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '聊天记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_owner
-- ----------------------------
DROP TABLE IF EXISTS `sys_owner`;
CREATE TABLE `sys_owner`  (
  `user_id` bigint(20) NOT NULL COMMENT '关联sys_user',
  `building` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '楼栋号',
  `unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单元号',
  `room` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '房号',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '身份证号',
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `fk_owner_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业主扩展表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_repair_worker
-- ----------------------------
DROP TABLE IF EXISTS `sys_repair_worker`;
CREATE TABLE `sys_repair_worker`  (
  `user_id` bigint(20) NOT NULL COMMENT '关联sys_user',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=待审核, 1=正常, 2=禁用',
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `fk_repair_worker_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '维修员扩展表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '账号/手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  `role` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=管理员, 1=业主, 2=维修员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '通知内容',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知类型',
  `is_read` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0=未读, 1=已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_activity_signup
-- ----------------------------
DROP TABLE IF EXISTS `bus_activity_signup`;
CREATE TABLE `bus_activity_signup`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_user`(`activity_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_signup_activity` FOREIGN KEY (`activity_id`) REFERENCES `bus_activity` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bus_notice_read
-- ----------------------------
DROP TABLE IF EXISTS `bus_notice_read`;
CREATE TABLE `bus_notice_read`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `notice_id` bigint(20) NOT NULL COMMENT '公告ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `read_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notice_user`(`notice_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_read_notice` FOREIGN KEY (`notice_id`) REFERENCES `bus_notice` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告已读表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
