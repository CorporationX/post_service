package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {
    @Mock
    private PostService postService;
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private ResourceController resourceController;
    private final long postId = 1L;
    private final Post post = Post.builder().id(postId).build();
    private final ResourceDto resourceDto = ResourceDto.builder().id(1L).build();
    private List<MultipartFile> files;
    private List<ResourceDto> resourceDtos;

    @BeforeEach
    void init () {
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        files = new ArrayList<>(List.of(multipartFileMock, multipartFileMock, multipartFileMock));

        ResourceDto resourceDto1 = ResourceDto.builder().id(1L).build();
        ResourceDto resourceDto2 = ResourceDto.builder().id(2L).build();
        ResourceDto resourceDto3 = ResourceDto.builder().id(3L).build();
        resourceDtos = new ArrayList<>(List.of(resourceDto1, resourceDto2, resourceDto3));
    }

    @Test
    void testCreateResources () {
        when(postService.getPost(postId)).thenReturn(post);
        when(resourceService.createResources(post, files)).thenReturn(resourceDtos);

        List<ResourceDto> resourcesDtosByController = resourceController.createResources(postId, files);

        assertEquals (resourceDtos, resourcesDtosByController);
        verify(postService, times(1)).getPost(postId);
        verify(resourceService, times(1)).createResources(post, files);
    }

    @Test
    void testGetResources () {
        Long resourceId = resourceDto.getId();
        when(resourceService.getResource(resourceId)).thenReturn(resourceDto);

        ResourceDto resourcesDtoByController = resourceController.getResource(resourceId);

        assertEquals (resourceDto, resourcesDtoByController);
        verify(resourceService, times(1)).getResource(resourceId);
    }

    @Test
    void testGetFile () {
        Long resourceId = resourceDto.getId();
        byte[] image = new byte[] {1, 2, 3};
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        ResponseEntity<byte[]>expectedResponseEntity = new ResponseEntity<>(image, headers, HttpStatus.OK);

        when(resourceService.downloadResource(resourceId)).thenReturn(image);

        ResponseEntity<byte[]> responseEntityByController = resourceController.getFile(resourceId);

        assertEquals (expectedResponseEntity, responseEntityByController);
        verify(resourceService, times(1)).downloadResource(resourceId);
    }

    @Test
    void testDeleteResource () {
        Long resourceId = resourceDto.getId();
        List<Long> resourceIds = List.of(resourceId);
        when(resourceService.deleteResources(resourceIds)).thenReturn(List.of(resourceDto));

        ResourceDto resourceDtoByController = resourceController.deleteResource(resourceId);

        assertEquals (resourceDto, resourceDtoByController);
        verify(resourceService, times(1)).deleteResources(resourceIds);
    }
}