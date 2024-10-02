CREATE FUNCTION delete_album_chosen_users_if_user_became_inactive()
    RETURNS TRIGGER AS $$
        BEGIN
            IF OLD.active = true AND NEW.active = false THEN
                DELETE FROM album_chosen_users WHERE user_id = NEW.id;
            END IF;
            RETURN NEW;
        END;
    $$ LANGUAGE plpgsql;

CREATE TRIGGER after_update_user_active
    AFTER UPDATE OF active ON users
    FOR EACH ROW
    WHEN (OLD.active = true AND NEW.active = false)
    EXECUTE FUNCTION delete_album_chosen_users_if_user_became_inactive();