INSERT INTO `category` (`id`, `name`, `parent_category_id`)
VALUES (1, 'Electronics', NULL),
       (2, 'Computers', 1),
       (3, 'Laptops', 2),
       (4, 'Desktops', 2),
       (5, 'Smartphones', 1),
       (6, 'Cameras', 1),
       (7, 'Home Appliances', NULL),
       (8, 'Refrigerators', 7),
       (9, 'Washing Machines', 7),
       (10, 'Furniture', NULL),
       (11, 'Living Room', 10),
       (12, 'Bedroom', 10);