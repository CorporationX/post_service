package faang.school.postservice.validator.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.enums.Visibility;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class AlbumValidatorTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @InjectMocks
    private AlbumValidator albumValidator;

    @Test
    public void privacyWrongTestWithPrivateAlbum() {
        var album = new Album();
        album.setAuthorId(1L);
        album.setVisibility(Visibility.PRIVATE);
        Mockito.when(albumRepository.findById(1L)).thenReturn(java.util.Optional.of(album));

        assertThrows(DataValidationException.class, () -> {
            albumValidator.privacyCheck(2L, 1L);
        });

    }

    @Test
    public void privacyTestWithOnlySubsAlbum() {
        var album = new Album();
        album.setAuthorId(1L);
        album.setVisibility(Visibility.ONLY_SUBSCRIBERS);
        Mockito.when(albumRepository.findById(1L)).thenReturn(java.util.Optional.of(album));
        Mockito.when(userServiceClient.getUser(1L)).thenReturn(UserDto.builder().id(1L).followedUserIds(List.of(2L)).build());

        albumValidator.privacyCheck(2L, 1L);
    }

    @Test
    public void privacyWrongTestWithOnlySubsAlbum() {
        var album = new Album();
        album.setAuthorId(1L);
        album.setVisibility(Visibility.ONLY_SUBSCRIBERS);
        Mockito.when(albumRepository.findById(1L)).thenReturn(java.util.Optional.of(album));
        Mockito.when(userServiceClient.getUser(1L)).thenReturn(UserDto.builder().id(1L).followedUserIds(List.of(2L)).build());

        assertThrows(DataValidationException.class, () -> {
            albumValidator.privacyCheck(3L, 1L);
        });
    }

    @Test
    public void privacyTestWithOnlySelectedAlbum() {
        var album = new Album();
        album.setAuthorId(1L);
        album.setVisibility(Visibility.ONLY_SELECTED);
        album.setAllowedUserIds(List.of(2L));
        Mockito.when(albumRepository.findById(1L)).thenReturn(java.util.Optional.of(album));

        albumValidator.privacyCheck(2L, 1L);

    }
}