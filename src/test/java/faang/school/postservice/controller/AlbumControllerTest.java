package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {
    @InjectMocks
    private AlbumController albumController;
    @Mock
    private AlbumService albumService;

    private AlbumDto trueAlbumDto;

    @BeforeEach
    void setUp() {
        trueAlbumDto = AlbumDto.builder().id(1L).authorId(1L).title("title").description("description").build();
    }

    @Test
    void testCreateAlbumSuccess() {
        albumController.createAlbum(trueAlbumDto);
        verify(albumService, times(1)).createAlbum(trueAlbumDto);
    }

    @Test
    void testUpdateAlbumSuccess() {
        albumController.updateAlbum(trueAlbumDto);
        verify(albumService, times(1)).updateAlbum(trueAlbumDto);
    }

    @Test
    void testUpdateAlbumFailIfAuthorIdIsNull() {
        AlbumDto wrongAlbumDto = AlbumDto.builder().title("title").description("description").build();
        assertThrows(NullPointerException.class, () -> albumController.updateAlbum(wrongAlbumDto));

        verifyNoInteractions(albumService);
    }

    @Test
    void testUpdateAlbumFailIfTitleIsEmpty() {
        AlbumDto wrongAlbumDto = AlbumDto.builder().authorId(1L).description("description").build();
        assertThrows(NullPointerException.class, () -> albumController.updateAlbum(wrongAlbumDto));

        verifyNoInteractions(albumService);
    }

    @Test
    void testUpdateAlbumFailIfDescriptionIsEmpty() {
        AlbumDto wrongAlbumDto = AlbumDto.builder().authorId(1L).title("title").build();
        assertThrows(NullPointerException.class, () -> albumController.updateAlbum(wrongAlbumDto));

        verifyNoInteractions(albumService);
    }

    @Test
    void testDeleteAlbumSuccess() {
        albumController.deleteAlbum(1L);
        verify(albumService, times(1)).deleteAlbum(1L);
    }

    @ParameterizedTest
    @MethodSource("validParametersProvider")
    public void testAddPostSuccess(Long userId, Long albumId, Long postId) {
        AlbumDto expectedAlbumDto = AlbumDto.builder().build();
        when(albumService.addPost(userId, albumId, postId)).thenReturn(expectedAlbumDto);

        AlbumDto actualAlbumDto = albumController.addPost(userId, albumId, postId);

        assertEquals(expectedAlbumDto, actualAlbumDto);
    }

    @ParameterizedTest
    @MethodSource("invalidParametersProvider")
    public void testAddPostWithInvalidParameters(Long userId, Long albumId, Long postId) {
        assertThrows(DataValidationException.class, () -> albumController.addPost(userId, albumId, postId));
    }

    @ParameterizedTest
    @MethodSource("validParametersProvider")
    public void testDeletePostSuccess(Long userId, Long albumId, Long postId) {
        AlbumDto expectedAlbumDto = AlbumDto.builder().build();
        when(albumService.deletePost(userId, albumId, postId)).thenReturn(expectedAlbumDto);

        AlbumDto actualAlbumDto = albumController.deletePost(userId, albumId, postId);

        assertEquals(expectedAlbumDto, actualAlbumDto);
    }

    @ParameterizedTest
    @MethodSource("invalidParametersProvider")
    public void testDeletePostWithInvalidParameters(Long userId, Long albumId, Long postId) {
        assertThrows(DataValidationException.class, () -> albumController.deletePost(userId, albumId, postId));
    }

    static Stream<Arguments> invalidParametersProvider() {
        return Stream.of(
                Arguments.of(-1L, 2L, 3L),
                Arguments.of(1L, -2L, 3L),
                Arguments.of(1L, 2L, -3L)
        );
    }

    static Stream<Arguments> validParametersProvider() {
        return Stream.of(
                Arguments.of(1L, 2L, 3L),
                Arguments.of(4L, 5L, 6L),
                Arguments.of(7L, 8L, 9L)
        );
    }
}