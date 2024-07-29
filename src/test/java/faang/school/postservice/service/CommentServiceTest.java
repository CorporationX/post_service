package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;


    private long commentId;
    private long postId;
    private long userId;
    private Comment comment;
    private CommentDto commentDto;
    private CommentDto updatedCommentDto;
    private Post post;

    @BeforeEach
    void init() {
        commentId = 1L;
        userId = 2L;
        String content = "content";
        post = Post.builder().id(postId).build();
        comment = Comment.builder()
                .id(commentId)
                .authorId(2L)
                .content(content)
                .post(post)
                .build();
        commentDto = CommentDto.builder()
                .id(commentId)
                .authorId(2L)
                .postId(postId)
                .build();
        updatedCommentDto = CommentDto.builder()
                .id(commentId)
                .authorId(2L)
                .content("UpdatedContent")
                .build();

    }

    @Test
    void deleteCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void findAllByPostIdTest() {
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));
        when(commentMapper.entityToDto(comment)).thenReturn(commentDto);
        commentService.findAllByPostId(postId);
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void createCommentTest() {
        when(commentMapper.dtoToEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(postRepository.findById(commentDto.getPostId())).thenReturn(Optional.of(post));
        when(commentMapper.entityToDto(comment)).thenReturn(commentDto);
        CommentDto result = commentService.createComment(commentDto);
        verify(commentRepository).save(comment);
        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    void updateCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.entityToDto(comment)).thenReturn(updatedCommentDto);
        CommentDto result = commentService.updateComment(updatedCommentDto);
        verify(commentRepository).findById(commentId);
        assertNotNull(updatedCommentDto);
        assertEquals(result.getContent(), updatedCommentDto.getContent());
    }
}
