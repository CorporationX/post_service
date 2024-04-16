package faang.school.postservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.hashService.AuthorHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;
    @Mock
    private AuthorHashService authorHashService;
    private CommentDto commentDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
                .authorId(1L)
                .content("content")
                .build();
        userDto = new UserDto(1L, "Username", "email");
    }

    @Test
    public void testCreateAuthorExistsInvalid() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.create(commentDto, 1L));
        assertEquals(illegalArgumentException.getMessage(), "There are no author with id " + commentDto.getAuthorId());
    }

    @Test
    public void testCreateVerifyToEntity() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        commentService.create(commentDto, 1L);
        verify(commentMapper, times(1)).toEntity(commentDto);
    }

    @Test
    public void testCreateVerifySave() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        commentService.create(commentDto, 1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testCreateVerifyToDto() {
        when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(userDto);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        commentService.create(commentDto, 1L);
        verify(commentMapper, times(1))
                .toDto(commentRepository.save(commentMapper.toEntity(commentDto)));
    }

    @Test
    public void testUpdatePostExistsIsInvalid() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.update(commentDto, 1L));
        assertEquals(illegalArgumentException.getMessage(), "There are no post with id " + 1L);
    }

    @Test
    public void testUpdateCommentIdIsInvalid() {
        commentDto.setId(null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.update(commentDto, 1L));
        assertEquals(illegalArgumentException.getMessage(), "There are no comment with id " + commentDto.getId());
    }

    @Test
    public void testUpdateAuthorIdIsInvalid() {
        Comment comment = new Comment();
        comment.setAuthorId(3L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));


        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.update(commentDto, 1L));
        assertEquals(illegalArgumentException.getMessage(), "Only author can make changes! ID: " + commentDto.getAuthorId() + " is not valid");
    }

    @Test
    public void testUpdatePostIdIsInvalid() {
        Comment comment = preparationForUpdate();
        comment.getPost().setId(5L);

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.update(commentDto, 1L));
        assertEquals(illegalArgumentException.getMessage(), "Post's ID is not invalid");
    }

    @Test
    public void testUpdateContentChanged() {
        Comment comment = preparationForUpdate();
        commentService.update(commentDto, 1L);
        assertEquals(commentDto.getContent(), comment.getContent());
    }

    @Test
    public void testUpdateIsSaved() {
        Comment comment = preparationForUpdate();
        commentService.update(commentDto, 1L);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    public void testUpdateToDto() {
        Comment comment = preparationForUpdate();
        commentService.update(commentDto, 1L);
        verify(commentMapper, times(1))
                .toDto(commentRepository.save(comment));

    }

    public Comment preparationForUpdate() {
        Comment comment = new Comment();
        comment.setAuthorId(1L);
        comment.setContent("great weather");
        Post post = new Post();
        post.setId(1L);
        comment.setPost(post);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        when(commentRepository.findById(commentDto.getId())).thenReturn(Optional.of(comment));
        return comment;
    }

    @Test
    public void testDeleteVerifyDelete() {
        Comment comment = preparationForUpdate();
        commentService.delete(commentDto, 1L);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testGetAllCommentsByPostIdIsSorted() {
        List<Comment> comments = preparationForGetAllCommentsByPostId();
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos.add(0, commentMapper.toDto(comments.get(2)));
        commentDtos.add(1, commentMapper.toDto(comments.get(0)));
        commentDtos.add(2, commentMapper.toDto(comments.get(1)));

        List<CommentDto> commentDtosActual = commentService.getAllCommentsByPostId(1L);
        assertEquals(commentDtosActual, commentDtos);
    }

    @Test
    public void testGetAllCommentsByPostIdIsMapped() {
        List<Comment> comments = preparationForGetAllCommentsByPostId();

        commentService.getAllCommentsByPostId(1L);
        verify(commentMapper).toDto(comments.get(0));
        verify(commentMapper).toDto(comments.get(1));
        verify(commentMapper).toDto(comments.get(2));
    }

    public List<Comment> preparationForGetAllCommentsByPostId() {
        Comment firstComment = new Comment();
        Comment secondComment = new Comment();
        Comment thirdComment = new Comment();
        firstComment.setCreatedAt(LocalDateTime.of(2024, 1, 14, 20, 5));
        secondComment.setCreatedAt(LocalDateTime.of(2024, 2, 5, 10, 25));
        thirdComment.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 4));
        List<Comment> comments = List.of(firstComment, secondComment, thirdComment);
        when(commentRepository.findAllByPostId(1L)).thenReturn(comments);

        return comments;
    }
}
