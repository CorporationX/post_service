package faang.school.postservice.service.resource;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.s3.S3Dto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ResourceServiceImpl implements ResourceService {
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final S3Service s3Service;
    private final PostService postService;
    private final static long MAX_SIZE_RESOURCE_LIST = 10;

    @Override
    @Transactional
    public Resource addImageToPost(long postId, MultipartFile file) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        Post post = postService.getPostById(postId);
        String key = s3Service.generateKeyForImage(file);
        if (post.getResources().size() >= MAX_SIZE_RESOURCE_LIST) {
            throw new DataValidationException(String.format("The post contains the maximum number of pictures %d",
                    MAX_SIZE_RESOURCE_LIST));
        }
        if (!Objects.equals(post.getAuthorId(), user.id())) {
            throw new DataValidationException(String.format("user with id %d is not the author of post with id %d",
                    post.getId(), user.id()));
        }
        Resource resource = new Resource();
        resource.setKey(key);
        resource.setSize(file.getSize());
        resource.setPost(post);
        resource.setName(file.getOriginalFilename());
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public void deleteImageByPostId(long postId, long resourceId) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        Post post = postService.getPostById(postId);
        Resource resource = resourceRepository.findById(resourceId)
                        .orElseThrow(() -> new DataValidationException(String.format("resource with such id %d" +
                                " does not exist", resourceId)));
        if(!post.getResources().contains(resource)) {
            throw new IllegalArgumentException(String.format("There is no such map in this resource %d", resourceId));
        }
        if (!Objects.equals(post.getAuthorId(), user.id())) {
            throw new DataValidationException(String.format("user with id %d is not the author of post with id %d",
                    post.getId(), user.id()));
        }
        s3Service.deleteImage(resource.getKey());
        resourceRepository.deleteById(resource.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public S3Dto downloadImage(long postId, long resourceId) {
        Post post = postService.getPostById(postId);
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new DataValidationException(String.format("resource with such id %d" +
                        " does not exist", resourceId)));
        if(!post.getResources().contains(resource)){
            throw new IllegalArgumentException(String.format("There is no such map in this resource %d", resourceId));
        }
        return s3Service.downloadImage(resource.getKey());
    }
}
