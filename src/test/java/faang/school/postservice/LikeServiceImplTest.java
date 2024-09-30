package faang.school.postservice;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    public PostRepository postRepository;
    @Mock
    public CommentRepository commentRepository;
    @Spy
    public LikeMapperImpl likeMapperImpl;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    private List<Like> likes1;
    private List<Like> likes2;
    private Post post;
    private Comment comment;
    private Like like1;
    private LikeDto likeDto;
    private long postId;
    private long commentId;

    @BeforeEach
    void setUp() {
        post = new Post();
        like1 = new Like();
        comment = new Comment();
        likeDto = new LikeDto(1L, 2L);
        postId = 3L;
        commentId = 3L;
        likes1 = new ArrayList<>();
        likes2 = new ArrayList<>();
    }

    @Test
    void addLikeToPost() {
        like1.setId(1L);
        like1.setUserId(2L);
        post.setId(postId);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        like1.setPost(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        likeService.addLikeToPost(likeDto, postId);

        verify(likeRepository, times(1)).save(like1);
    }

    @Test
    void deleteLikeFromPostThatNotLiked() {
        like1.setId(1L);
        like1.setUserId(2L);
        post.setLikes(likes1);
        post.setId(postId);
        like1.setPost(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> likeService.deleteLikeFromPost(likeDto, postId));
    }

    @Test
    void deleteLikeFromPostSuccessful() {
        likes1.add(like1);
        like1.setId(1L);
        like1.setUserId(2L);
        post.setLikes(likes1);
        post.setId(postId);
        like1.setPost(post);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeMapperImpl.toLike(likeDto)).thenReturn(like1);

        assertDoesNotThrow(() -> likeService.deleteLikeFromPost(likeDto, postId));
    }

    @Test
    void addLikeToComment() {
        likes1.add(like1);
        like1.setId(1L);
        like1.setUserId(2L);
        post.setId(commentId);
        post.setLikes(new ArrayList<>());
        post.setComments(new ArrayList<>());
        comment.setPost(post);
        post.setComments(List.of(comment));
        comment.setLikes(new ArrayList<>());
        comment.setId(commentId);
        like1.setComment(comment);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        likeService.addLikeToComment(likeDto, commentId);

        verify(likeRepository, times(1)).save(like1);
    }

    @Test
    void deleteLikeFromNotLikedComment() {
        likes1.add(like1);
        likes2.add(new Like());
        comment.setPost(post);
        comment.setId(commentId);
        comment.setLikes(likes2);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(DataValidationException.class, () -> likeService.deleteLikeFromComment(likeDto, commentId));
    }

    @Test
    void deleteLikeFromComment() {
        likes1.add(like1);
        likes2.add(new Like());
        comment.setPost(post);
        comment.setId(commentId);
        comment.setLikes(new ArrayList<>(likes1));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeMapperImpl.toLike(likeDto)).thenReturn(like1);

        assertDoesNotThrow(() -> likeService.deleteLikeFromComment(likeDto, commentId));
    }
}
