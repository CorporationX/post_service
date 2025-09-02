package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
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

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikersTest() {
        List<Like> likeList = LikeTestData.getLikesList();
        long postId = LikeTestData.postId;
        long userId = LikeTestData.userId;
        List<UserDto> userDtoList = LikeTestData.getUserDtoList();
        List<Long> userIdList = LikeTestData.getUserIdList();

        when(likeRepository.findAllByPostId(postId)).thenReturn(likeList);
        when(userClient.getUsersByIds(List.of(userId))).thenReturn(userDtoList);

        List<UserDto> resultList = likeService.getPostLikers(postId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getUsersByIds(userIdList);
        verify(likeRepository).findAllByPostId(postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikersTest() {
        List<Like> likeList = LikeTestData.getLikesList();
        long commentId = LikeTestData.commentId;
        long userId = LikeTestData.userId;
        List<UserDto> userDtoList = LikeTestData.getUserDtoList();
        List<Long> userIdList = LikeTestData.getUserIdList();

        when(likeRepository.findAllByCommentId(commentId)).thenReturn(likeList);
        when(userClient.getUsersByIds(List.of(userId))).thenReturn(userDtoList);

        List<UserDto> resultList = likeService.getCommentLikers(commentId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getUsersByIds(userIdList);
        verify(likeRepository).findAllByCommentId(commentId);
    }
}
