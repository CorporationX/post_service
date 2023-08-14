package faang.school.postservice.service.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    @Test
    public void byPostTest() {
        when(likeRepository.findByPostId(1L)).thenReturn(createLikeList());

        assertNotNull(likeService.getUsersByPostId(1L));

        verify(userServiceClient).getUsersByIds(anyList());
    }

    @Test
    public void byCommentTest() {
        when(likeRepository.findByCommentId(2L)).thenReturn(createLikeList());

        assertNotNull(likeService.getUsersByCommentId(2L));

        verify(userServiceClient).getUsersByIds(anyList());
    }

    private List<Like> createLikeList() {
        return List.of(Like.builder().id(5).post(createPost()).comment(createComment()).userId(1L).build(),
                Like.builder().id(6).post(createPost()).comment(createComment()).userId(2L).build(),
                Like.builder().id(7).post(createPost()).comment(createComment()).userId(3L).build(),
                Like.builder().id(8).post(createPost()).comment(createComment()).userId(4L).build(),
                Like.builder().id(9).post(createPost()).comment(createComment()).userId(5L).build());
    }

    private Post createPost() {
        return Post.builder().id(1L).build();
    }

    private Comment createComment() {
        return Comment.builder().id(2L).build();
    }
}