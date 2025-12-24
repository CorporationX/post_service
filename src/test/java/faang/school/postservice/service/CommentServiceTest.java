package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.CommentEventProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.AuthorCacheRepository;
import faang.school.postservice.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AuthorCacheRepository authorCacheRepository;
    @Mock
    private CommentEventProducer commentEventProducer;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentService commentService;

    private CommentCreateDto createDto;
    private CommentUpdateDto updateDto;
    private Post post;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        createDto = new CommentCreateDto("Nice post!", 1L, null, null);
        updateDto = new CommentUpdateDto("Updated content", "largeKey", "smallKey");
        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(10L);
        comment.setAuthorId(1L);
        comment.setPost(post);
        comment.setContent("Old content");

        userDto = new UserDto(1L, "John", "Doe", Boolean.TRUE);
    }

    @Test
    void create_success() {
        post.setAuthorId(1L);
        when(postRepository.getByIdOrThrow(1L)).thenReturn(post);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Comment result = commentService.create(createDto, 1L);

        assertNotNull(result);
        assertEquals("Nice post!", result.getContent());
        assertEquals(post, result.getPost());
        assertEquals(1L, result.getAuthorId());

        verify(commentRepository).save(any(Comment.class));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    void update_success() {
        when(commentRepository.findByIdOrThrow(10L))
                .thenReturn(comment);
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Comment updated = commentService.update(10L, updateDto, 1L);

        assertEquals("Updated content", updated.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void update_throws_whenUserNotOwner() {
        comment.setAuthorId(2L);
        when(commentRepository.findByIdOrThrow(10L)).thenReturn(comment);

        assertThrows(ValidationException.class,
                () -> commentService.update(10L, updateDto, 1L));
    }

    @Test
    void update_throws_whenNotFound() {
        when(commentRepository.findByIdOrThrow(10L))
                .thenThrow(new ResourceNotFoundException("Comment not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.update(10L, updateDto, 1L));
    }

    @Test
    void findAllByPostId_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> page = new PageImpl<>(List.of(comment));

        when(postRepository.getByIdOrThrow(1L)).thenReturn(post);
        when(commentRepository.findAllByPostId(1L, pageable)).thenReturn(page);

        PageResponse<CommentDto> result = commentService.findAllByPostId(1L, pageable);

        assertEquals(1, result.content().size());
        verify(postRepository).getByIdOrThrow(1L);
    }

    @Test
    void delete_success() {
        when(commentRepository.findByIdOrThrow(10L)).thenReturn(comment);
        comment.setAuthorId(1L);

        commentService.delete(10L, 1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void getById_success() {
        when(commentRepository.findByIdOrThrow(10L)).thenReturn(comment);

        Comment found = commentService.getById(10L);

        assertEquals(comment, found);
        verify(commentRepository).findByIdOrThrow(10L);
    }
}