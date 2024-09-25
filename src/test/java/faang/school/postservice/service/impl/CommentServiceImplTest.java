package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.mapper.post.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    UserServiceClient userServiceClient;

    @Spy
    CommentMapperImpl commentMapper;

    @Spy
    PostMapperImpl postMapper;

    private CommentDto commentDto;
    private Post post;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "NameTest", "mail.ru");

        post = new Post();
        post.setId(1L);
        post.setContent("content");

        commentDto = CommentDto.builder()
                .id(1L)
                .postId(1L)
                .content("Test Comment")
                .authorName(userDto.getUsername())
                .build();

        commentDto.setAuthorId(1L);
        commentDto.setAuthorName(userDto.getUsername());
        commentDto.setPostId(commentDto.getPostId());
        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test Comment");
        comment.setPost(post);
    }

    @Test
    void createComment_shouldReturnCommentDto() {
        when(postService.getPost(1L)).thenReturn(PostDto.builder().build());
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto);
        result.setPostId(1L);

        assertEquals("NameTest", result.getAuthorName());
        assertEquals(1L, result.getPostId());

        verify(postService).getPost(1L);
        verify(userServiceClient).getUser(1L);
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void updateComment_shouldReturnUpdatedCommentDto() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.updateComment(commentDto);

        assertEquals(commentDto.getContent(), result.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void getAllCommentsByPostId_shouldReturnListOfComments() {
        when(commentRepository.findAllByPostId(1L)).thenReturn(List.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(userDto);

        List<CommentDto> result = commentService.getAllCommentsByPostId(1L);

        assertEquals(1, result.size());
        assertEquals("NameTest", result.get(0).getAuthorName());
    }

    @Test
    void deleteComment_shouldDeleteExistingComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        commentService.deleteComment(1L);

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L));
    }
}
