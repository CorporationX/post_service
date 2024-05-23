package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    public void testValidateCommentWithEmptyAuthor() {
        CommentDto commentDto = CommentDto.builder()
                .authorId(999L)
                .build();
        Mockito.when(userServiceClient.getUser(commentDto.getAuthorId())).thenReturn(null);
        Assert.assertThrows(DataValidationException.class, () ->
                commentValidator.checkCommentAuthor(commentDto));
    }
}
