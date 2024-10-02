CREATE FUNCTION check_user_active()
    RETURNS TRIGGER AS $$
        BEGIN
            IF NOT EXISTS (SELECT 1 FROM users WHERE id = NEW.user_id AND active = true) THEN
                RAISE EXCEPTION 'User with id % is not active.', NEW.user_id;
            END IF;
            RETURN NEW;
        END;
    $$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_to_album_chosen_users
    BEFORE INSERT ON album_chosen_users
    FOR EACH ROW
    EXECUTE FUNCTION check_user_active();