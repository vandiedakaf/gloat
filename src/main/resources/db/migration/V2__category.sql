SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE category;
ALTER TABLE category MODIFY id INT(11) NOT NULL AUTO_INCREMENT;
SET FOREIGN_KEY_CHECKS = 1;

ALTER TABLE category ADD description VARCHAR(45) NULL;

INSERT INTO category (id, description) VALUES
  (1, "Pool"),
  (2, "Fifa"),
  (3, "Rocket League");

