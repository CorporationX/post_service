package faang.school.postservice.service.album;

import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.album.AlbumValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private AlbumValidator albumValidator;

    @InjectMocks
    private AlbumService albumService;


    @Test
    public void createAlbum_Test() {
        Album album = new Album();
        AlbumDto albumDto = AlbumDto.builder()
                .build();
        Mockito.when(albumMapper.toEntity(albumDto)).thenReturn(album);
        Mockito.when(albumRepository.save(album)).thenReturn(album);
        Mockito.when(albumMapper.toDto(album)).thenReturn(albumDto);
        albumService.createAlbum(albumDto);
        Mockito.verify(albumMapper).toDto(album);


        System.out.println(album.getCreatedAt());
        System.out.println(album.getUpdatedAt());

        Assertions.assertTrue(album.getCreatedAt().isBefore(album.getUpdatedAt()) ||
                album.getCreatedAt().isEqual(album.getUpdatedAt()));
    }

}
