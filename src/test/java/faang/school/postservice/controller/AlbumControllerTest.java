package faang.school.postservice.controller;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.service.album.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

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
}