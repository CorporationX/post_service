package faang.school.postservice.album.mapper;

import faang.school.postservice.model.dto.album.AlbumDto;
import faang.school.postservice.mapper.AlbumMapper;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlbumMapperTest {

    private AlbumMapper albumMapper;

    @BeforeEach
    public void setUp() {
        albumMapper = Mappers.getMapper(AlbumMapper.class);
    }

    @Test
    public void testAlbumToAlbumDto() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        posts.add(post1);

        Post post2 = new Post();
        post2.setId(2L);
        posts.add(post2);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Test Album");
        album.setDescription("Test Description");
        album.setPosts(posts);

        AlbumDto albumDto = albumMapper.albumToAlbumDto(album);

        Assertions.assertNotNull(albumDto);
        assertEquals("Test Album", albumDto.getTitle());
        assertEquals("Test Description", albumDto.getDescription());

        Assertions.assertNotNull(albumDto.getPostIds());
        assertEquals(2, albumDto.getPostIds().size());
        assertEquals(1L, albumDto.getPostIds().get(0));
        assertEquals(2L, albumDto.getPostIds().get(1));
    }

    @Test
    public void testToDtoList() {
        List<Post> posts = new ArrayList<>();
        Post post1 = new Post();
        post1.setId(1L);
        posts.add(post1);

        Album album1 = new Album();
        album1.setId(1L);
        album1.setTitle("Album 1");
        album1.setDescription("Description 1");
        album1.setPosts(posts);

        Album album2 = new Album();
        album2.setId(2L);
        album2.setTitle("Album 2");
        album2.setDescription("Description 2");
        album2.setPosts(posts);

        List<Album> albums = new ArrayList<>();
        albums.add(album1);
        albums.add(album2);

        List<AlbumDto> albumDtos = albumMapper.toDtoList(albums);

        Assertions.assertNotNull(albumDtos);
        assertEquals(2, albumDtos.size());

        assertEquals("Album 1", albumDtos.get(0).getTitle());
        assertEquals("Description 1", albumDtos.get(0).getDescription());
        Assertions.assertNotNull(albumDtos.get(0).getPostIds());
        assertEquals(1, albumDtos.get(0).getPostIds().size());
        assertEquals(1L, albumDtos.get(0).getPostIds().get(0));

        assertEquals("Album 2", albumDtos.get(1).getTitle());
        assertEquals("Description 2", albumDtos.get(1).getDescription());
        Assertions.assertNotNull(albumDtos.get(1).getPostIds());
        assertEquals(1, albumDtos.get(1).getPostIds().size());
        assertEquals(1L, albumDtos.get(1).getPostIds().get(0));
    }
}

