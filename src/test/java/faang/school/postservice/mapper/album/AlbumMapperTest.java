package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.album.Album;
import faang.school.postservice.util.album.BuilderForAlbumsTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static faang.school.postservice.util.album.BuilderForAlbumsTests.buildAlbum;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumMapperTest {
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    private AlbumMapper mapper;
    private Album album;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AlbumMapper.class);
    }

    @Test
    void testAlbumToAlbumResponseDto() {
        List<Long> postIds = new ArrayList<>();
        List<Post> posts = LongStream.rangeClosed(1, 5)
                .peek(postIds::add)
                .mapToObj(BuilderForAlbumsTests::buildPost)
                .toList();
        album = buildAlbum(1, TITLE, DESCRIPTION, 2, posts);

        AlbumResponseDto albumResponseDto = mapper.toAlbumResponseDto(album);

        assertThat(album)
                .usingRecursiveComparison()
                .ignoringFields("posts", "chosenUsers")
                .isEqualTo(albumResponseDto);
        assertEquals(postIds, albumResponseDto.getPostIds());
    }

    @Test
    void testAlbumResponseDtoToEntity() {
        CreateAlbumDto createAlbumDto = new CreateAlbumDto();
        createAlbumDto.setTitle(TITLE);
        createAlbumDto.setDescription(DESCRIPTION);

        album = mapper.toEntity(createAlbumDto);

        assertThat(createAlbumDto)
                .usingRecursiveComparison()
                .ignoringFields("chosenUserIds")
                .isEqualTo(album);
    }
}