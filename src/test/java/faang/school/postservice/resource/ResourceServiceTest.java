package faang.school.postservice.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.resource.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import faang.school.postservice.service.s3.AmazonS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceMapper resourceMapper;
    @InjectMocks
    private ResourceService resourceService;

    private Post post;
    private long postId;
    private long resourceId;
    private MultipartFile mockMultipartFile1;
    private MultipartFile mockMultipartFile2;
    private Resource resource1;
    private Resource resource2;
    private ResourceDto resourceDto1;
    private ResourceDto resourceDto2;
    private List<Resource> resources;
    private List<MultipartFile> multipartFiles;
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        post = new Post();
        postId = 1L;
        resourceId = 2L;
        mockMultipartFile1 = mock(MultipartFile.class);
        mockMultipartFile2 = mock(MultipartFile.class);
        resource1 = new Resource();
        resource2 = new Resource();
        resourceDto1 = new ResourceDto();
        resourceDto2 = new ResourceDto();
        resources = new ArrayList<>();
        multipartFiles = new ArrayList<>();
        inputStream = mock(InputStream.class);

        post.setId(postId);
        resource1.setId(resourceId);
        resource1.setKey("key");
        resources.add(resource1);
        resources.add(resource2);
        post.setResources(resources);

        multipartFiles.add(mockMultipartFile1);
        multipartFiles.add(mockMultipartFile2);

    }

    @Test
    @DisplayName("Add file.")
    public void testAddFile() {
        when(postService.findById(postId)).thenReturn(post);
        when(amazonS3Service.uploadFile(mockMultipartFile1, String.valueOf(postId))).thenReturn(resource1);
        when(resourceMapper.toDto(resource1)).thenReturn(resourceDto1);

        ResourceDto result = resourceService.addFile(postId, mockMultipartFile1);

        assertEquals(resourceDto1, result);
        verify(resourceRepository).save(resource1);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Add files.")
    public void testAddFiles() {
        when(postService.findById(postId)).thenReturn(post);
        when(amazonS3Service.uploadFiles(multipartFiles, String.valueOf(postId))).thenReturn(resources);
        when(resourceMapper.toDto(resource1)).thenReturn(resourceDto1);
        when(resourceMapper.toDto(resource2)).thenReturn(resourceDto2);

        List<ResourceDto> result = resourceService.addResources(postId, multipartFiles);

        assertEquals(2, result.size());
        assertTrue(result.contains(resourceDto1));
        assertTrue(result.contains(resourceDto2));
        verify(resourceRepository).saveAll(resources);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Delete resource.")
    public void testDeleteResource() {
        when(postService.findById(postId)).thenReturn(post);
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource1);
        doNothing().when(amazonS3Service).deleteFile(anyString());

        post.getResources().add(resource1);

        resourceService.deleteResource(postId, resourceId);

        verify(resourceRepository).delete(resource1);
        verify(amazonS3Service).deleteFile(resource1.getKey());
    }

    @Test
    @DisplayName("Download resource.")
    public void testDownloadFile() {
        when(postService.findById(postId)).thenReturn(post);
        when(resourceRepository.getReferenceById(resourceId)).thenReturn(resource1);
        when(amazonS3Service.downloadFile("key")).thenReturn(inputStream);

        post.getResources().add(resource1);

        InputStream result = resourceService.downloadFile(postId, resourceId);

        assertEquals(inputStream, result);
    }
}
