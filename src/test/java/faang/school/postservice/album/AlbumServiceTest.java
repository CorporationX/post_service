package faang.school.postservice.album;

import faang.school.postservice.mapper.album.AlbumMapper;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.service.AlbumService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {
    @InjectMocks
    private AlbumService albumService;

    @Mock
    private AlbumRepository albumRepository;

    @Spy
    private AlbumMapper albumMapper;

    @Test
    public void testCreateAlbum(){

    }
}
