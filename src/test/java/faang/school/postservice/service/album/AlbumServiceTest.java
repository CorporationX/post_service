package faang.school.postservice.service.album;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.album.AlbumDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.album.AlbumMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.validator.AlbumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {
    private static final String EXPECTED_MESSAGE_USER_NOT_FOUND = "User not found";
    private static final String EXPECTED_MESSAGE_TITLE_CANNOT_BE_UNIQUE = "Title must be unique";
    private static final String EXPECTED_MESSAGE_DESCRIPTION_CANNOT_BE_NULL = "Description cannot be null";

    @InjectMocks
    private AlbumService albumService;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumValidator albumValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private AlbumMapperImpl albumMapper;

    private AlbumDto trueAlbumDto;
    private UserDto userDto;


    @BeforeEach
    void setUp() {
        trueAlbumDto = AlbumDto.builder().authorId(1L).title("title1").description("description2").build();
        userDto = UserDto.builder().id(1L).build();
    }

    @Test
    void testCreateAlbumSuccess() {
        Album album = albumMapper.toAlbum(trueAlbumDto);
        when(userServiceClient.getUser(trueAlbumDto.getAuthorId())).thenReturn(userDto);
        when(albumRepository.save(album)).thenReturn(album);
        AlbumDto albumDto = albumService.createAlbum(trueAlbumDto);

        assertAll(
                () -> assertNull(trueAlbumDto.getId()),
                () -> assertNotNull(albumDto.getId()),
                () -> assertEquals(trueAlbumDto.getAuthorId(), albumDto.getAuthorId()),
                () -> assertEquals(trueAlbumDto.getTitle(), albumDto.getTitle()),
                () -> assertEquals(trueAlbumDto.getDescription(), albumDto.getDescription())
        );
    }

    @Test
    void testCreateAlbumFailIfAuthorIdIsNull() {
        AlbumDto wrongAlbumDto = AlbumDto.builder().authorId(2L).title("title").description("description").build();
        when(userServiceClient.getUser(wrongAlbumDto.getAuthorId())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> albumService.createAlbum(wrongAlbumDto));
    }

    @Test
    void testCreateAlbumFailIfTitleIsNotUnique() {
        try {
            when(userServiceClient.getUser(trueAlbumDto.getAuthorId())).thenReturn(userDto);
            when(albumRepository.existsByTitleAndAuthorId(trueAlbumDto.getTitle(), userDto.getId())).thenReturn(true);
            albumService.createAlbum(trueAlbumDto);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), EXPECTED_MESSAGE_TITLE_CANNOT_BE_UNIQUE);
        }
    }

    @Test
    void testUpdateAlbumSuccess() {
    }

    @Test
    void deleteAlbum() {
    }
}