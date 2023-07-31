package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private ResponsePostMapper responsePostMapper = ResponsePostMapper.INSTANCE;
    @InjectMocks
    private PostService postService;

    @Test
    void updateTest() {
        Post post = Post.builder().id(1L).content("Before").build();
        UpdatePostDto dto = new UpdatePostDto(1L, "After");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.update(dto);

        assertEquals("After", post.getContent());
    }
}