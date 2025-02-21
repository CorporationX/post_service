package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.comment.CommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentServiceValidator validator;

    @Mock
    private PostService postService;

    @Mock
    private CommentProducer commentProducer;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void createCommentSuccess() {
        CommentDto commentDto = getCommentDto();
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(new Post());

        Mockito.lenient().when(postService.findPostById(Mockito.anyLong())).thenReturn(Optional.of(new Post()));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.doNothing().when(commentProducer).sendMessage(Mockito.any(CommentEvent.class));

        commentService.createComment(commentDto);

        Mockito.verify(validator).validateCreateComment(getCommentDto());
        Mockito.verify(commentRepository).save(comment);
    }

    @Test
    void createCommentFailure_PostNotFound() {
        Mockito.lenient().when(postService.findPostById(getCommentDto().getPostId())).thenReturn(Optional.empty());
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(commentProducer, Mockito.never()).sendMessage(Mockito.any());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commentService.createComment(getCommentDto()));
        assertEquals("Post with id %s not found".formatted(getCommentDto().getPostId()), exception.getMessage());
    }

    @Test
    void updateCommentSuccess() {
        CommentDto commentDto = getCommentDto();
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(new Post());

        Mockito.lenient().when(commentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(comment));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        commentService.updateComment(commentDto);

        Mockito.verify(commentRepository).save(comment);
    }

    @Test
    void updateCommentFailure_CommentNotFound() {
        Mockito.lenient().when(commentRepository.findById(getCommentDto().getId())).thenReturn(Optional.empty());
        Mockito.verify(commentRepository, Mockito.never()).save(Mockito.any());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(getCommentDto()));
        assertEquals("Comment with id %s not found".formatted(getCommentDto().getId()), exception.getMessage());
    }

    @Test
    void getCommentsByPostIdSuccess() {
        List<Comment> comments = getComments().stream().sorted(Comparator.comparing(Comment::getCreatedAt).reversed()).toList();

        Mockito.lenient().when(commentRepository.findAllByPostId(Mockito.anyLong())).thenReturn(comments);

        assertEquals(getCommentDtoList().stream().sorted(Comparator.comparing(CommentDto::getCreatedAt).reversed()).peek(comment -> comment.setLikeIds(List.of())).toList(), commentService.getCommentsByPostId(getCommentDto().getPostId()));
    }

    @Test
    void deleteCommentSuccess() {
        commentService.deleteComment(getCommentDto().getId());
        Mockito.verify(validator).validateCommentId(getCommentDto().getId());
        Mockito.verify(commentRepository).deleteById(getCommentDto().getId());
        Mockito.verify(commentRepository).deleteById(getCommentDto().getId());

    }

    private List<Comment> getComments() {
        return commentMapper.toEntity(getCommentDtoList()).stream()
                .peek(comment -> {
                    comment.setId(1L);
                    comment.setPost(Post.builder().id(1L).build());
                })
                .toList();
    }

    private List<CommentDto> getCommentDtoList() {
        return new ArrayList<>(List.of(getCommentDto(), getCommentDto(), getCommentDto(), getCommentDto(), getCommentDto()));
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .content("Content 1")
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }
}