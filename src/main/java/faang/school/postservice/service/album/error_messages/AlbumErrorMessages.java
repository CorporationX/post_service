package faang.school.postservice.service.album.error_messages;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AlbumErrorMessages {
    public static final String ALBUM_NOT_EXISTS = "The album does not exist";
    public static final String TITLE_NOT_UNIQUE = "The album title must be unique";
    public static final String USER_IS_NOT_CREATOR = "The user cannot change someone else's album";
    public static final String ALREADY_FAVORITE = "Album is already favorite";
    public static final String NOT_FAVORITE = "Album is not favorite";
}
