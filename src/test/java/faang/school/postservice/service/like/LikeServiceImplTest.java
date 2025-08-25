package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тест для LikeServiceImpl")
public class LikeServiceImplTest {

    @Mock
    private PostRepository postRepository;
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

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк посту")
    public void getPostLikers() {
        like.setUserId(userId);
        post.setLikes(likesList);
        Optional<Post> optionalPost = Optional.of(post);
        when(postRepository.findById(postId)).thenReturn(optionalPost);
        when(userClient.getUser(like.getUserId())).thenReturn(userDto);

        List<UserDto> resultList = likeService.getPostLikers(postId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getUser(userId);
        verify(postRepository).findById(postId);
    }

    @Test
    @DisplayName("тест успешного получения списка пользователей поставивших лайк коментарию")
    public void getCommentLikers() {
        like.setUserId(userId);
        comment.setLikes(likesList);
        Optional<Comment> optionalComment = Optional.of(comment);
        when(commentRepository.findById(commentId)).thenReturn(optionalComment);
        when(userClient.getUser(like.getUserId())).thenReturn(userDto);

        List<UserDto> resultList = likeService.getCommentLikers(commentId);

        assertThat(resultList).usingRecursiveAssertion().isEqualTo(userDtoList);
        verify(userClient).getUser(userId);
        verify(commentRepository).findById(commentId);
    }
}
