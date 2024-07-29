package faang.school.postservice;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.amazonS3.AmazonS3Service;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validator.image.ImageValidator;
import faang.school.postservice.validator.resource.ResourceValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    @Mock
    private ImageValidator imageValidator;
    @Mock
    private ResourceValidator resourceValidator;
    @Spy
    private ResourceMapper resourceMapper = Mappers.getMapper(ResourceMapper.class);
    @Mock
    private AmazonS3Service amazonS3Service;
    @Mock
    private PostRepository postRepository;
    @Mock
    private ResourceRepository resourceRepository;
    @InjectMocks
    private ResourceService resourceService;

    private static long POST_ID = 1L;
    private static long RESOURCE_ID = 1L;
    private static String FILE_KEY = "keyTest";


    private Post post;
    private Post postWithTenPictures;
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
    void init() {
        mockMultipartFile1 = mock(MultipartFile.class);
        mockMultipartFile2 = mock(MultipartFile.class);
        post = Post.builder()
                .id(POST_ID).build();

        resourceDto1 = ResourceDto.builder()
                .id(1L)
                .key(FILE_KEY)
                .postId(1L)
                .build();

        resource1 = resourceMapper.toEntity(resourceDto1);

        List<Resource> resourceList = LongStream.range(2, 12)
                .mapToObj(i ->
                        Resource.builder()
                                .id(i).key("KEYTEST" + i)
                                .post(post).build()).toList();
        postWithTenPictures = Post.builder()
                .id(POST_ID)
                .resources(resourceList)
                .build();

        inputStream = mock(InputStream.class);
    }

    @Test
    @DisplayName("Add file : Invalid postId")
    //public ResourceDto addResource(long postId, MultipartFile file)
    public void testAddResourceInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postRepository).findById(POST_ID);
        assertThrows(EntityNotFoundException.class, () -> resourceService.addResource(POST_ID, mockMultipartFile1));
    }

    @Test
    @DisplayName("Add file : File is more than 5MB")
    public void testAddResourceFileIsMoreThanLimit() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        doThrow(DataValidationException.class).when(imageValidator).validateFileSize(mockMultipartFile1);

        assertThrows(DataValidationException.class, () -> resourceService.addResource(POST_ID, mockMultipartFile1));
        verifyNoMoreInteractions(imageValidator, amazonS3Service, resourceRepository);
    }

    @Test
    @DisplayName("Add file : Reaching Limits of pictures per post")
    public void testAddResourceLimitOfPictures() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postWithTenPictures));
        doNothing().when(imageValidator).validateFileSize(mockMultipartFile1);
        String errorMessage = "Number of pictures in post are more than " + 10;
        doThrow(new DataValidationException(errorMessage)).when(imageValidator).validateFileCurrentPostImages(postWithTenPictures);
        Exception exception = assertThrows(DataValidationException.class,
                () -> resourceService.addResource(POST_ID, mockMultipartFile1));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Add file : Everything is fine")
    public void testAddResourceOk() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        doNothing().when(imageValidator).validateFileSize(mockMultipartFile1);
        doNothing().when(imageValidator).validateFileCurrentPostImages(post);

        String folderName = String.format("post%d", POST_ID);
        when(amazonS3Service.uploadFile(mockMultipartFile1, folderName)).thenReturn(resourceDto1);
        when(resourceMapper.toEntity(resourceDto1)).thenReturn(resource1);
        when(resourceRepository.save(resource1)).thenReturn(resource1);

        ResourceDto result = resourceService.addResource(POST_ID, mockMultipartFile1);
        assertEquals(resourceDto1, result);
        verify(resourceRepository, times(1)).save(resource1);
    }

    @Test
    @DisplayName("Delete file : Wrong resource ID")
    public void testDeleteResourceWrongID() {
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource1));
        String errorMessage = "Resource ID = " + RESOURCE_ID + " doesn't contain Post ID = " + POST_ID;
        doThrow(new DataValidationException(errorMessage)).when(resourceValidator).validateResourceInPost(POST_ID, resource1);

        Exception exception = assertThrows(DataValidationException.class, () -> resourceService.deleteResource(POST_ID, RESOURCE_ID));

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Delete file : Successfully")
    public void testDeleteResourceSuccessfully() {
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource1));
        doNothing().when(resourceValidator).validateResourceInPost(POST_ID, resource1);

        resourceService.deleteResource(POST_ID, RESOURCE_ID);

        verify(resourceRepository, times(1)).deleteById(anyLong());
        verify(amazonS3Service, times(1)).deleteFile(anyString());
    }

    @Test
    @DisplayName("Download file : Successfully")
    public void testDownloadFile(){
        when(resourceRepository.findById(RESOURCE_ID)).thenReturn(Optional.of(resource1));
        doNothing().when(resourceValidator).validateResourceInPost(POST_ID, resource1);

        when(amazonS3Service.downloadFile(FILE_KEY)).thenReturn(inputStream);

        InputStream result = resourceService.downloadFile(POST_ID, RESOURCE_ID);

        assertEquals(result, inputStream);
    }
}

