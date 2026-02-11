-- Add missing fields to bus_forum_comment table
USE `wuye`;

ALTER TABLE `bus_forum_comment` 
ADD COLUMN `images` VARCHAR(500) DEFAULT NULL COMMENT '评论图片，JSON格式' AFTER `content`,
ADD COLUMN `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '0=匿名, 1=实名' AFTER `images`;