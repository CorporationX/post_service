package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CheckUserServiceTest {

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    CheckUserService checkUserService;

    @InjectMocks
    CommentService commentService;

    private final CreateCommentDto createCommentDto = new CreateCommentDto();

    CreateCommentDto prepareCreateCommentDto() {
        createCommentDto.setContent("Test content");
        createCommentDto.setAuthorId(1L);
        createCommentDto.setPostId(1L);
        return createCommentDto;
    }

    @Test
    public void testCreateCommentIfAuthorDoesNotFound() {
        CreateCommentDto createCommentDto = prepareCreateCommentDto();
        when(userServiceClient.getUser(createCommentDto.getAuthorId())).thenReturn(Mockito.isNull());

        checkUserService.checkUserExistence(createCommentDto);

        assertThrows(RuntimeException.class, () -> commentService.createComment(createCommentDto));
    }

    @Test
    public void testCreateCommentIfAuthorFound() {
        CreateCommentDto createCommentDto = prepareCreateCommentDto();

        checkUserService.checkUserExistence(createCommentDto);

        verify(userServiceClient, times(1)).getUser(createCommentDto.getAuthorId());
    }
}
