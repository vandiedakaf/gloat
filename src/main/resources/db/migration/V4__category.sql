ALTER TABLE category DROP description;
ALTER TABLE category DROP enabled;
DELETE FROM category WHERE 1=1;
ALTER TABLE category ADD channel_id VARCHAR(45) NOT NULL;
ALTER TABLE category ADD team_id VARCHAR(45) NOT NULL;
ALTER TABLE category ADD CONSTRAINT category_team_id_channel_id_pk UNIQUE (team_id, channel_id);

