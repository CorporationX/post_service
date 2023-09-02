package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class CommentEventValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentEventValidator validator;
    private CommentEventDto dto;
    private UserDto userDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        dto = CommentEventDto.builder()
                .postId(4L)
                .authorId(2L)
                .commentId(3L)
                .build();

        userDto = UserDto.builder().id(1L).build();

        comment = Comment.builder()
                .id(3L)
                .content("Comment 3")
                .authorId(1L)
                .post(Post.builder().id(4L).build())
                .build();
    }

    @Test
    void testValidateBeforeCreate_ValidDto() {
        when(userServiceClient.getUser(2L)).thenReturn(userDto);

        comment = new Comment() ;
        when(commentRepository.findById(3L)).thenReturn(Optional.of(comment));

        validator.validateBeforeCreate(dto);
    }

    @Test
    void testValidateBeforeCreate_InvalidAuthor() {
        when(userServiceClient.getUser(2L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> validator.validateBeforeCreate(dto));
    }

    @Test
    void testValidateBeforeCreate_InvalidPost() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(commentRepository.findAllByPostId(2L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> validator.validateBeforeCreate(dto));
    }


    @Test
    void testValidateBeforeCreate_InvalidComment() {
        when(userServiceClient.getUser(2L)).thenReturn(userDto);

        when(commentRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> validator.validateBeforeCreate(dto));
    }
}