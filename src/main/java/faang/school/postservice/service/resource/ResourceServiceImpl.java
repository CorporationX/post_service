package faang.school.postservice.service.resource;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
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
                    if(post.getResources().size() < 10) {
                        resource.setKey(key);
                        resource.setSize(file.getSize());
                        resource.setPost(post);
                        resource.setName(file.getOriginalFilename());
                    } else {
                        throw  new IllegalArgumentException("Max size list Resource == 10");
                    }
                });
        return resourceRepository.save(resource);
    }

    @Override
    public void deleteImageByPostId(long postId, long resourceId) {
        Post post = postService.getPostById(postId);
        List<Resource> resources = post.getResources();
        for(Resource resource : resources) {
            if(resource.getId() == resourceId) {
                s3Service.deleteImage(resource.getKey());
                resourceRepository.deleteById(resourceId);
            }
        }
    }
}
