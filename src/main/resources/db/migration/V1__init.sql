CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `category` (
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user_category` (
  `user_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `elo` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`category_id`),
  KEY `FK_user_category_category_id_idx` (`category_id`),
  CONSTRAINT `FK_user_category_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  KEY `FK_user_category_user_id_idx` (`user_id`),
  CONSTRAINT `FK_user_category_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO category (id) VALUES
  (1);
