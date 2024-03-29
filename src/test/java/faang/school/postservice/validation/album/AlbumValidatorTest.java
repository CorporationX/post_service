package faang.school.postservice.validation.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.AlbumVisibility;
import faang.school.postservice.repository.AlbumRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private AlbumValidator albumValidator;

    private Album album;
    private AlbumDto albumDto;
    private UserDto firstFollower;
    private UserDto secondFollower;

    @BeforeEach
    void setUp() {
        album = Album.builder()
                .id(1L)
                .title("Valid title")
                .description("Valid description")
                .authorId(10L)
                .build();
        albumDto = AlbumDto.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .authorId(album.getAuthorId())
                .build();
        firstFollower = UserDto.builder()
                .id(15L)
                .build();
        secondFollower = UserDto.builder()
                .id(20L)
                .build();
    }

    @Test
    void validateAlbumTitle_AlbumWithGivenTitleDoesntExist_ShouldNotThrow() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(false);

        albumValidator.validateAlbumTitle(albumDto);

        assertAll(
                () -> verify(albumRepository, times(1)).existsByTitleAndAuthorId(
                        albumDto.getTitle(), albumDto.getAuthorId()),
                () -> assertDoesNotThrow(() -> albumValidator.validateAlbumTitle(albumDto))
        );
    }

    @Test
    void validateAlbumTitle_AlbumWithGivenTitleAlreadyExists_ShouldThrowDataValidationException() {
        when(albumRepository.existsByTitleAndAuthorId(albumDto.getTitle(), albumDto.getAuthorId())).thenReturn(true);

        assertThrows(DataValidationException.class,
                () -> albumValidator.validateAlbumTitle(albumDto));
    }

    @Test
    void validateIfUserIsAuthor_UserIsAuthor_ShouldNotThrow() {
        assertDoesNotThrow(() -> albumValidator.validateIfUserIsAuthor(10L, album));
    }

    @Test
    void validateIfUserIsAuthor_UserIsNotTheAuthor_ShouldThrowDataValidationException() {
        assertThrows(DataValidationException.class,
                () -> albumValidator.validateIfUserIsAuthor(15L, album));
    }

    @Test
    void validateUpdatedAlbum_AuthorHasntBeenChanged_ShouldNotThrow() {
        albumDto.setTitle("Updated title");
        assertDoesNotThrow(() -> albumValidator.validateUpdatedAlbum(10L, albumDto));
    }

    @Test
    void validateUpdatedAlbum_AuthorWasChanged_ShouldThrowDataValidationException() {
        albumDto.setTitle("Updated title");
        albumDto.setAuthorId(15L);
        assertThrows(DataValidationException.class,
                () -> albumValidator.validateUpdatedAlbum(10L, albumDto));
    }

    @Test
    void validateAccessToAlbum_ClientThrowsException_ShouldThrowEntityNotFoundException() {
        album.setAlbumVisibility(AlbumVisibility.FOLLOWERS_ONLY);
        when(userServiceClient.getFollowers()).thenThrow(FeignException.InternalServerError.class);

        assertThrows(EntityNotFoundException.class,
                () -> albumValidator.validateAccessToAlbum(firstFollower.getId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsPublic_ShouldNotThrow() {
        album.setAlbumVisibility(AlbumVisibility.PUBLIC);

        assertDoesNotThrow(() -> albumValidator.validateAccessToAlbum(album.getAuthorId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsPrivateUserIsNotAuthor_ShouldThrowDataValidationException() {
        album.setAlbumVisibility(AlbumVisibility.PRIVATE);

        assertThrows(DataValidationException.class,
                () -> albumValidator.validateAccessToAlbum(129L, album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsPrivateUserIsAuthor_ShouldNotThrow() {
        album.setAlbumVisibility(AlbumVisibility.PRIVATE);

        assertDoesNotThrow(() -> albumValidator.validateAccessToAlbum(album.getAuthorId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsFollowersOnlyUserIsFollower_ShouldNotThrow() {
        album.setAlbumVisibility(AlbumVisibility.FOLLOWERS_ONLY);
        when(userServiceClient.getFollowers()).thenReturn(List.of(firstFollower, secondFollower));

        assertDoesNotThrow(() -> albumValidator.validateAccessToAlbum(firstFollower.getId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsFollowersOnlyUserIsNotFollower_ShouldThrowDataValidationException() {
        album.setAlbumVisibility(AlbumVisibility.FOLLOWERS_ONLY);
        when(userServiceClient.getFollowers()).thenReturn(List.of(secondFollower));

        assertThrows(DataValidationException.class,
                () -> albumValidator.validateAccessToAlbum(firstFollower.getId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsSelectedUsersOnlyUserIsSelectedUser_ShouldNotThrow() {
        album.setAlbumVisibility(AlbumVisibility.SELECTED_USERS_ONLY);
        album.setAllowedUsersIds(List.of(firstFollower.getId(), secondFollower.getId()));

        assertDoesNotThrow(() -> albumValidator.validateAccessToAlbum(firstFollower.getId(), album));
    }

    @Test
    void validateAccessToAlbum_AlbumIsSelectedUsersOnlyUserIsNotSelectedUser_ShouldThrowDataValidationException() {
        album.setAlbumVisibility(AlbumVisibility.SELECTED_USERS_ONLY);
        album.setAllowedUsersIds(List.of(secondFollower.getId()));

        assertThrows(DataValidationException.class,
                () -> albumValidator.validateAccessToAlbum(firstFollower.getId(), album));
    }
}
