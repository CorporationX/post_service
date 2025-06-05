package faang.school.postservice.service.post;

import faang.school.postservice.config.post.media.properties.PostMediaProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.service.utils.PostServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMediaService {
    private final PostServiceUtils postServiceUtils;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostMapper postMapper;
    private final PostMediaProperties postMediaProperties;

    @Transactional
    public PostDto addMediaFiles(Long postId, List<MultipartFile> files) {
        Post post = postServiceUtils.getPost(postId);
        log.info("Max amount of files in post {}", postMediaProperties.getMaxPostMediaFilesAmount());
        if(Objects.requireNonNullElse(post.getResources(), Collections.emptyList()).size()
                + files.size() > postMediaProperties.getMaxPostMediaFilesAmount()) {
            throw new DataValidationException("Exceeded maximum number of media files for a post");
        }
        for (MultipartFile file : files) {
            addMediaFile(post, file);
        }
        log.info("Media files added to post with ID: {}", postId);
        return postMapper.toPostDto(post);
    }

    @Transactional
    public PostDto deleteMediaFiles(Long postId, List<Long> fileIds) {
        Post post = postServiceUtils.getPost(postId);
        log.info("Deleting media files with IDs: {} for post ID: {}", fileIds, postId);

        for (Long fileId : fileIds) {
            Resource resource = resourceRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("Resource not found with ID: " + fileId));
            if (!Objects.equals(resource.getPost(), post)) {
                throw new DataValidationException("Resource with ID " + fileId + " does not belong to post with ID " + postId);
            }

            s3Service.deleteFile(resource.getKey());
            resourceRepository.delete(resource);
            post.getResources().remove(resource);
        }

        log.info("Media files deleted successfully for post ID: {}", postId);
        return postMapper.toPostDto(post);
    }

    @Transactional()
    public List<InputStream> getMediaFiles(Long postId) {
        Post post = postServiceUtils.getPost(postId);
        log.info("Retrieving media files for post ID: {}", postId);
        List<Resource> resources = post.getResources();
        return resources.stream()
                .map(s3Service::getFileAsInputStream)
                .toList();
    }

    private void addMediaFile(Post post, MultipartFile file) {
        String folder = "post" + post.getId();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setPost(post);
        post.getResources().add(resource);
        resourceRepository.save(resource);
    }
}
