package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.UserService;
import faang.school.postservice.service.cache.MultiGetCacheService;
import faang.school.postservice.service.cache.MultiSaveCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.service.comment.CommentServiceImpl;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private SingleCacheService<Long, UserDto> userCacheService;

    @Mock
    private EventPublisher<CommentDto> commentFeedEventPublisher;

    @Mock
    private MultiSaveCacheService<CommentDto> commentSaveCacheService;

    @Mock
    private MultiGetCacheService<Long, CommentDto> commentGetCacheService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private long authorId;
    private long commentId;
    private long postId;
    private Post post;
    private Comment mockComment;
    private UserDto userDto;
    private long count;
    private List<CommentDto> existingComments;

    private CommentDto commentDto;
    private UpdateCommentDto updateCommentDto;
    private List<UserDto> userDtos;

    @BeforeEach
    void setUp() {
        authorId = 1L;
        postId = 1L;
        commentId = 1L;
        count = 2L;

        List<CommentDto> existingComments = List.of(
                CommentDto.builder().id(1L).content("comment1").authorId(101L).build(),
                CommentDto.builder().id(2L).content("comment2").authorId(102L).build()
        );
        this.existingComments = new ArrayList<>(existingComments);

        List<UserDto> userDtos = List.of(
                UserDto.builder().id(101L).username("Author 1").build(),
                UserDto.builder().id(102L).username("Author 2").build()
        );
        this.userDtos = new ArrayList<>(userDtos);

        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Тестовый комментарий")
                .authorId(authorId)
                .postId(postId)
                .build();

         mockComment = Comment.builder()
                .id(commentId)
                .content("Старое содержимое")
                .authorId(authorId)
                .post(post)
                .build();

        updateCommentDto = UpdateCommentDto.builder()
                .content("Новый текстовый комментарий")
                .authorId(authorId)
                .build();

        userDto = UserDto.builder()
                .id(authorId)
                .build();

        lenient().when(commentMapper.toDto(mockComment)).thenReturn(commentDto);
    }

    @Test
    void deleteComment() {
        long commentId = 3L;
        when(commentRepository.existsById(commentId)).thenReturn(true);
        commentService.deleteComment(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void addComment_WhenOk() {
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentMapper.toComment(commentDto)).thenReturn(mockComment);
        when(commentRepository.save(mockComment)).thenReturn(mockComment);

        commentService.addComment(commentDto);

        verify(postRepository).findById(postId);
        verify(userServiceClient).getUser(authorId);
        verify(commentRepository).save(any());
        verify(userCacheService).save(authorId, userDto);
        verify(commentEventPublisher).publish(any());
        verify(commentMapper).toDto((Comment) any());
        verify(commentFeedEventPublisher).publish(commentDto);
    }

    @Test
    void addComment_WhenUserNotExists() {
        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(Exception.class, () -> commentService.addComment(commentDto));

        verify(userServiceClient).getUser(authorId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
        verify(commentFeedEventPublisher, never()).publish(commentDto);
    }

    @Test
    void addComment_WhenNotExistsPosts() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.addComment(commentDto));

        verify(postRepository).findById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
        verify(commentFeedEventPublisher, never()).publish(commentDto);
    }

    @Test
    void updateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        commentService.updateComment(commentId, updateCommentDto);

        verify(commentRepository, times(1))
                .updateContentAndDateById(eq(commentId), eq(updateCommentDto.content()), any(LocalDateTime.class));
    }

    @Test
    void getCommentsByPostId() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("Комментарий 1").post(post).build(),
                Comment.builder().id(2L).content("Комментарий 2").post(post).build()
        );
        List<CommentDto> commentDto = List.of(
                CommentDto.builder().id(1L).content("Комментарий 1").postId(postId).build(),
                CommentDto.builder().id(2L).content("Комментарий 2").postId(postId).build()
        );

        when(commentRepository.getByPostIdOrderByCreatedAtDesc(postId)).thenReturn(comments);
        when(commentMapper.toDto(comments)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(commentDto, result);
        verify(commentRepository).getByPostIdOrderByCreatedAtDesc(postId);
        verify(commentMapper).toDto(comments);
    }

    @Test
    public void testGetCommentsByPostId_ShouldReturnAllCommentsFromCache_WhenCacheHasEnoughComments() {
        when(commentGetCacheService.getAll(postId)).thenReturn(existingComments);

        List<CommentDto> result = commentService.getCommentsByPostId(postId, count);

        assertEquals(existingComments, result);
        verify(commentRepository, never()).getByPostIdWithLimit(anyLong(), anyLong());
        verify(commentSaveCacheService, never()).saveAll(anyList());
    }

    @Test
    public void testGetCommentsByPostId_ShouldFetchMissingCommentsFromRepository_WhenCacheDoesNotHaveEnoughComments() {
        List<Comment> missingComments = List.of(
                Comment.builder().id(3L).content("Комментарий 1").post(post).build(),
                Comment.builder().id(4L).content("Комментарий 2").post(post).build()
        );
        List<CommentDto> missingCommentDtos = List.of(
                CommentDto.builder().id(4L).content("Комментарий 1").postId(postId).build(),
                CommentDto.builder().id(5L).content("Комментарий 2").postId(postId).build()
        );

        when(commentGetCacheService.getAll(postId)).thenReturn(existingComments);
        when(commentRepository.getByPostIdWithLimit(postId, count)).thenReturn(missingComments);
        when(commentMapper.toDto(missingComments)).thenReturn(missingCommentDtos);

        List<CommentDto> result = commentService.getCommentsByPostId(postId, 4);

        assertEquals(4, result.size());
        verify(commentRepository).getByPostIdWithLimit(postId, count);
        verify(commentSaveCacheService).saveAll(missingCommentDtos);
    }

    @Test
    public void testAssignAuthorsToComments_ShouldAssignAuthors_WhenAuthorsAreFound() {
        when(userService.getUsersFromCacheOrService(anyList())).thenReturn(userDtos);

        commentService.assignAuthorsToComments(existingComments);

        assertEquals("Author 1", existingComments.get(0).getAuthor().getUsername());
        assertEquals("Author 2", existingComments.get(1).getAuthor().getUsername());
    }

    @Test
    public void testAssignAuthorsToComments_ShouldNotAssignAuthor_WhenNoAuthorFound() {
        when(userService.getUsersFromCacheOrService(anyList())).thenReturn(userDtos);
        userDtos.remove(1);

        commentService.assignAuthorsToComments(existingComments);

        assertEquals("Author 1", existingComments.get(0).getAuthor().getUsername());
        assertNull(existingComments.get(1).getAuthor());
    }

    @Test
    public void testAssignAuthorsToComments_ShouldHandleEmptyList() {
        List<UserDto> userDtos = List.of(
                UserDto.builder().id(101L).username("Author 1").build(),
                UserDto.builder().id(102L).username("Author 2").build()
        );
        when(userService.getUsersFromCacheOrService(anyList())).thenReturn(userDtos);
        List<CommentDto> emptyCommentDtos = new ArrayList<>();

        commentService.assignAuthorsToComments(emptyCommentDtos);

        assertTrue(emptyCommentDtos.isEmpty());
        verify(userService).getUsersFromCacheOrService(anyList());
    }
}
