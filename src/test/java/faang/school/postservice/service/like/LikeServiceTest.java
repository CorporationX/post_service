package faang.school.postservice.service.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private static final long USER_ID = 1L;
    private static final long POST_ID = 1L;
    private static final long COMMENT_ID = 1L;

    @Test
    @DisplayName("Should like on post saved in repository")
    public void saveLikeOnPost() {
        Post post = createPost();
        Like like = createLikeOnPost(post);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        likeService.likePost(USER_ID, POST_ID);
        verify(likeRepository).save(like);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Should add Like in Post")
    public void addLikeOnPost() {
        Post post = createPost();
        List<Like> likes = List.of(createLikeOnPost(post));

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        likeService.likePost(USER_ID, POST_ID);
        assertEquals(post.getLikes(), likes);
    }

    @Test
    @DisplayName("Should like on comment saved in repository")
    public void saveLikeOnComment() {
        Comment comment = createComment();
        Like like = createLikeOnComment(comment);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        likeService.likeComment(USER_ID, COMMENT_ID);
        verify(likeRepository).save(like);
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Should add Like in Comment")
    public void addLikeOnComment() {
        Comment comment = createComment();
        List<Like> likes = List.of(createLikeOnComment(comment));

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        likeService.likeComment(USER_ID, COMMENT_ID);
        assertEquals(comment.getLikes(), likes);
    }

    @Test
    @DisplayName("Should delete like from post")
    public  void deleteLikeFromPost() {
        Post post = createPost();
        Like like = createLikeOnPost(post);
        post.setLikes(new ArrayList<>(List.of(like)));

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        likeService.deleteLikeFromPost(USER_ID, POST_ID);
        assertEquals(post.getLikes(), List.of());
        verify(postRepository).save(post);
        verify(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("Should delete like from comment")
    public  void deleteLikeFromComment() {
        Comment comment = createComment();
        Like like = createLikeOnComment(comment);
        comment.setLikes(new ArrayList<>(List.of(like)));

        when(commentRepository.findById(POST_ID)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));

        likeService.deleteLikeFromComment(USER_ID, COMMENT_ID);
        assertEquals(comment.getLikes(), List.of());
        verify(commentRepository).save(comment);
        verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    private Like createLikeOnComment(Comment comment) {
        return Like.builder()
                .userId(USER_ID)
                .comment(comment)
                .build();
    }

    private Like createLikeOnPost(Post post) {
        return Like.builder()
                .userId(USER_ID)
                .post(post)
                .build();
    }

    private Post createPost() {
        return Post.builder()
                .id(POST_ID)
                .authorId(USER_ID)
                .likes(new ArrayList<>())
                .build();
    }

    private Comment createComment() {
        return Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .likes(new ArrayList<>())
                .build();
    }
}
