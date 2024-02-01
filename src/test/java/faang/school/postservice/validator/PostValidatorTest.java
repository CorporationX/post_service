package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    public void testValidatePostContentFailed() {
        String postContent = "";
        PostDto postDto = new PostDto();
        postDto.setContent(postContent);
        Assertions.assertThrows(DataValidationException.class, () ->
                postValidator.validatePostContent(postDto));
    }

    @Test
    public void testValidatePostContentSuccess() {
        String postContent = "Content";
        PostDto postDto = new PostDto();
        postDto.setContent(postContent);
        Assertions.assertDoesNotThrow(() -> postValidator.validatePostContent(postDto));
    }

    @Test
    public void testValidateOwnerPostAuthorAndProjectExists() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);
        Assertions.assertThrows(DataValidationException.class, () ->
                postValidator.validatePost(postDto));
    }

    @Test
    public void testValidateOwnerPostAuthorAndProjectNonExists() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(null);
        postDto.setProjectId(null);
        Assertions.assertThrows(DataValidationException.class, () ->
                postValidator.validatePost(postDto));
    }

    @Test
    public void testValidateOwnerPostSuccess() {
        PostDto postDto = new PostDto();
        postDto.setAuthorId(1L);
        postDto.setProjectId(null);
        Assertions.assertDoesNotThrow(() -> postValidator.validatePost(postDto));
    }
}
