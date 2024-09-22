package faang.school.postservice.mapper.album;

import faang.school.postservice.dto.album.AlbumResponseDto;
import faang.school.postservice.dto.album.CreateAlbumDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AlbumMapperTest {
    AlbumMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(AlbumMapper.class);
    }

    @Test
    void toAlbumResponseDto() {
        Post post1 = new Post();
        post1.setId(1);
        List<Long> postIds = new ArrayList<>();
        List<Post> posts = LongStream.rangeClosed(1, 5)
                .peek(postIds::add)
                .mapToObj(id -> {
                    Post post = new Post();
                    post.setId(id);
                    return post;
                })
                .toList();
        Album album = Album.builder()
                .id(1)
                .title("Some title")
                .description("descripton")
                .authorId(2)
                .posts(posts)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        AlbumResponseDto albumResponseDto = mapper.toAlbumResponseDto(album);

        assertThat(album)
                .usingRecursiveComparison()
                .ignoringFields("posts")
                .isEqualTo(albumResponseDto);
        assertEquals(postIds, albumResponseDto.getPostIds());
    }

    @Test
    void toEntity() {
        CreateAlbumDto createAlbumDto = new CreateAlbumDto();
        createAlbumDto.setTitle("Some title");
        createAlbumDto.setDescription("description");

        Album album = mapper.toEntity(createAlbumDto);

        assertThat(createAlbumDto)
                .usingRecursiveComparison()
                .isEqualTo(album);
    }
}