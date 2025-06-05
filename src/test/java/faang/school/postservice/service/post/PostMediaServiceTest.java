package faang.school.postservice.service.post;

import faang.school.postservice.config.post.media.properties.PostMediaProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.service.utils.PostServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import faang.school.postservice.exception.DataValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostMediaServiceTest { // Предполагаем, что это тесты для PostMediaService

    @Mock
    private PostServiceUtils postServiceUtils;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private PostMediaService postMediaService; // Ваш сервис, содержащий эти методы

    private Post post;
    private Resource resource1;
    private Resource resource2;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setResources(new ArrayList<>()); // Инициализируем, чтобы избежать NPE

        resource1 = new Resource();
        resource1.setId(10L);
        resource1.setKey("media/key1.jpg");
        resource1.setPost(post);

        resource2 = new Resource();
        resource2.setId(20L);
        resource2.setKey("media/key2.png");
        resource2.setPost(post);

        post.getResources().add(resource1);
        post.getResources().add(resource2);

        postDto = new PostDto(); // Заполните при необходимости
    }

    // Тесты для deleteMediaFiles
    @Test
    void deleteMediaFiles_shouldDeleteFilesAndRemoveFromPost_whenFilesExistAndBelongToPost() {
        // Arrange
        Long postId = 1L;
        List<Long> fileIdsToDelete = Arrays.asList(10L, 20L);

        when(postServiceUtils.getPost(postId)).thenReturn(post);
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource1));
        when(resourceRepository.findById(20L)).thenReturn(Optional.of(resource2));
        when(postMapper.toPostDto(post)).thenReturn(postDto);
        // s3Service.deleteFile не возвращает значение в вашем текущем коде, поэтому when().thenReturn() не нужен

        // Act
        PostDto resultDto = postMediaService.deleteMediaFiles(postId, fileIdsToDelete);

        // Assert
        assertNotNull(resultDto);
        assertTrue(post.getResources().isEmpty()); // Проверяем, что ресурсы удалены из списка поста

        verify(s3Service).deleteFile("media/key1.jpg");
        verify(resourceRepository).delete(resource1);
        verify(s3Service).deleteFile("media/key2.png");
        verify(resourceRepository).delete(resource2);
        verify(postMapper).toPostDto(post);
    }

    @Test
    void deleteMediaFiles_shouldThrowDataValidationException_whenResourceNotFound() {
        // Arrange
        Long postId = 1L;
        List<Long> fileIdsToDelete = List.of(99L); // Несуществующий ID

        when(postServiceUtils.getPost(postId)).thenReturn(post);
        when(resourceRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            postMediaService.deleteMediaFiles(postId, fileIdsToDelete);
        });
        assertEquals("Resource not found with ID: 99", exception.getMessage());
        verify(s3Service, never()).deleteFile(anyString()); // S3 удаление не должно вызываться
    }

    @Test
    void deleteMediaFiles_shouldThrowDataValidationException_whenResourceDoesNotBelongToPost() {
        // Arrange
        Long postId = 1L;
        Post otherPost = new Post();
        otherPost.setId(2L);
        resource1.setPost(otherPost); // Ресурс принадлежит другому посту
        List<Long> fileIdsToDelete = List.of(10L);

        when(postServiceUtils.getPost(postId)).thenReturn(post);
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(resource1));

        // Act & Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            postMediaService.deleteMediaFiles(postId, fileIdsToDelete);
        });
        assertEquals("Resource with ID 10 does not belong to post with ID 1", exception.getMessage());
        verify(s3Service, never()).deleteFile(anyString());
    }

    // Тесты для getMediaFiles
    @Test
    void getMediaFiles_shouldReturnListOfInputStreams_whenPostHasResources() {
        // Arrange
        Long postId = 1L;
        InputStream stream1 = new ByteArrayInputStream("data1".getBytes());
        InputStream stream2 = new ByteArrayInputStream("data2".getBytes());

        when(postServiceUtils.getPost(postId)).thenReturn(post);
        // Мокируем вызовы getFileAsInputStream для каждого ресурса
        when(s3Service.getFileAsInputStream(resource1)).thenReturn(stream1);
        when(s3Service.getFileAsInputStream(resource2)).thenReturn(stream2);

        // Act
        List<InputStream> resultStreams = postMediaService.getMediaFiles(postId);

        // Assert
        assertNotNull(resultStreams);
        assertEquals(2, resultStreams.size());
        assertTrue(resultStreams.contains(stream1));
        assertTrue(resultStreams.contains(stream2));

        verify(s3Service).getFileAsInputStream(resource1);
        verify(s3Service).getFileAsInputStream(resource2);
    }

    @Test
    void getMediaFiles_shouldThrowDataValidationException_whenPostHasNoResources() {
        // Arrange
        Long postId = 1L;
        post.setResources(new ArrayList<>()); // Убираем ресурсы из поста

        when(postServiceUtils.getPost(postId)).thenReturn(post);

        // Act & Assert
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            postMediaService.getMediaFiles(postId);
        });
        assertEquals("No media files found for post with ID: 1", exception.getMessage());
        verify(s3Service, never()).getFileAsInputStream(any(Resource.class));
    }
}