-- 添加帖子置顶字段
USE `wuye`;

ALTER TABLE `bus_forum` 
ADD COLUMN `is_pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '0=普通, 1=置顶' AFTER `is_public`,
ADD INDEX `idx_is_pinned` (`is_pinned`);
