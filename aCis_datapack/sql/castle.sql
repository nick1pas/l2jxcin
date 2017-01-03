CREATE TABLE IF NOT EXISTS `castle` (
  `id` INT NOT NULL default 0,
  `name` varchar(25) NOT NULL,
  `taxPercent` INT NOT NULL default 15,
  `treasury` BIGINT NOT NULL default 0,
  `siegeDate` DECIMAL(20,0) NOT NULL default 0,
  `regTimeOver` enum('true','false') DEFAULT 'true' NOT NULL,
  PRIMARY KEY (`name`),
  KEY `id` (`id`)
);

INSERT IGNORE INTO `castle` VALUES
(1,'Gludio',0,0,0,'true'),
(2,'Dion',0,0,0,'true'),
(3,'Giran',0,0,0,'true'),
(4,'Oren',0,0,0,'true'),
(5,'Aden',0,0,0,'true'),
(6,'Innadril',0,0,0,'true'),
(7,'Goddard',0,0,0,'true'),
(8,'Rune',0,0,0,'true'),
(9,'Schuttgart',0,0,0,'true');