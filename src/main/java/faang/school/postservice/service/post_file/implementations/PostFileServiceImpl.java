package faang.school.postservice.service.post_file.implementations;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.dto.post_file.PostFileDto;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.mapper.post_file.PostFileMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.amazons3.interfaces.AmazonS3Service;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.service.post_file.interfaces.PostFileService;
import faang.school.postservice.service.resource.interfaces.ResourceService;
import faang.school.postservice.validator.post_file.PostFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFileServiceImpl implements PostFileService {
    private final UserContext userContext;
    private final PostFileMapper postFileMapper;
    private final PostFileValidator postFileValidator;
    private final PostService postService;
    private final ResourceService resourceService;
    private final AmazonS3Service amazonS3Service;

    @Override
    @Transactional
    public void uploadFilesToPost(long postId, List<MultipartFile> files) {
        long requesterId = userContext.getUserId();
        log.info("Received request from user with id {} to upload files to post with id {}", requesterId, postId);

        Post post = postService.getPostById(postId);
        int alreadyUploadedFilesAmount = resourceService.getCountByPostId(postId);
        postFileValidator.validatePostBelongsToUser(post, requesterId);
        postFileValidator.validateUploadFilesAmount(files);
        postFileValidator.validateAlreadyUploadedFilesAmount(files, alreadyUploadedFilesAmount);
        postFileValidator.validateFilesNotEmpty(files);
        List<FileMetaData> fileMetaDatas = postFileValidator.validateAndExtractFileMetadatas(files);
        String folder = "post/%s".formatted(postId);
        fileMetaDatas
                .forEach(fileMetaData -> amazonS3Service.uploadFile(fileMetaData, folder)
                        .thenAccept(uploadFileInfo -> {
                            Resource resource = createResource(
                                    uploadFileInfo.getRight(), uploadFileInfo.getLeft(), post);
                            resourceService.save(resource);
                        }));
    }

    @Override
    @Transactional
    public List<PostFileDto> getPostFilesInfo(long postId) {
        long requesterId = userContext.getUserId();
        log.info("Received request from user with id {} to get files info by post id {}", requesterId, postId);
        Post post = postService.getPostById(postId);
        List<Resource> postFiles = resourceService.findAllByPostId(post.getId());
        log.info("{} post files were found after the request of user with id {}. Post id {}",
                postFiles.size(), requesterId, postId);
        return postFileMapper.toDtoList(postFiles);
    }

    @Override
    @Transactional
    public void deletePostFile(long postId, long fileId) {
        long requesterId = userContext.getUserId();
        log.info("Received request from user with id {} to delete file with id {} from post with id {}",
                requesterId, fileId, postId);

        Post post = postService.getPostById(postId);
        postFileValidator.validatePostBelongsToUser(post, requesterId);
        Resource postFile = resourceService.getResource(fileId);
        amazonS3Service.deleteFile(postFile.getKey());
        resourceService.deleteResource(fileId);

        log.info("File with id {} was deleted from post with id {}. Requester id {}",
                fileId, postId, requesterId);
    }

    @Override
    @Transactional
    public FileMetaData downloadFile(long postId, long fileId) {
        long requesterId = userContext.getUserId();
        log.info("Received request from user with id {} to download file with id {} from post with id {}",
                requesterId, fileId, postId);

        Post post = postService.getPostById(postId);
        postFileValidator.validatePostBelongsToUser(post, requesterId);
        Resource postFile = resourceService.getResource(fileId);
        S3Object file = amazonS3Service.getFileFromS3(postFile.getKey());

        String[] fileTypeInfo = postFile.getType().split("/");
        try (InputStream inputStream = file.getObjectContent()) {
            byte[] data = IOUtils.toByteArray(inputStream);
            log.info("File with id {} (key={}) was downloaded from AmazonS3", fileId, postFile.getKey());
            return FileMetaData.builder()
                    .data(data)
                    .originalName(postFile.getName())
                    .type(fileTypeInfo[0])
                    .extension(fileTypeInfo[1])
                    .build();
        } catch (IOException e) {
            throw new FileProcessException("Error occurred while reading file from S3: %s"
                    .formatted(postFile.getKey()), e);
        }
    }

    private Resource createResource(FileMetaData fileMetaData, String key, Post post) {
        return Resource.builder()
                .key(key)
                .name(fileMetaData.getOriginalName())
                .type("%s%s".formatted(fileMetaData.getType(), fileMetaData.getExtension()))
                .size(fileMetaData.getData().length)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
