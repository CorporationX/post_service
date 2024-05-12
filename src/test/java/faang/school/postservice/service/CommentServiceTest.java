package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.CommentPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validation.CommentValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {


    @Mock
    PostService postService;

    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    CommentValidation commentValidation;
    @Mock
    CommentPublisher commentPublisher;
    @InjectMocks
    private CommentService commentService;
    CommentDto firstCommentDto;
    CommentDto secondCommentDto;

    Comment firstComment;
    Comment secondComment;
    Post post;


    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .build();
        firstCommentDto = CommentDto.builder()
                .id(null)
                .content("first content")
                .authorId(1L)
                .likesIds(null)
                .postId(post.getId())
                .build();
        firstComment = Comment.builder()
                .id(1L)
                .content("first content")
                .authorId(1L)
                .likes(null)
                .post(post)
                .build();
        secondCommentDto = CommentDto.builder()
                .id(2L)
                .content("second content")
                .authorId(1L)
                .likesIds(null)
                .postId(post.getId())
                .build();
        secondComment = Comment.builder()
                .id(2L)
                .content("edited second content")
                .authorId(1L)
                .likes(null)
                .post(post)
                .build();
        post.setComments(List.of(firstComment, secondComment));

    }

    @Test
    public void testCreation() {
        when(commentMapper.toEntity(firstCommentDto)).thenReturn(firstComment);
        when(commentRepository.save(firstComment)).thenReturn(firstComment);
        when(commentMapper.toDto(firstComment)).thenReturn(firstCommentDto);
        when(postService.getPost(firstCommentDto.getPostId())).thenReturn(post);

        CommentDto result = commentService.create(firstCommentDto, firstCommentDto.getAuthorId());

        verify(commentValidation, times(1)).authorExistenceValidation(firstCommentDto.getAuthorId());
        verify(postService, times(1)).getPost(firstCommentDto.getPostId());

        verify(commentPublisher, times(1)).publish(CommentEvent.builder()
                .commentId(firstComment.getId())
                .authorId(firstComment.getAuthorId())
                .content(firstComment.getContent())
                .postId(firstCommentDto.getPostId())
                .build());
        assertEquals(result, firstCommentDto);
    }


    @Test
    public void testUpdating() {
        when(commentRepository.findById(secondComment.getId())).thenReturn(Optional.of(secondComment));
        when(commentRepository.save(secondComment)).thenReturn(secondComment);
        when(commentMapper.toDto(secondComment)).thenReturn(secondCommentDto);

        CommentDto result = commentService.update(secondCommentDto, secondCommentDto.getAuthorId());

        verify(commentValidation, times(1)).validateCommentExistence(secondComment.getId());
        verify(commentValidation, times(1)).authorExistenceValidation(secondCommentDto.getAuthorId());
        assertEquals(result, secondCommentDto);
    }

    @Test
    public void testGetPostComments() {
        when(postService.getPost(post.getId())).thenReturn(post);

        commentService.getPostComments(post.getId());

        verify(postService, times(1)).getPost(post.getId());
        verify(commentMapper, times(1)).toDto(post.getComments());

    }

    @Test
    public void testDeletion() {
        commentService.delete(secondCommentDto, secondCommentDto.getAuthorId());

        verify(commentValidation, times(1)).authorExistenceValidation(secondCommentDto.getAuthorId());
        verify(commentValidation, times(1)).validateCommentExistence(secondComment.getId());
        verify(commentRepository, times(1)).deleteById(secondComment.getId());
    }
}

