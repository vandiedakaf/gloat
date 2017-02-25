ALTER TABLE contest
  ADD contest_outcome VARCHAR(2) NOT NULL
  AFTER loser_id;

ALTER TABLE contest
  CHANGE winner_id reporter_id INT(11) NOT NULL;
ALTER TABLE contest
  CHANGE loser_id opponent_id INT(11) NOT NULL;

ALTER TABLE user_category
  ADD draws INT(11) DEFAULT 0 NULL;
ALTER TABLE user_category
  CHANGE contest_total losses INT(11) NOT NULL DEFAULT 0;
ALTER TABLE user_category
  CHANGE contest_wins wins INT(11) NOT NULL DEFAULT 0;
ALTER TABLE user_category
  ALTER COLUMN k SET DEFAULT 0;
ALTER TABLE user_category
  MODIFY COLUMN losses INT(11) NOT NULL DEFAULT 0
  AFTER wins;

CREATE INDEX contest__created_index
  ON contest (created DESC);

ALTER TABLE user_category
  MODIFY elo INT(11) NOT NULL;
