package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.DeleteFileS3Service;
import faang.school.postservice.service.S3.UploadFilesS3Service;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostImagesServiceTest {

    @InjectMocks
    private PostImagesService postImagesService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private PostImageValidator imageValidator;

    @Mock
    private PostService postService;

    @Mock
    private DeleteFileS3Service deleteImageS3Service;

    @Mock
    private UploadFilesS3Service uploadImagesS3Service;

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
        when(uploadImagesS3Service.uploadFiles(images)).thenReturn(resources);

        postImagesService.uploadPostImages(ID, images);

        verify(postService).findById(ID);
        assertEquals(resources, post.getResources());
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Successful uploading of images")
    void whenUpdatePostImagesThenSuccess() {
        when(postService.findById(ID)).thenReturn(post);
        when(uploadImagesS3Service.uploadFiles(images)).thenReturn(resources);

        postImagesService.uploadPostImages(ID, images);

        resourcesDB = post.getResources();
        verify(postService).findById(ID);
        assertTrue(post.getResources().removeAll(resourcesDB));
        assertTrue(post.getResources().addAll(resources));
        assertEquals(resources, post.getResources());
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Successful uploading of images")
    void whenDeletePostImageThenSuccess() {
        when(resourceService.findById(ID)).thenReturn(resource);


        postImagesService.deleteImage(ID);

        verify(resourceService).findById(ID);
        verify(deleteImageS3Service).deleteFile("UUID");
        verify(resourceService).deleteResource(ID);
    }
}