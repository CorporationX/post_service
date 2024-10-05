package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.model.album.AlbumVisibility.ALL_USERS;
import static faang.school.postservice.model.album.AlbumVisibility.CHOSEN_USERS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALBUM_NOT_EXISTS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALREADY_FAVORITE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.NOT_FAVORITE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.TITLE_NOT_UNIQUE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.USER_IS_NOT_CREATOR;
import static faang.school.postservice.util.album.BuilderForAlbumsTests.buildAlbum;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceCheckerTest {
    private static final long USER_ID = 1;
    private static final long ALBUM_ID = 1;
    private static final String TITLE = "title";

    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClientMock userServiceClient;

    @InjectMocks
    private AlbumServiceChecker checker;

    private Album album;

    @Test
    void testIsExistingPosts() {
        long postId = 1;
        when(postRepository.existsById(postId)).thenReturn(true);

        boolean isExist = checker.isExistingPosts(postId);

        verify(postRepository, Mockito.times(1)).existsById(postId);
        assertTrue(isExist);
    }

    @Test
    void testCheckUserExists() {
        userServiceClient.getUser(USER_ID);

        verify(userServiceClient, Mockito.times(1)).getUser(USER_ID);
    }

    @Test
    void testFindByIdWithPostsIsSuccessful() {
        when(albumRepository.findByIdWithPosts(ALBUM_ID)).thenReturn(Optional.of(new Album()));
        album = checker.findByIdWithPosts(ALBUM_ID);

        assertNotNull(album);
    }

    @Test
    void testFindByIdWithPostsIsNotSuccessful() {
        when(albumRepository.findByIdWithPosts(ALBUM_ID)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(BadRequestException.class, () -> checker.findByIdWithPosts(ALBUM_ID));
        String expected = String.format(ALBUM_NOT_EXISTS, ALBUM_ID);
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testCheckAlbumExistsWithTitleIsSuccessful() {
        when(albumRepository.existsByTitleAndAuthorId(TITLE, USER_ID)).thenReturn(false);

        checker.checkAlbumExistsWithTitle(TITLE, USER_ID);

        verify(albumRepository, Mockito.times(1)).existsByTitleAndAuthorId(TITLE, USER_ID);
    }

    @Test
    void testCheckAlbumExistsWithTitleIsNotSuccessful() {
        when(albumRepository.existsByTitleAndAuthorId(TITLE, USER_ID)).thenReturn(true);

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkAlbumExistsWithTitle(TITLE, USER_ID));
        String expected = String.format(TITLE_NOT_UNIQUE, USER_ID, TITLE);
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void testIsNotCreatorOfAlbum() {
        long authorId = 2;
        album = buildAlbum(authorId);

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.isCreatorOfAlbum(USER_ID, album));
        String expected = String.format(USER_IS_NOT_CREATOR, USER_ID, album.getId());
        assertEquals(expected, exception.getMessage());
    }

    @Test
    void checkFavoritesAlbumsWhenContainsAlbum() {
        album = buildAlbum(USER_ID);
        List<Album> favoritesAlbums = List.of(album);
        String exceptionMessage = ALREADY_FAVORITE;
        boolean isContains = true;
        when(albumRepository.findFavoriteAlbumsByUserId(USER_ID)).thenReturn(favoritesAlbums.stream());

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkFavoritesAlbumsContainsAlbum(USER_ID, album, exceptionMessage, isContains));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void checkFavoritesAlbumsWhenNotContainsAlbum() {
        album = buildAlbum(USER_ID);
        Album secondAlbum = new Album();
        List<Album> favoritesAlbums = List.of(secondAlbum);
        String exceptionMessage = NOT_FAVORITE;
        boolean isContains = false;
        when(albumRepository.findFavoriteAlbumsByUserId(USER_ID)).thenReturn(favoritesAlbums.stream());

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkFavoritesAlbumsContainsAlbum(USER_ID, album, exceptionMessage, isContains));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testValidateAlbumVisibility_CHOSEN_USERS() {
        List<Long> chosenUserIds = null;
        assertThrows(BadRequestException.class, () ->
                checker.validateAlbumVisibility(CHOSEN_USERS, chosenUserIds)
        );
    }

    @Test
    void testValidateAlbumVisibility_NOT_CHOSEN_USERS() {
        List<Long> chosenUserIds = new ArrayList<>();
        assertThrows(BadRequestException.class, () ->
                checker.validateAlbumVisibility(ALL_USERS, chosenUserIds)
        );
    }
}