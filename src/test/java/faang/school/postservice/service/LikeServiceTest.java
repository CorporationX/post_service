package faang.school.postservice.service;

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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

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
        likeService.getUsersWhoLikesByPostId(anyLong());
        verify(postService, Mockito.times(1)).getPostById(anyLong());
        verify(likeRepository, Mockito.times(1)).findByPostId(anyLong());
    }

    @Test
    void testExistIdAndGetLikesByCommentId() {
        likeService.getUsersWhoLikesByCommentId(anyLong());
        verify(commentService, Mockito.times(1)).checkCommentExists(anyLong());
        verify(likeRepository, Mockito.times(1)).findByCommentId(anyLong());
    }
}