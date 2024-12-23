package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.like.LikeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeRepository likeRepository;
    @Spy
    private LikeMapperImpl likeMapper;
    @Mock
    private CommentService commentService;
    @Mock
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Captor
    private ArgumentCaptor<Like> likeCaptor;

    @Test
    public void testLikeCommentPassed() {
        Long commentId = 1L;
        LikeDto likeDto = prepareData(true);
        Comment comment = Comment.builder()
                .id(1L)
                .likes(new ArrayList<>())
                .build();
        when(commentService.findEntityById(commentId)).thenReturn(comment);
        LikeDto actualDto = likeService.likeComment(commentId, likeDto);
        verify(likeRepository).save(likeCaptor.capture());
        Like like = likeCaptor.getValue();

        verify(likeRepository, times(1)).save(like);
        assertEquals(likeDto, actualDto);
        assertEquals(1, comment.getLikes().size());
    }

    @Test
    public void testLikePostPassed() {
        Long postId = 1L;
        LikeDto likeDto = prepareData(false);
        Post post = Post.builder()
                .id(1L)
                .likes(new ArrayList<>())
                .build();
        when(postService.findEntityById(postId)).thenReturn(post);
        LikeDto actualDto = likeService.likePost(postId, likeDto);
        verify(likeRepository).save(likeCaptor.capture());
        Like like = likeCaptor.getValue();

        verify(likeRepository, times(1)).save(like);
        assertEquals(likeDto, actualDto);
    }

    @Test
    public void testRemoveLikeUnderCommentPassed() {
        Long commentId = 1L;
        LikeDto likeDto = prepareData(true);
        Comment comment = Comment.builder()
                .id(1L)
                .likes(new ArrayList<>())
                .build();
        when(commentService.findEntityById(commentId)).thenReturn(comment);
        Like like = likeMapper.toEntity(likeDto);

        LikeDto actualDto = likeService.removeLikeUnderComment(commentId, likeDto);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, like.getUserId());
        verify(commentRepository, times(1)).save(comment);
        assertEquals(likeDto, actualDto);
    }

    @Test
    public void testRemoveLikeUnderPostPassed() {
        Long postId = 1L;
        LikeDto likeDto = prepareData(false);
        Post post = Post.builder()
                .id(1L)
                .likes(new ArrayList<>())
                .build();
        when(postService.findEntityById(postId)).thenReturn(post);
        Like like = likeMapper.toEntity(likeDto);

        LikeDto actualDto = likeService.removeLikeUnderPost(postId, likeDto);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, like.getUserId());
        verify(postRepository, times(1)).save(post);
        assertEquals(likeDto, actualDto);
    }

    private LikeDto prepareData(boolean isComment) {
        if (isComment) {
            return LikeDto.builder()
                    .id(0L)
                    .userId(2L)
                    .commentId(1L)
                    .build();
        } else {
            return LikeDto.builder()
                    .id(0L)
                    .userId(2L)
                    .postId(1L)
                    .build();
        }
    }
}
