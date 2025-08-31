package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для LikeServiceImpl")
public class LikeServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userClient;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private LikeServiceImpl likeService;

    long postId = 1L;
    long commentId = 2L;
    long userId = 3L;
    String userName = "User";
    String userEmail = "@123";
    Like like = new Like();
    Post post = new Post();
    Comment comment = new Comment();
    UserDto userDto = new UserDto(userId, userName, userEmail);
    List<UserDto> userDtoList = List.of(userDto);
    List<Like> likesList = List.of(like);
    List<Long> userIdList = List.of(userId);

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikersTest() {
        like.setUserId(userId);
        when(likeRepository.findAllByPostId(postId)).thenReturn(likesList);
        when(userClient.getListUsers(userIdList)).thenReturn(userDtoList);

        List<UserDto> resultList = likeService.getPostLikers(postId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getListUsers(userIdList);
        verify(likeRepository).findAllByPostId(postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikersTest() {
        like.setUserId(userId);
        when(likeRepository.findAllByPostId(commentId)).thenReturn(likesList);
        when(userClient.getListUsers(userIdList)).thenReturn(userDtoList);

        List<UserDto> resultList = likeService.getCommentLikers(commentId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getListUsers(userIdList);
        verify(likeRepository).findAllByPostId(commentId);
    }
}
