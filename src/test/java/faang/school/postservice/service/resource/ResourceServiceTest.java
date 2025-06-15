package faang.school.postservice.service.resource;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.s3.S3Dto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private S3Service s3Service;
    @Mock
    private PostService postService;
    @InjectMocks
    private ResourceServiceImpl resourceService;
    private MultipartFile file;
    private UserDto userDto;
    private Resource resource;
    private Post post;
    private static final long POST_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long RESOURCE_ID = 1L;

    @BeforeEach
    public void setUp() {
        file = new MockMultipartFile(
                "test.txt",
                "test",
                "testType",
                "File".getBytes()
        );
        userDto = UserDto.builder()
                .id(USER_ID)
                .username("Test name")
                .email("test@test.com")
                .build();
        resource = Resource.builder()
                .id(RESOURCE_ID)
                .key("test-key")
                .build();
        post = Post.builder()
                .id(POST_ID)
                .resources(List.of(resource))
                .build();
    }

    @Test
    public void testAddBuildForPost() {
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        when(userServiceClient.getUser(userContext.getUserId())).thenReturn(userDto);
        when(postRepository.findByAuthorId(USER_ID)).thenReturn(postList);
        when(s3Service.generateKeyForImage(any())).thenReturn("test-key");

        when(resourceRepository.save(any(Resource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Resource testResource = resourceService.addBuildForPost(POST_ID, file);

        assertNotNull(testResource);
        assertEquals(resource.getKey(), testResource.getKey());
        verify(s3Service).generateKeyForImage(any());
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    public void testDeleteImageByPostId() {
        when(postService.getPostById(POST_ID)).thenReturn(post);

        resourceService.deleteImageByPostId(POST_ID, RESOURCE_ID);

        verify(s3Service).deleteImage(resource.getKey());
        verify(resourceRepository).deleteById(RESOURCE_ID);
    }

    @Test
    public void testDownloadImage() {
        S3Dto s3Dto = new S3Dto();
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(s3Service.downloadImage(resource.getKey())).thenReturn(s3Dto);

        S3Dto resultS3Dto = resourceService.downloadImage(POST_ID, RESOURCE_ID);

        assertEquals(s3Dto, resultS3Dto);
        verify(s3Service).downloadImage(resource.getKey());
    }
}
