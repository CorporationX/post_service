package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClientMock;
import faang.school.postservice.client.UserServiceClientMock;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {
    private static final int IMAGES_MAX_NUMBER = 10;

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private UserServiceClientMock userServiceClient;
    @Mock
    private ProjectServiceClientMock projectServiceClient;
    @Mock
    private MultipartFile image;
    @InjectMocks
    private PostValidator postValidator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postValidator, "imagesMaxNumber", IMAGES_MAX_NUMBER);
    }

    @Test
    void testValidateCreateAuthorAndProject() {
        Post createPost = Post.builder()
                .authorId(1L)
                .projectId(2L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));
    }

    @Test
    void testValidateCreateEmptyAuthorAndProject() {
        Post createPost = Post.builder()
                .authorId(null)
                .projectId(null)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));
    }

    @Test
    void testValidateCreateInputAuthor() {
        Post createPost = Post.builder()
                .authorId(1L)
                .build();

        when(userServiceClient
                .getUser(createPost.getAuthorId()))
                .thenReturn(new UserDto(createPost.getAuthorId(), null, null));

        postValidator.validateCreatePost(createPost);

        verify(userServiceClient).getUser(createPost.getAuthorId());
    }

    @Test
    void testValidateCreateInputAuthorFailed() {
        Post createPost = Post.builder()
                .authorId(1L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));

        verify(userServiceClient).getUser(createPost.getAuthorId());
    }

    @Test
    void testValidateCreateInputProject() {
        Post createPost = Post.builder()
                .projectId(1L)
                .build();

        when(projectServiceClient
                .getProject(createPost.getProjectId()))
                .thenReturn(new ProjectDto());

        postValidator.validateCreatePost(createPost);

        verify(projectServiceClient).getProject(createPost.getProjectId());
    }

    @Test
    void testValidateCreateInputProjectFailed() {
        Post createPost = Post.builder()
                .projectId(1L)
                .build();

        assertThrows(ValidationException.class, () -> postValidator.validateCreatePost(createPost));

        verify(projectServiceClient).getProject(createPost.getProjectId());
    }

    @Test
    void testValidateImagesToUpload_Exception_EmptyImages() {
        Long postId = 1L;
        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, null);
        });

        List<MultipartFile> imagesEmpty = List.of();
        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, imagesEmpty);
        });
    }

    @Test
    void testValidateImagesToUpload_Exception_ImagesMoreThanMaxNumber() {
        Long postId = 1L;
        List<MultipartFile> images = new ArrayList<>();
        for (int i = 1; i <= IMAGES_MAX_NUMBER + 1; i++) {
            images.add(image);
        }

        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, images);
        });
    }

    @Test
    void testValidateImagesToUpload_Exception_ExistedPlusUploadedImagesMoreThanMaxNumber() {
        Long postId = 1L;
        List<MultipartFile> images = List.of(image);
        List<Resource> resources = new ArrayList<>();
        for (int i = 1; i <= IMAGES_MAX_NUMBER; i++) {
            resources.add(new Resource());
        }

        when(resourceRepository.findAllByPostId(postId)).thenReturn(resources);

        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, images);
        });
    }

    @Test
    void testValidateImagesToUpload_Exception_Image_ContentTypeNull() {
        Long postId = 1L;
        byte[] content = new byte[]{};
        MockMultipartFile file = new MockMultipartFile("name", "originalFilename", null, content);
        List<MultipartFile> images = List.of(file);
        List<Resource> resources = List.of(new Resource());

        when(resourceRepository.findAllByPostId(postId)).thenReturn(resources);

        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, images);
        });
    }

    @Test
    void testValidateImagesToUpload_Exception_Image_ContentTypeUnavailable() {
        Long postId = 1L;
        byte[] content = new byte[]{};
        MockMultipartFile file = new MockMultipartFile("name", "originalFilename", "contentType", content);
        List<MultipartFile> images = List.of(file);
        List<Resource> resources = List.of(new Resource());

        when(resourceRepository.findAllByPostId(postId)).thenReturn(resources);

        assertThrows(ValidationException.class, () -> {
            postValidator.validateImagesToUpload(postId, images);
        });
    }

    @Test
    void testValidateImagesToUpload_Success() {
        Long postId = 1L;
        byte[] content = new byte[]{};
        MockMultipartFile file = new MockMultipartFile("name", "originalFilename", IMAGE_JPEG_VALUE, content);
        List<MultipartFile> images = List.of(file);
        List<Resource> resources = List.of(new Resource());

        when(resourceRepository.findAllByPostId(postId)).thenReturn(resources);

        assertDoesNotThrow(() -> postValidator.validateImagesToUpload(postId, images));
    }
}
