package faang.school.postservice.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.image.ImageResizer;
import faang.school.postservice.mapper.resource.ResourceMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.validation.resource.ResourceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private AmazonS3 amazonS3Client;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ImageResizer imageResizer;
    @Mock
    private ResourceValidator resourceValidator;
    @Spy
    private ResourceMapperImpl resourceMapper;
    @InjectMocks
    private ResourceService resourceService;

    @BeforeEach
    public void setUp() throws Exception {
        Field name = resourceService.getClass().getDeclaredField("bucketName");
        name.setAccessible(true);
        name.set(resourceService, "post-service-bucket");
    }

    @Test
    void saveImage_ValidArgs() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        when(imageResizer.getResizedImage(any(MultipartFile.class))).thenReturn(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        MockMultipartFile file = new MockMultipartFile("file", "filename.jpg", "image/jpeg", new ByteArrayInputStream(baos.toByteArray()));
        ObjectMetadata expectedMetadata = new ObjectMetadata();
        expectedMetadata.setContentType("image/jpeg");
        expectedMetadata.setContentLength(235);

        Resource resource = resourceService.saveImage(file, Post.builder().resources(new ArrayList<>()).build());

        assertNotNull(resource);
    }

    @Test
    void deleteResource_ValidArgs() {
        when(resourceRepository.findById(anyLong())).thenReturn(Optional.ofNullable(
                Resource.builder()
                        .key("erg")
                        .build())
        );

        resourceService.deleteResource(1L);

        verify(amazonS3Client, times(1)).deleteObject(anyString(), anyString());
    }

    @Test
    void attachMediaToPost_ValidArgs() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.mpeg", "video/mpeg", "test.mpeg".getBytes());
        Post post = getPost();

        ResourceDto resource = resourceService.attachMediaToPost(mockMultipartFile, post);

        assertEquals(post.getId(), resource.getPostId());
        verify(resourceValidator, times(1)).validateAudioOrVideoFileSize(any(MultipartFile.class));
        verify(resourceValidator, times(1)).validateTypeAudioOrVideo(any(MultipartFile.class));
        verify(amazonS3Client, times(1)).putObject(any(PutObjectRequest.class));
        verify(resourceRepository, times(1)).save(any(Resource.class));
        verify(resourceMapper, times(1)).toDto(any(Resource.class));
    }

    private Post getPost() {
        return Post.builder()
                .id(1L)
                .build();
    }
}
