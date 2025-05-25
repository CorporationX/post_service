package faang.school.postservice.service.post_file.interfaces;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.mapper.post_file.PostFileMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.amazons3.interfaces.AmazonS3Service;
import faang.school.postservice.service.post.implementations.PostServiceImpl;
import faang.school.postservice.service.post_file.implementations.PostFileServiceImpl;
import faang.school.postservice.service.resource.implementations.ResourceServiceImpl;
import faang.school.postservice.validator.post_file.PostFileValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostFileServiceTest {
    @Spy
    private PostFileMapperImpl postFileMapper;

    @Mock
    UserContext userContext;

    @Mock
    private PostFileValidator postFileValidator;

    @Mock
    private ResourceServiceImpl resourceService;

    @Mock
    private AmazonS3Service amazonS3Service;

    @Mock
    private PostServiceImpl postService;

    @InjectMocks
    private PostFileServiceImpl postFileService;

    @Test
    void testUploadFilesToPostSuccessfully() {
        MultipartFile file = mock(MultipartFile.class);
        List<MultipartFile> files = Collections.singletonList(file);
        long postId = 1L;
        long requesterId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(requesterId);
        List<FileMetaData> fileMetaDatas = Collections.singletonList(
                new FileMetaData(new byte[]{1, 2, 3}, "file.jpg", "image", "jpg"));
        when(userContext.getUserId()).thenReturn(requesterId);
        when(postService.getPostById(postId)).thenReturn(post);
        when(postFileValidator.validateAndExtractFileMetadatas(files)).thenReturn(fileMetaDatas);
        Resource mockResource = mock(Resource.class);
        when(resourceService.save(any(Resource.class))).thenReturn(mockResource);
        when(amazonS3Service.uploadFile(any(), any())).thenAnswer(invocation -> {
            FileMetaData fileMetaData = invocation.getArgument(0);
            return CompletableFuture.completedFuture(Pair.of("key", fileMetaData));
        });

        postFileService.uploadFilesToPost(postId, files);

        verify(postFileValidator, times(1))
                .validatePostBelongsToUser(post, requesterId);
        verify(postFileValidator, times(1))
                .validateUploadFilesAmount(files);
        verify(postFileValidator, times(1))
                .validateAlreadyUploadedFilesAmount(files, 0);
        verify(postFileValidator, times(1))
                .validateFilesNotEmpty(files);
        verify(postFileValidator, times(1))
                .validateAndExtractFileMetadatas(files);
        verify(amazonS3Service, times(1))
                .uploadFile(any(), any());
        verify(resourceService, times(1))
                .save(any(Resource.class));
    }

    @Test
    void testDeletePostFileSuccessfully() {
        long postId = 1L;
        long fileId = 1L;
        long requesterId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(requesterId);
        Resource resource = new Resource();
        resource.setId(fileId);
        when(userContext.getUserId()).thenReturn(requesterId);
        when(postService.getPostById(postId)).thenReturn(post);
        when(resourceService.getResource(fileId)).thenReturn(resource);

        postFileService.deletePostFile(postId, fileId);

        verify(postFileValidator, times(1)).validatePostBelongsToUser(post, requesterId);
        verify(amazonS3Service, times(1)).deleteFile(resource.getKey());
        verify(resourceService, times(1)).deleteResource(fileId);
    }
}
