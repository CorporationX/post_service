package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.S3Service;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.postImages.PostImageValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostImagesServiceTest {

    @InjectMocks
    private PostImagesService postImagesService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private PostService postService;

    @Mock
    private S3Service s3Service;

    @Mock
    private PostImageValidator postImageValidator;

    @Mock
    private DimensionChanger dimensionChanger;

    @Mock
    private PostRepository postRepository;

    private static final long ID = 1L;
    private static final String KEY = "key";

    private Post post;
    private Resource resource;
    private Resource resourceDB;
    private List<Resource> resources;
    private List<Resource> resourcesDB;
    private List<MultipartFile> images;
    private MultipartFile image;

    @BeforeEach
    public void init() {

        images = new ArrayList<>(10) {{
            add(image);
            add(image);
        }};

        resource = Resource.builder()
                .id(1L)
                .key(KEY)
                .name("UUID")
                .type("png")
                .size(BigInteger.ONE)
                .build();

        resourceDB = Resource.builder()
                .id(1L)
                .key(KEY)
                .name("Something")
                .type("png")
                .size(BigInteger.ONE)
                .build();

        resources = new ArrayList<>() {{
            add(resource);
        }};

        resourcesDB = new ArrayList<>() {{
            add(resourceDB);
        }};

        post = Post.builder()
                .id(ID)
                .resources(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Successful uploading of images")
    void whenUploadPostImagesThenSuccess() {
        when(postService.findById(ID)).thenReturn(post);
        when(s3Service.uploadFiles(images, ID)).thenReturn(resources);

        postImagesService.uploadPostImages(ID, images);

        verify(postService).findById(ID);
        verify(resourceService).saveResources(resources);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Successful uploading of images")
    void whenUpdatePostImagesThenSuccess() {
        post.setResources(resourcesDB);

        when(postService.findById(ID)).thenReturn(post);
        when(s3Service.uploadFiles(images, ID)).thenReturn(resources);

        postImagesService.updatePostImages(ID, images);


        verify(postService).findById(ID);
        verify(resourceService).deleteResources(resourcesDB);
        verify(resourceService).saveResources(resources);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Successful uploading of images")
    void whenDeletePostImageThenSuccess() {
        when(resourceService.findById(ID)).thenReturn(resource);


        postImagesService.deleteImage(ID);

        verify(resourceService).findById(ID);
        verify(resourceService).deleteResource(ID);
        verify(s3Service).deleteFile("UUID");
    }
}