package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private  PostValidator postValidator;
    @Mock
    private  PostRepository postRepository;
    @Mock
    private  UserServiceClient userServiceClient;
    @Mock
    private  ProjectServiceClient projectServiceClient;
    @Spy
    private PostMapperImpl postMapper = new PostMapperImpl();

    @InjectMocks
    private  PostService postService;

    private final PostDto postDto = new PostDto();



    @Test
    void CreateDraftWithAuthorTest() {
        postDto.setAuthorId(1L);
        Mockito.when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);

        postService.createDraftPost(postDto);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void CreateDraftWithProjectTest() {
        postDto.setProjectId(1L);
        Mockito.when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(null);

        postService.createDraftPost(postDto);
        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void CreateDraftWithAuthorAndProjectTest() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);

        Mockito.when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);
        Mockito.doThrow(new DataValidationException("У поста должен быть только один автор"))
                .when(postValidator).validateAuthorExists(Mockito.any(), Mockito.any());

        assertThrows(DataValidationException.class, ()-> postService.createDraftPost(postDto));
    }

    @Test
    void CreateDraftWithNonExistingCreatorTest() {
        assertThrows(IllegalArgumentException.class, ()-> postService.createDraftPost(postDto));
    }

    @Test
    void CreateDraftWithCorrectDataTest() {
        postDto.setAuthorId(1L);

        Mockito.when(userServiceClient.getUser(postDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "user1", "user1@mail"));
        postService.createDraftPost(postDto);

        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
    }
}