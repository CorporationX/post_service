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
    public Resource addBuildForPost(long postId, MultipartFile file) {
        UserDto user = userServiceClient.getUser(userContext.getUserId());
        List<Post> lists = postRepository.findByAuthorId(user.id());
        String key = s3Service.generateKeyForImage(file);
        Resource resource = new Resource();

        lists.stream()
                .filter(post -> post.getId() == postId)
                .findFirst()
                .ifPresent(post -> {
                    if (post.getResources().size() < MAX_SIZE_RESOURCE_LIST) {
                        resource.setKey(key);
                        resource.setSize(file.getSize());
                        resource.setPost(post);
                        resource.setName(file.getOriginalFilename());
                    } else {
                        throw new IllegalArgumentException("Max size list Resource == 10");
                    }
                });
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public void deleteImageByPostId(long postId, long resourceId) {
        Post post = postService.getPostById(postId);
        post.getResources().stream()
                .filter(resource -> resource.getId() == resourceId)
                .findFirst()
                .ifPresent(resource -> {
                    s3Service.deleteImage(resource.getKey());
                    resourceRepository.deleteById(resource.getId());
                });
    }

    @Override
    @Transactional(readOnly = true)
    public S3Dto downloadImage(long postId, long resourceId) {
        Post post = postService.getPostById(postId);
        return post.getResources().stream()
                .filter(resource -> resource.getId() == resourceId)
                .findFirst()
                .map(resource -> s3Service.downloadImage(resource.getKey()))
                .orElseThrow(() -> new DataValidationException(String.format("Resource by id %d not Found", resourceId)));
    }
}
