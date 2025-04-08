package faang.school.postservice.validator.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AlbumValidator {

    private static final String ALBUM_TITLE_IS_BLANK = "Album title mustn't be empty";
    private static final String ALBUM_DESCRIPTION_IS_BLANK = "Album description mustn't be empty";
    private static final String LOG_USER_NOT_FOUND = "User with id {} not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String ALBUM_EXIST = "Album already exist";
    private static final String POST_EXIST_IN_ALBUM = "Post already added in the album";
    private static final String USER_NOT_AUTHOR = "User with id {} is not album's author";


    public void checkAlbumDtoTitleAndDescriptionExist(AlbumDto albumDto) {
        if (albumDto.getTitle() == null || albumDto.getTitle().isBlank()) {
            log.warn(ALBUM_TITLE_IS_BLANK);
            throw new DataValidationException(ALBUM_TITLE_IS_BLANK);
        }
        if (albumDto.getDescription() == null || albumDto.getDescription().isBlank()) {
            log.warn(ALBUM_DESCRIPTION_IS_BLANK);
            throw new DataValidationException(ALBUM_DESCRIPTION_IS_BLANK);
        }
    }

    public void checkUserExist(long userId, UserDto userDto) {
        if (userId != userDto.id()) {
            log.warn(LOG_USER_NOT_FOUND, userId);
            throw new EntityNotFoundException(USER_NOT_FOUND);
        }
    }

    public void checkAlbumNotExist(String titile, List<Album> albums) {
        albums.forEach(album -> {
            if(titile.equals(album.getTitle())) {
                log.info(ALBUM_EXIST);
                throw new IllegalArgumentException(ALBUM_EXIST);
            }
        });
    }

    public void checkAlbumAuthorWithUser(long userId, Album album) {
        if(userId != album.getAuthorId()) {
            log.warn(USER_NOT_AUTHOR, userId);
            throw new DataValidationException(USER_NOT_FOUND);
        }
    }

    public void checkPostInAlbum(Post post, Album album) {
        List<Post> posts = album.getPosts();
        if(posts.contains(post)) {
            log.warn(POST_EXIST_IN_ALBUM);
            throw new IllegalArgumentException(POST_EXIST_IN_ALBUM);
        }
    }

}
