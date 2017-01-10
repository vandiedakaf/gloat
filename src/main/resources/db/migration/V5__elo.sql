ALTER TABLE user
  ADD team_id VARCHAR(45) NOT NULL;
ALTER TABLE user
  ADD user_id VARCHAR(45) NOT NULL;

SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE user
  MODIFY id INT(11) NOT NULL AUTO_INCREMENT;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `oauth` (
  `team_id`      VARCHAR(45)  NOT NULL,
  `access_token` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`team_id`)
);

CREATE TABLE `contest` (
  `id`          INT(11) NOT NULL AUTO_INCREMENT,
  `category_id` INT(11) NOT NULL,
  `winner_id`   INT(11) NOT NULL,
  `loser_id`    INT(11) NOT NULL,
  `created`     DATETIME         DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `contest_category_id_fk` (`category_id`),
  CONSTRAINT `contest_category_id_fk` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`),
  KEY `contest_winner_id_fk` (`winner_id`),
  CONSTRAINT `contest_winner_id_fk` FOREIGN KEY (`winner_id`) REFERENCES `user` (`id`),
  KEY `contest_loser_id_fk` (`loser_id`),
  CONSTRAINT `contest_loser_id_fk` FOREIGN KEY (`loser_id`) REFERENCES `user` (`id`)
);
