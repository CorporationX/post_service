package faang.school.postservice.service.album.error_messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AlbumErrorMessages {
    public static final String USER_NOT_FOUND = "User with id %d not found";
    public static final String ALBUM_NOT_EXISTS = "The album with id %d not exist";
    public static final String TITLE_NOT_UNIQUE = "The user with id %d already has album with title - %s";
    public static final String USER_IS_NOT_CREATOR =
            "User with id %d is not a creator of album with id %d. The user cannot change someone else's album";
    public static final String ALREADY_FAVORITE = "Album is already favorite";
    public static final String NOT_FAVORITE = "Album is not favorite";
}
