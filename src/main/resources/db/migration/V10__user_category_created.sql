ALTER TABLE user_category
  ADD created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6);

CREATE INDEX user_category_created_index
  ON user_category (created DESC);

