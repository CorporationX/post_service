package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(likeService, "maxNumberUsersInRequest", 2);
    }

    @Test
    public void testFindAllUserWhoLikedPost() {
        long postId = 1;
        Post post = Post.builder().id(postId).build();
        List<Like> likes = new ArrayList<>();
        for (long i = 0L; i < 10L; i++) {
            likes.add(Like.builder().id(i).userId(i).post(post).build());
        }
        List<UserDto> userDtos = new ArrayList<>();
        for (long i = 0L; i < 10L; i++) {
            userDtos.add(new UserDto(i, "eee", "hjhjhj"));
        }
        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(List.of(userDtos.get(0), userDtos.get(1)))
                .thenReturn(List.of(userDtos.get(2), userDtos.get(3)))
                .thenReturn(List.of(userDtos.get(4), userDtos.get(5)))
                .thenReturn(List.of(userDtos.get(6), userDtos.get(7)))
                .thenReturn(List.of(userDtos.get(8), userDtos.get(9)));
        List<UserDto> result = likeService.findAllUserWhoLikedPost(postId);
        assertNotNull(result);
        assertEquals(10, result.size());
        verify(likeRepository,times(1)).findByPostId(postId);
        verify(userServiceClient,times(5)).getUsersByIds(anyList());
    }

    @Test
    public void testFindAllUserWhoLikedComment() {
        long commentId = 1;
        Comment comment = Comment.builder().id(commentId).build();
        List<Like> likes = new ArrayList<>();
        for (long i = 0L; i < 10L; i++) {
            likes.add(Like.builder().id(i).userId(i).comment(comment).build());
        }
        List<UserDto> userDtos = new ArrayList<>();
        for (long i = 0L; i < 10L; i++) {
            userDtos.add(new UserDto(i, "eee", "hjhjhj"));
        }
        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList()))
                .thenReturn(List.of(userDtos.get(0), userDtos.get(1)))
                .thenReturn(List.of(userDtos.get(2), userDtos.get(3)))
                .thenReturn(List.of(userDtos.get(4), userDtos.get(5)))
                .thenReturn(List.of(userDtos.get(6), userDtos.get(7)))
                .thenReturn(List.of(userDtos.get(8), userDtos.get(9)));
        List<UserDto> result = likeService.findAllUserWhoLikedComment(commentId);
        assertNotNull(result);
        assertEquals(10, result.size());
        verify(likeRepository,times(1)).findByCommentId(commentId);
        verify(userServiceClient,times(5)).getUsersByIds(anyList());
    }
}
