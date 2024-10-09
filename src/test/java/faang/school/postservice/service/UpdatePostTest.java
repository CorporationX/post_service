package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.enums.AuthorType;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.mapper.PostMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdatePostTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private Post existingPost;
    private PostDto updatedPostDto;

    @BeforeEach
    void setUp() {
        existingPost = new Post();
        existingPost.setId(1L);
        existingPost.setAuthorId(100L);
        existingPost.setContent("The old, original content");

        updatedPostDto = new PostDto();
        updatedPostDto.setId(1L);
        updatedPostDto.setAuthorId(100L);
        updatedPostDto.setAuthorType(AuthorType.USER);
        updatedPostDto.setContent("The new, updated content");
    }

    @Test
    void shouldUpdatePostSuccessfully() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(existingPost));

        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        when(postMapper.toPostDto(any(Post.class))).thenReturn(updatedPostDto);

        PostDto result = postService.updatePost(1L, updatedPostDto);

        assertNotNull(result);
        assertEquals("The new, updated content", result.getContent());

        verify(postRepository).save(argThat(post -> post.getContent().equals("The new, updated content") && post.getId() == 1L));

        verify(postMapper).toPostDto(any(Post.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthorOrTypeChanged() {
        PostDto invalidPostDto = new PostDto();
        invalidPostDto.setId(1L);
        invalidPostDto.setAuthorId(200L);
        invalidPostDto.setAuthorType(AuthorType.PROJECT);
        invalidPostDto.setContent("Attempting to update content");

        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(existingPost));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            postService.updatePost(1L, invalidPostDto);
        });

        assertEquals("Cannot change author or author type of the post", exception.getMessage());
        verify(postRepository, never()).save(any(Post.class));
    }
}