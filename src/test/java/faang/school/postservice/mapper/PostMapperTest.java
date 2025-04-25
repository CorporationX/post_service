package faang.school.postservice.mapper;

import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostMapperTest {
    private final HashtagsPostMapper postMapper = Mappers.getMapper(HashtagsPostMapper.class);
    private final LocalDateTime publishedAt = LocalDateTime.now();
    private Post post;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(1L).content("content").authorId(1L).publishedAt(publishedAt).build();
    }

    @Test
    public void testToPostResponseDto_postByAuthor() {
        PostResponseDto responseDto = postMapper.toPostResponseDto(post);
        assertEquals(post.getId(), responseDto.getId());
        assertEquals(post.getContent(), responseDto.getContent());
        assertEquals(post.getAuthorId(), responseDto.getAuthorId());
        assertEquals(post.getPublishedAt().format(dateFormatter), responseDto.getPublishedAt());
    }

    @Test
    public void testToPostResponseDto_postByProject() {
        post.setAuthorId(null);
        post.setProjectId(1L);
        PostResponseDto responseDto = postMapper.toPostResponseDto(post);
        assertEquals(post.getId(), responseDto.getId());
        assertEquals(post.getContent(), responseDto.getContent());
        assertEquals(post.getProjectId(), responseDto.getProjectId());
        assertEquals(post.getPublishedAt().format(dateFormatter), responseDto.getPublishedAt());
    }
}
