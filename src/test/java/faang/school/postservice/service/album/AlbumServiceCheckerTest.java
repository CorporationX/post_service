package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.exception.BadRequestException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.ALBUM_NOT_EXISTS;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.TITLE_NOT_UNIQUE;
import static faang.school.postservice.service.album.error_messages.AlbumErrorMessages.USER_IS_NOT_CREATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceCheckerTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClientMock userServiceClient;

    @InjectMocks
    private AlbumServiceChecker checker;

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
        long userId = 1;

        userServiceClient.getUser(userId);

        verify(userServiceClient, Mockito.times(1)).getUser(userId);
    }

    @Test
    void testFindByIdWithPostsIsSuccessful() {
        long albumId = 1;
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.of(new Album()));
        Album album = checker.findByIdWithPosts(albumId);

        assertNotNull(album);
    }

    @Test
    void testFindByIdWithPostsIsNotSuccessful() {
        long albumId = 1;
        when(albumRepository.findByIdWithPosts(albumId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(BadRequestException.class, () -> checker.findByIdWithPosts(albumId));
        assertEquals(ALBUM_NOT_EXISTS, exception.getMessage());
    }

    @Test
    void testCheckAlbumExistsWithTitleIsSuccessful() {
        long authorId = 1;
        String title = "title";
        when(albumRepository.existsByTitleAndAuthorId(title, authorId)).thenReturn(false);

        checker.checkAlbumExistsWithTitle(title, authorId);

        verify(albumRepository, Mockito.times(1)).existsByTitleAndAuthorId(title, authorId);
    }

    @Test
    void testCheckAlbumExistsWithTitleIsNotSuccessful() {
        long authorId = 1;
        String title = "title";
        when(albumRepository.existsByTitleAndAuthorId(title, authorId)).thenReturn(true);

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkAlbumExistsWithTitle(title, authorId));
        assertEquals(TITLE_NOT_UNIQUE, exception.getMessage());
    }

    @Test
    void testIsNotCreatorOfAlbum() {
        long userId = 1;
        long authorId = 2;
        Album album = new Album();
        album.setAuthorId(authorId);

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.isCreatorOfAlbum(userId, album));
        assertEquals(USER_IS_NOT_CREATOR, exception.getMessage());
    }

    @Test
    void checkFavoritesAlbumsWhenContainsAlbum() {
        long userId = 1;
        Album album = new Album();
        album.setAuthorId(userId);
        List<Album> favoritesAlbums = List.of(album);
        String exceptionMessage = "Some message";
        boolean isContains = true;
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(favoritesAlbums.stream());

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkFavoritesAlbumsContainsAlbum(userId, album, exceptionMessage, isContains));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void checkFavoritesAlbumsWhenNotContainsAlbum() {
        long userId = 1;
        Album album = new Album();
        album.setAuthorId(userId);
        Album secondAlbum = new Album();
        List<Album> favoritesAlbums = List.of(secondAlbum);
        String exceptionMessage = "Some message";
        boolean isContains = false;
        when(albumRepository.findFavoriteAlbumsByUserId(userId)).thenReturn(favoritesAlbums.stream());

        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> checker.checkFavoritesAlbumsContainsAlbum(userId, album, exceptionMessage, isContains));
        assertEquals(exceptionMessage, exception.getMessage());
    }
}