package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class PostMapperTest {
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private Post post;

    private PostDto postDto;

    @Spy
    private TestData testData;

    @BeforeEach
    void init() {
        post = new Post();
        post.setId(2L);
        postDto = testData.returnPostDto();
    }

    @Test
    void testToEntity() {
        Post actualPost = postMapper.toEntity(postDto);
        assertEquals(post, actualPost);
    }

    @Test
    void testToDto() {
        PostDto actualPostDto = postMapper.toDto(post);
        assertEquals(actualPostDto, postDto);
    }
}
