ALTER TABLE user_category
  ADD modified DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6);

CREATE INDEX user_category_modified_index
  ON user_category (modified DESC);

