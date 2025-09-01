package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
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

import static faang.school.postservice.service.like.LikeTestData.createData;
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

    LikeTestData data = createData();

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikersTest() {
        data.getLike().setUserId(LikeTestData.userId);
        when(likeRepository.findAllByPostId(LikeTestData.postId)).thenReturn(data.getLikesList());
        when(userClient.getUsersByIds(data.getUserIdList())).thenReturn(data.getUserDtoList());

        List<UserDto> resultList = likeService.getPostLikers(LikeTestData.postId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(data.getUserDtoList());
        verify(userClient).getUsersByIds(data.getUserIdList());
        verify(likeRepository).findAllByPostId(LikeTestData.postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikersTest() {
        data.getLike().setUserId(LikeTestData.userId);
        when(likeRepository.findAllByCommentId(LikeTestData.commentId)).thenReturn(data.getLikesList());
        when(userClient.getUsersByIds(data.getUserIdList())).thenReturn(data.getUserDtoList());

        List<UserDto> resultList = likeService.getCommentLikers(LikeTestData.commentId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(data.getUserDtoList());
        verify(userClient).getUsersByIds(data.getUserIdList());
        verify(likeRepository).findAllByCommentId(LikeTestData.commentId);
    }
}
