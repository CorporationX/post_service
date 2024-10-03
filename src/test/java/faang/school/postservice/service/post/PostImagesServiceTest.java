package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.S3.delete.DeleteFileS3ServiceImpl;
import faang.school.postservice.service.S3.upload.UploadFilesS3ServiceImpl;
import faang.school.postservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostImagesServiceTest {

    @InjectMocks
    private PostImagesService postImagesService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ImageValidator imageValidator;

    @Mock
    private PostService postService;

    @Mock
    private DeleteFileS3ServiceImpl deleteImageS3Service;

    @Mock
    private UploadFilesS3ServiceImpl uploadImagesS3Service;

    @Mock
    private PostRepository postRepository;

    private static final long ID = 1L;
    private static final String KEY = "key";

    private Post post;
    private Resource resource;
    private List<Resource> resources;
    private List<MultipartFile> images;
    private MultipartFile image;

    @BeforeEach
    public void init() {

        images = new ArrayList<>(10);
        images.add(image);
        images.add(image);

        resource = Resource.builder()
                .id(1L)
                .key(KEY)
                .name("UUID")
                .type("png")
                .size(1L)
                .build();

        resources = new ArrayList<>();
        resources.add(resource);

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
}