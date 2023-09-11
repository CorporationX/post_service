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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    List<Like> likes;
    List<UserDto> expectedUsers;

    @BeforeEach
    void setUp() {
        likes = Arrays.asList(
                new Like(1L, 1L, new Comment(), new Post(), LocalDateTime.now()),
                new Like(2L, 2L, new Comment(), new Post(), LocalDateTime.now()),
                new Like(3L, 3L, new Comment(), new Post(), LocalDateTime.now())
        );

        expectedUsers = Arrays.asList(
                new UserDto(1L, "Mike", "Smith@mail.com"),
                new UserDto(2L, "John", "Doe@mail.com"),
                new UserDto(3L, "Jane", "Dory@mail.com")
        );
    }

    @Test
    void testExistIdAndGetLikesByPostId() {
        likeService.getLikesByPostId(anyLong());
        verify(postService, Mockito.times(1)).getPostById(anyLong());
        verify(likeRepository, Mockito.times(1)).findByPostId(anyLong());
    }

    @Test
    void testExistIdAndGetLikesByCommentId() {
        likeService.getLikesByCommentId(anyLong());
        verify(commentService, Mockito.times(1)).checkCommentExists(anyLong());
        verify(likeRepository, Mockito.times(1)).findByCommentId(anyLong());
    }

//    @Test
//    void testFindUsersWhoLiked() {
//        when(likeRepository.findByPostId(anyLong())).thenReturn(likes);
//        when(userServiceClient.getUsersByIds(anyList())).thenReturn(expectedUsers);
//        List<UserDto> result = likeService.getLikesByPostId(anyLong());
//        assertEquals(expectedUsers, result);
//    }
}