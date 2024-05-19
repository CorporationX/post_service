package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    private Long id;
    private LikeDto likeDto;
    private Post post;
    private Like like1;
    private Like like2;
    private Like like3;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        id = 1L;
        likeDto = new LikeDto();

        post = Post.builder().likes(
                List.of(
                        Like.builder().userId(1L).build(),
                        Like.builder().userId(2L).build(),
                        Like.builder().userId(3L).build())
        ).build();

        comment = Comment.builder().likes(
                List.of(
                        Like.builder().userId(1L).build(),
                        Like.builder().userId(2L).build(),
                        Like.builder().userId(3L).build())
        ).build();

        like1 = Like.builder()
                .userId(4L)
                .post(Post.builder().id(1L).build())
                .build();

        like2 = Like.builder()
                .userId(2L)
                .build();

        like3 = Like.builder()
                .userId(4L)
                .comment(Comment.builder().id(1L).build())
                .build();
    }

    @Test
    public void testAddLikePost_notPostInBd() {
        assertThrows(DataLikeValidation.class, () -> likeService.addLikePost(1L, likeDto));
    }

    @Test
    public void testAddLikePost_standLikeOnPost() {
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(likeDto)).thenReturn(like2);

        assertThrows(DataLikeValidation.class, () -> likeService.addLikePost(id, likeDto));
    }

    @Test
    public void testAddLikePost_standLikeOnComment() {
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        when(likeMapper.toEntity(likeDto)).thenReturn(like3);

        assertThrows(DataLikeValidation.class, () -> likeService.addLikePost(id, likeDto));
    }

    @Test
    public void testAddLikeComment_notCommentInBd() {
        assertThrows(DataLikeValidation.class, () -> likeService.addLikeComment(1L, likeDto));
    }

    @Test
    public void testAddLikeComment_standLikeOnComment() {
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(likeMapper.toEntity(likeDto)).thenReturn(like2);

        assertThrows(DataLikeValidation.class, () -> likeService.addLikeComment(id, likeDto));
    }

    @Test
    public void testAddLikeComment_standLikeOnPost() {
        when(commentRepository.findById(id)).thenReturn(Optional.of(comment));
        when(likeMapper.toEntity(likeDto)).thenReturn(like1);

        assertThrows(DataLikeValidation.class, () -> likeService.addLikeComment(id, likeDto));
    }

    @Test
    public void testDeleteLikePost_notPostInBd() {
        assertThrows(DataLikeValidation.class, () -> likeService.deleteLikePost(1L, likeDto));
    }

    @Test
    public void testDeleteLikeComment_notCommentInBd() {
        assertThrows(DataLikeValidation.class, () -> likeService.deleteLikeComment(1L, likeDto));
    }
}