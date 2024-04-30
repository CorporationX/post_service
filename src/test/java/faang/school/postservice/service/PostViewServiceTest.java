package faang.school.postservice.service;

import faang.school.postservice.dto.PostViewDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostViewMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostView;
import faang.school.postservice.publisher.kafka.KafkaEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostViewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewServiceTest {

    @Mock
    private PostViewRepository postViewRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    @Mock
    private PostViewMapper postViewMapper;

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    @InjectMocks
    private PostViewService postViewService;

    private PostViewDto postViewDto;
    private Post post;
    private PostView postView;

    @BeforeEach
    public void setUp() {
        postViewDto = new PostViewDto();
        postViewDto.setPostId(1L);
        postViewDto.setViewerId(1L);

        post = new Post();
        post.setId(1L);

        postView = new PostView();
        postView.setId(1L);
        postView.setPost(post);
    }


    @Test
    public void testAddPostViewWhenPostServiceThrowsNotFoundExceptionThenPostViewIsNotAdded() {
        when(postService.getPost(anyLong())).thenThrow(new NotFoundException("Post not found"));

        try {
            postViewService.addPostView(postViewDto);
        } catch (NotFoundException e) {
            // Expected exception
        }

        verify(postViewRepository, never()).save(any(PostView.class));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    public void testAddPostViewWhenPostViewRepositoryThrowsDataAccessExceptionThenPostViewIsNotAdded() {
        when(postService.getPost(anyLong())).thenReturn(post);
        when(postViewMapper.toEntity(any(PostViewDto.class))).thenReturn(postView);
        when(postViewRepository.save(any(PostView.class))).thenThrow(new DataAccessException("Error") {
        });

        try {
            postViewService.addPostView(postViewDto);
        } catch (DataAccessException e) {
            // Expected exception
        }

        verify(postViewRepository, times(1)).save(any(PostView.class));
        verify(postRepository, never()).save(any(Post.class));
    }
}