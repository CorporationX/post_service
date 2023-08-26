package faang.school.postservice.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.util.exception.EntityNotFoundException;
import faang.school.postservice.util.exception.NotAllowedException;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Visibility;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import faang.school.postservice.util.validator.AccessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepository repository;

    @Mock
    private AlbumMapper mapper;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private AccessValidator accessValidator;

    @InjectMocks
    private AlbumService albumService;

    AlbumDto albumDto;
    Album album;

    @BeforeEach
    public void setUp() {
        albumDto = AlbumDto.builder()
                .id(1L)
                .title("Title4")
                .description("Album Descri2ption 2")
                .authorId(2L)
                .postIds(new ArrayList<>())
                .visibility(Visibility.EVERYONE)
                .allowedUsersIds(Arrays.asList(1L, 4L, 5L))
                .build();
        album = Album.builder()
                .id(1)
                .title("Title4")
                .description("Album Descri2ption 2")
                .authorId(2L)
                .posts(new ArrayList<>())
                .visibility(Visibility.EVERYONE)
                .allowedUsersIds("[1, 4, 5]")
                .build();
    }

    @Test
    public void testCreateAlbum() throws JsonProcessingException {
        when(mapper.toEntity(albumDto)).thenReturn(album);
        when(repository.save(album)).thenReturn(album);
        when(mapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.createAlbum(albumDto);
        assertEquals(albumDto, result);
    }

    @Test
    public void testGetAlbum() throws JsonProcessingException {
        when(repository.findById(albumDto.getId())).thenReturn(Optional.of(album));
        when(mapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.getAlbum(albumDto.getId(), albumDto.getAuthorId());
        assertEquals(albumDto, result);
    }

    @Test
    public void testGetAlbumNotFound() {
        when(repository.findById(albumDto.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> albumService.getAlbum(albumDto.getId(), albumDto.getAuthorId()));
    }

    @Test
    public void testGettingNotAllowed() throws JsonProcessingException {
        when(repository.findById(albumDto.getId())).thenReturn(Optional.of(album));
        doThrow(new NotAllowedException("message")).when(accessValidator).validateAccess(Mockito.any(), Mockito.anyLong());
        assertThrows(NotAllowedException.class,
                () -> albumService.getAlbum(albumDto.getId(), albumDto.getAuthorId() + 12));
    }

    @Test
    public void testGettingPrivateAlbumException() throws JsonProcessingException {
        album.setVisibility(Visibility.ONLY_ME);
        when(repository.findById(albumDto.getId())).thenReturn(Optional.of(album));
        doThrow(new NotAllowedException("message")).when(accessValidator).validateAccess(album, albumDto.getAuthorId() + 1);

        assertThrows(NotAllowedException.class,
                () -> albumService.getAlbum(albumDto.getId(), albumDto.getAuthorId() + 1));
    }

    @Test
    public void UpdateAlbumTest() throws JsonProcessingException {
        albumDto.setAuthorId(12L);
        when(mapper.toEntity(albumDto)).thenReturn(album);
        when(repository.save(album)).thenReturn(album);
        when(mapper.toDto(album)).thenReturn(albumDto);

        AlbumDto result = albumService.update(albumDto, albumDto.getAuthorId());

        assertEquals(albumDto, result);
        verify(accessValidator, times(1)).validateUpdateAccess(albumDto, albumDto.getAuthorId());
    }

    @Test
    public void UpdateNotAllowedExceptionTest() {
        long userId = 123L;

        doThrow(new NotAllowedException("Update not allowed")).when(accessValidator).validateUpdateAccess(albumDto, userId);

        assertThrows(NotAllowedException.class, () -> albumService.update(albumDto, userId));
        verify(accessValidator).validateUpdateAccess(albumDto, userId);
        verifyNoInteractions(repository);
    }

    @Test
    public void testDeleteAlbum_Success() {
        long albumId = 1L;
        long userId = 2L;

        when(repository.findById(albumId)).thenReturn(Optional.of(album));
        doNothing().when(accessValidator).validateUpdateAccess(album, userId);

        albumService.delete(albumId, userId);

        verify(repository).delete(album);
    }

    @Test
    public void testDeleteAlbum_EntityNotFoundException() {
        long albumId = 1L;
        long userId = 2L;
        when(repository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> albumService.delete(albumId, userId));
        verify(repository, never()).delete(any());
    }

    @Test
    public void testDeleteAlbum_NotAllowedException() {
        long albumId = 1L;
        long userId = 2L;
        when(repository.findById(albumId)).thenReturn(Optional.of(album));
        doThrow(new NotAllowedException("Update not allowed")).when(accessValidator).validateUpdateAccess(album, userId);

        assertThrows(NotAllowedException.class, () -> albumService.delete(albumId, userId));
        verify(repository, never()).delete(any());
    }
}
