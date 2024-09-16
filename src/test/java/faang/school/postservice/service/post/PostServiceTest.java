package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.entity.Post;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostRepository postRepository;

    private PostDto examplePostDto;
    private Post examplePost;

    @BeforeEach
    void setUp() {
        examplePostDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .build();

        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .build();
    }

    @Test
    void createDraftPost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.createDraftPost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
    }
}
