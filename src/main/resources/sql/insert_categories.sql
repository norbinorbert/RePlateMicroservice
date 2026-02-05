INSERT INTO `category` (`id`, `name`, `parent_category_id`) VALUES
-- LEVEL 1 (Main Categories)
(1, 'Electronics', NULL),
(2, 'Vehicles', NULL),
(3, 'Real Estate', NULL),
(4, 'Home & Garden', NULL),
(5, 'Fashion', NULL),
(6, 'Jobs', NULL),
(7, 'Services', NULL),
(8, 'Sports & Outdoors', NULL),
(9, 'Hobbies & Entertainment', NULL),
(10, 'Kids & Baby', NULL),

-- LEVEL 2
(11, 'Phones & Accessories', 1),
(12, 'Computers', 1),
(13, 'TV & Audio', 1),

(14, 'Cars', 2),
(15, 'Motorcycles', 2),
(16, 'Auto Parts', 2),

(17, 'Apartments', 3),
(18, 'Houses', 3),
(19, 'Commercial', 3),

(20, 'Furniture', 4),
(21, 'Appliances', 4),
(22, 'Garden', 4),

(23, 'Men Clothing', 5),
(24, 'Women Clothing', 5),
(25, 'Shoes', 5),

(26, 'IT Jobs', 6),
(27, 'Construction Jobs', 6),
(28, 'Office Jobs', 6),

(29, 'Home Services', 7),
(30, 'Car Services', 7),
(31, 'Business Services', 7),

(32, 'Fitness', 8),
(33, 'Cycling', 8),
(34, 'Camping', 8),

(35, 'Music', 9),
(36, 'Books', 9),
(37, 'Games', 9),

(38, 'Baby Gear', 10),
(39, 'Toys', 10),
(40, 'Kids Clothing', 10),

-- LEVEL 3
(41, 'Smartphones', 11),
(42, 'Phone Accessories', 11),

(43, 'Laptops', 12),
(44, 'Desktop PCs', 12),

(45, 'Televisions', 13),
(46, 'Speakers', 13),

(47, 'Sedan', 14),
(48, 'SUV', 14),

(49, 'Kitchen Furniture', 20),
(50, 'Bedroom Furniture', 20),

(51, 'Refrigerators', 21),
(52, 'Washing Machines', 21),

(53, 'Dresses', 24),
(54, 'Jackets', 23),

(55, 'Software Development', 26),
(56, 'Network & SysAdmin', 26),

(57, 'Cleaning', 29),
(58, 'Plumbing', 29),

(59, 'Gym Equipment', 32),
(60, 'Tents', 34),

(61, 'Guitars', 35),
(62, 'Board Games', 37),

-- LEVEL 4
(63, 'Android Phones', 41),
(64, 'iPhones', 41),

(65, 'Gaming Laptops', 43),
(66, 'Ultrabooks', 43),

(67, 'LED TVs', 45),
(68, 'Smart TVs', 45),

(69, 'Electric Guitars', 61),
(70, 'Acoustic Guitars', 61),

-- LEVEL 1
(71, 'Food & Drink', NULL),

-- LEVEL 2
(72, 'Groceries', 71),
(73, 'Beverages', 71),
(74, 'Restaurant & Catering', 71),

-- LEVEL 3
(75, 'Fruits & Vegetables', 72),
(76, 'Meat & Seafood', 72),
(77, 'Snacks', 72),
(78, 'Non-Alcoholic Drinks', 73),
(79, 'Alcoholic Drinks', 73),
(80, 'Catering Services', 74),
(81, 'Baked Goods', 72),

-- LEVEL 4
(82, 'Organic Produce', 75),
(83, 'Citrus Fruits', 75),
(84, 'Beef', 76),
(85, 'Fish', 76),
(86, 'Chips', 77),
(87, 'Chocolate & Candy', 77),
(88, 'Juices', 78),
(89, 'Soft Drinks', 78),
(90, 'Beer', 79),
(91, 'Wine', 79),
(92, 'Wedding Catering', 80),
(93, 'Corporate Catering', 80),
(94, 'Bread', 81),
(95, 'Cakes', 81);