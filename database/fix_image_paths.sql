-- 修复图片路径：将activity1.jpg替换为已存在的banner1.jpg
UPDATE bus_activity SET cover_image = 'http://localhost:8080/upload/banner1.jpg' WHERE cover_image LIKE '%activity1.jpg%';
