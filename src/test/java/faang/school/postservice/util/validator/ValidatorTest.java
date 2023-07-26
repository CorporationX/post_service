package faang.school.postservice.util.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.util.exception.CreatePostException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostServiceValidator validator;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .build();
    }

    @Test
    void validatePostTest_InputsAreIncorrect_ShouldThrowException() {
        postDto.setProjectId(1L);

        Assert.assertThrows(CreatePostException.class, () -> {
            validator.validatePost(postDto);
        });
    }

    @Test
    void validatePostTest_ByAuthor_ShouldSave() {
        validator.validatePost(postDto);

        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void validatePostTest_ByProject_ShouldSave() {
        postDto.setAuthorId(null);
        postDto.setProjectId(1L);

        validator.validatePost(postDto);

        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }
}
