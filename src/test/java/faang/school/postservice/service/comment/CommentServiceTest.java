package faang.school.postservice.service.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.publisher.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.UserClientValidation;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PostService postService;
    @Mock
    private UserClientValidation userClientValidation;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private PostMapper postMapper;

    CommentDto commentDto;
    Comment existingComment;
    PostDto postDto;
    long postId = 1L;

    @BeforeEach
    void init() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("Updated content");
        commentDto.setPostId(1L);

        existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setContent("Original content");

        postDto = new PostDto();
    }

    @Test
    @DisplayName("testAddCommentService")
    void testAddCommentService() {
        doNothing().when(userClientValidation).checkUser(anyLong());
        when(commentMapper.toEntity(any(CommentDto.class)))
                .thenReturn(existingComment);
        when(postService.getById(anyLong()))
                .thenReturn(postDto);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(existingComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(new CommentDto());

        commentService.addNewCommentInPost(commentDto);

        verify(commentMapper, times(1))
                .toEntity(any(CommentDto.class));
        verify(postService, times(1))
                .getById(anyLong());
        verify(commentRepository, times(1))
                .save(existingComment);
        verify(commentMapper, times(1))
                .toDto(any(Comment.class));
        verify(commentEventPublisher).publish(commentMapper.toEvent(existingComment));
    }

    @Test
    @DisplayName("testUpdateCommentService")
    void testUpdateCommentService() {
        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setContent("Updated content");

        doNothing().when(userClientValidation).checkUser(anyLong());
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(existingComment));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment))
                .thenReturn(commentDto);

        CommentDto result = commentService.updateExistingComment(commentDto);

        verify(commentRepository, times(1))
                .findById(anyLong());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
        verify(commentMapper, times(1))
                .toDto(updatedComment);

        assertEquals("Updated content", result.getContent());
    }

    @Test
    @DisplayName("testGetCommentsService")
    void testGetCommentsService() {
        Comment commentOne = Comment.builder().id(1L).content("Updated content").build();
        PostDto post1 = PostDto.builder().id(postId).build();
        when(postService.getById(postId)).thenReturn(post1);

        commentService.getCommentsForPost(postId);

        verify(postService, times(1)).getById(postId);
    }

    @Test
    @DisplayName("testDeleteCommentService")
    void testDeleteCommentService() {
        doNothing().when(commentRepository).deleteById(commentDto.getAuthorId());

        CommentDto resultTest = commentService.deleteExistingCommentInPost(commentDto);

        verify(commentRepository, times(1)).deleteById(existingComment.getId());
        assertEquals(commentDto, resultTest);
    }
}