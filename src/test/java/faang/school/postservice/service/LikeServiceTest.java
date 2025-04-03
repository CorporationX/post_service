package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.like.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @InjectMocks
    private LikeService likeService;

    Like like;
    Like like1;
    Like like2;
    Comment comment;
    Post post;

    @BeforeEach
    void setUp() {
        like1 = new Like();
        like1.setUserId(2L);
        like1.setId(3);

        like2 = new Like();
        like2.setUserId(9L);

        post = new Post();
        post.setId(1L);
        post.setAuthorId(5L);
        post.setLikes(List.of(like1));

        comment = new Comment();
        comment.setId(4L);
        comment.setLikes(List.of(like1));
        comment.setPost(post);

        like = new Like();
        like.setId(1);
        like.setPost(post);
        like.setUserId(2L);
        like.setComment(comment);
    }


    @Test
    public void testLikePostSetLikeToIsSavedToDataBase() {
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(userServiceClient.getUser(like.getUserId())).thenReturn(new UserDto(like.getUserId(), "testUser", "t@mail.com"));
        post.setLikes(List.of(like2));
        likeService.setLikeToPost(like);

        Mockito.verify(likeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testUnsetLikeToPostSetLikeToIsRemovedFromDataBase() {
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(userServiceClient.getUser(like.getUserId())).thenReturn(new UserDto(like.getUserId(), "testUser", "t@mail.com"));
        likeService.unsetLikeToPost(like);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteById(like1.getId());
    }

    @Test
    public void testLikeCommentSetLikeToIsSavedToDataBase() {
        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(userServiceClient.getUser(2L)).thenReturn(new UserDto(2L, "testUser", "t@mail.com"));
        comment.setLikes(List.of(like2));
        likeService.setLikeToComment(like);

        Mockito.verify(likeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testUnsetLikeToCommentSetLikeToIsRemovedFromDataBase() {
        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(userServiceClient.getUser(2L)).thenReturn(new UserDto(2L, "testUser", "t@mail.com"));
        likeService.unsetLikeToComment(like);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteById(like.getId());
    }

    @Test
    public void testSetLikeToPostNotificationIssent() {
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(userServiceClient.getUser(like.getUserId())).thenReturn(new UserDto(like.getUserId(), "testUser", "t@mail.com"));
        post.setLikes(List.of(like2));
        likeService.setLikeToPost(like);

        Mockito.verify(likeEventPublisher, Mockito.times(1))
                .publish(new LikePostEvent(post.getId(), post.getAuthorId(), like.getUserId()), "like_topic");
    }
}
