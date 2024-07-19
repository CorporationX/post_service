package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderator.comment.logic.CommentModerator;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import faang.school.postservice.threadpool.ThreadPoolForCommentModerator;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Spy
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private ThreadPoolForCommentModerator threadPoolForCommentModerator;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private CommentModerator commentModerator;

    private CreateCommentDto createCommentDto;
    private ChangeCommentDto changeCommentDto;
    private CommentEventDto commentEventDto;
    private Comment comment;
    private List<Comment> commentList;
    private Long id;
    private String patternByInfAuthor = "InfAuthor";
    private UserDto authorCommentDto;

    @BeforeEach
    public void setUp() {
        Post post = Post.builder().id(1L).build();
        createCommentDto = CreateCommentDto.builder().id(1L).content("content").authorId(1L).postId(1L).build();
        authorCommentDto = UserDto.builder().id(1L).build();
        changeCommentDto = ChangeCommentDto.builder().id(1L).content("content").build();
        commentEventDto = CommentEventDto.builder().commentId(1L).createdAt(null).postId(1L).authorId(1L).build();
        comment = Comment.builder().id(1L).content("content").authorId(1L).post(post).build();
        commentList = new ArrayList<>(List.of(comment, comment, comment, comment));
        id = 1L;
        commentService.setPatternByInfAuthor("InfAuthor");
    }

    @Test
    public void testCorrectWorkCreateComment() {
        when(commentMapper.toEntity(createCommentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toEventDto(comment)).thenReturn(commentEventDto);
        doNothing().when(commentEventPublisher).sendEvent(commentEventDto);
        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);
        when(userServiceClient.getUser(createCommentDto.getAuthorId())).thenReturn(authorCommentDto);

        CreateCommentDto result = commentService.createComment(createCommentDto);

        assertEquals(createCommentDto, result);
        verify(commentMapper).toEntity(createCommentDto);
        verify(commentMapper).toEventDto(comment);
        verify(commentEventPublisher).sendEvent(commentEventDto);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(comment);
        verify(redisCacheService, times(1)).saveToCache(patternByInfAuthor, authorCommentDto.getId(), authorCommentDto);
    }

    @Test
    public void testCorrectWorkChangeComment() {
        when(commentRepository.findById(createCommentDto.getId())).thenReturn(Optional.ofNullable(comment));

        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);

        CreateCommentDto result = commentService.changeComment(changeCommentDto);

        assertEquals(createCommentDto, result);
        verify(commentRepository).findById(createCommentDto.getId());
        verify(commentMapper).toDto(comment);
    }

    @Test
    public void testChangeCommentWithValidationException() {
        when(commentRepository.findById(createCommentDto.getId())).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> commentService.changeComment(changeCommentDto));
    }


    @Test
    public void testCorrectWorkGetAllCommentsOnPostId() {
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(id)).thenReturn(Collections.singletonList(comment));
        doNothing().when(commentValidator).getAllCommentsOnPostIdService(id);
        when(commentMapper.toDto(comment)).thenReturn(createCommentDto);

        List<CreateCommentDto> result = commentService.getAllCommentsOnPostId(id);

        verify(commentValidator, times(1)).getAllCommentsOnPostIdService(createCommentDto.getPostId());
        verify(commentRepository, times(1)).findAllByPostIdOrderByCreatedAtDesc(createCommentDto.getPostId());
        verify(commentMapper, times(1)).toDto(comment);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(id, result.get(0).getId());
        assertEquals(createCommentDto.getContent(), result.get(0).getContent());
    }

    @Test
    public void testGetAllCommentsOnPostIdWithValidationException() {
        doThrow(DataValidationException.class).when(commentValidator).getAllCommentsOnPostIdService(id);
        assertThrows(DataValidationException.class, () -> commentService.getAllCommentsOnPostId(id));
    }

    @Test
    public void testCorrectWorkDeleteComment() {
        doNothing().when(commentRepository).deleteById(id);
        commentService.deleteComment(id);
        verify(commentRepository, times(1)).deleteById(id);
    }

    @Test
    public void testCorrectWorkModerateComment() {
        commentService.setPullNumbers(4);
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        when(commentRepository.findUnVerifiedComments()).thenReturn(commentList);
        when(threadPoolForCommentModerator.taskExecutor()).thenReturn(executorService);

        commentService.moderateComment();

        verify(commentModerator, times(4)).moderateComment(anyList());
        executorService.shutdownNow();
    }
}
