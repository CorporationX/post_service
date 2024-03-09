package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final PostRepository postRepository;
    public final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final ResourceMapper resourceMapper;


    @Transactional
    public List<ResourceDto> addResource(long postId, List<MultipartFile> files) {
        Post post = postService.searchPostById(postId);
        if (post.getResources().size() == 10) {
            log.info("There are already 10 pictures in the post");
            throw new DataValidationException("A post can only have 10 images");
        }

        List<Resource> resources = new ArrayList<>();
        files.forEach(file -> {
            String folder = post.getId() + "/" + file.getName();
            Resource resource = s3Service.uploadFile(file, folder);
            log.info("File {} upload", resource.getName());
            resource.setPost(post);
            resources.add(resource);
            post.getResources().add(resource);
            log.info("File {} saved", resource.getName());
        });

        List<Resource> newResources = resourceRepository.saveAll(resources);
        postRepository.save(post);
        return newResources.stream()
                .map(resourceMapper::toDto)
                .toList();
    }


        @Transactional
        public void deleteResource ( long postId, long resourceId){
            Post post = postService.searchPostById(postId);
            Resource resource = resourceRepository.getReferenceById(resourceId);
            Optional<Resource> optionalResource = post.getResources().stream()
                    .filter(resource1 -> resource.getId() == resourceId)
                    .findFirst();
//            if (optionalResource.isEmpty()) {
//                log.info("Resource is empty");
//                throw new DataValidationException("Resource with id " + resourceId + " does not belong to post with id " + postId);
//            }
            Resource resource1 = optionalResource.orElseThrow(() -> {
                log.info("Resource is empty");
                return new DataValidationException("Resource with id " + resourceId + " does not belong to post with id " + postId);
            });
            post.getResources().remove(resource);
            postRepository.save(post);
            resourceRepository.delete(resource);
            String key = resource.getKey();
            log.info("File {} delete", resource.getName());
            s3Service.deleteFile(key);
        }
    }
