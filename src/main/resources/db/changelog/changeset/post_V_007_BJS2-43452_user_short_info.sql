CREATE TABLE user_short_info (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    file_id VARCHAR(255),
    small_file_id VARCHAR(255),
    follower_ids TEXT,
    last_saved_at TIMESTAMP DEFAULT current_timestamp NOT NULL
);

CREATE OR REPLACE FUNCTION update_last_saved_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_saved_at = current_timestamp;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_last_saved_at
BEFORE UPDATE ON user_short_info
FOR EACH ROW
EXECUTE FUNCTION update_last_saved_at();

CREATE INDEX idx_last_saved_at ON user_short_info (last_saved_at);
