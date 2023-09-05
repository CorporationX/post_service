package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.DownloadFileDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostFileServiceTest {
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private S3Service s3Service;
    @Spy
    private ResourceMapper resourceMapper;
    @Mock
    private ImageService imageService;
    @InjectMocks
    private PostFileService postFileService;
    private List<MultipartFile> fileslist;
    private List<MultipartFile> files;


    @BeforeEach
    void data() {
        fileslist = new ArrayList<>();
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));
        fileslist.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));

        files = new ArrayList<>();
        files.add(new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes()));

    }

    @Test
    void testAddFilesEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> postFileService.addFiles(1L, fileslist));
    }

    @Test
    void testAddFilesDataValidationException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(Post.builder().id(1L).build()));
        assertThrows(DataValidationException.class,
                () -> postFileService.addFiles(1L, fileslist));
    }

    @Test
    void testAddFilesResources() {
        byte[] byteFile = {1, 2, 3, 4, 5};
        List<ResourceDto> resourcesDto = new ArrayList<>();
        resourcesDto.add(ResourceDto.builder().build());
        List<Resource> resources = new ArrayList<>();
        resources.add(Resource.builder().build());

        when(postRepository.findById(1L)).thenReturn(Optional.of(Post.builder().id(1L).build()));
        when(imageService.resizeImage(any())).thenReturn(byteFile);
        when(s3Service.uploadFile(any(), any(), any())).thenReturn("test");
        when(resourceRepository.saveAll(any())).thenReturn(resources);
        when(resourceMapper.toListDto(any())).thenReturn(resourcesDto);

        List<ResourceDto> resourceDtoList = postFileService.addFiles(1L, files);
        assertEquals(resourcesDto.size(), resourceDtoList.size());
    }

    @Test
    void testDownloadFileEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> postFileService.downloadFile(1L));

    }

    @Test
    void testDownloadFile() {
        byte[] byteFile = {1, 2, 3, 4, 5};
        DownloadFileDto downloadFileDto = DownloadFileDto.builder().name("test").contentType("jpg").bytes(byteFile).build();

        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(Resource.builder().id(1L).name("test").type("jpg").build()));
        when(s3Service.downloadFile(any())).thenReturn(byteFile);

        assertEquals(downloadFileDto, postFileService.downloadFile(1L));
    }

    @Test
    void testDeleteFileEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class,
                () -> postFileService.deleteFile(1L));
    }

    @Test
    void testDeleteFile() {
        when(resourceRepository.findById(1L))
                .thenReturn(Optional.of(Resource.builder().id(1L).build()));
        postFileService.deleteFile(1L);

        verify(resourceRepository).deleteById(1L);
    }
}