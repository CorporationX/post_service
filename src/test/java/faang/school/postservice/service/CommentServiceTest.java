package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentValidator commentValidator;

    @Mock
    CommentRepository commentRepository;

    @Mock
    private PostService postService;

    Long postId;
    CommentDto commentDto;

    @BeforeEach
    void setUp() {

        postId = 1L;

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Test comment")
                .authorId(1L)
                .build();

    }

    @Test
    void testCreateComment_ValidData_ReturnsCreatedCommentDto(){

        Post post = Post.builder()
                .id(postId)
                .content("Test post content")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .content("Test comment")
                .build();


        when(postService.getPostById(postId)).thenReturn(post);

        when(commentMapper.toEntity(any(CommentDto.class))).thenReturn(comment);

        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto createdCommentDto = commentService.createComment(postId, commentDto);

        verify(commentValidator).validateUserBeforeCreate(commentDto);


        verify(postService).getPostById(postId);
        verify(commentMapper).toEntity(commentDto);
        verify(commentRepository).save(comment);

        assertEquals(commentDto.getId(), createdCommentDto.getId());
        assertEquals(commentDto.getContent(), createdCommentDto.getContent());
    }
}