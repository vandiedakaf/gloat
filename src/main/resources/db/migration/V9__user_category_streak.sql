ALTER TABLE user_category
  ADD streak_type VARCHAR(2) DEFAULT '-' NOT NULL,
  ADD streak_count INT(11) DEFAULT 0 NULL;

CREATE TABLE `user_user_category` (
  `user_id`     INT(11) NOT NULL,
  `opponent_id` INT(11) NOT NULL,
  `category_id` INT(11) NOT NULL,
  `wins`        INT(11) DEFAULT 0,
  `losses`      INT(11) DEFAULT 0,
  `draws`       INT(11) DEFAULT 0,
  `wilson`      DOUBLE   DEFAULT 0,
  PRIMARY KEY (`user_id`, `category_id`, `opponent_id`),
  KEY `FK_user_user_category_user_id_idx` (`user_id`),
  CONSTRAINT `FK_user_user_category_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  KEY `FK_user_user_category_opponent_id_idx` (`opponent_id`),
  CONSTRAINT `FK_user_user_category_opponent_id` FOREIGN KEY (`opponent_id`) REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  KEY `FK_user_user_category_category_id_idx` (`category_id`),
  CONSTRAINT `FK_user_user_category_category_id` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);