package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PostService postService;
    private final PostRepository postRepository;

    private String getFolderName(long postId, String contentType) {
        return String.format("%s-%s", postId, contentType);
    }

//    public List<ResourceDto> deleteResources(List<Long> resourceIds) {
//        List<Resource> resourcesToDelete = resourceIds.stream()
//                .map(this::validateAcces)
//    }


    @Transactional
    public List<ResourceDto> addResource(Long postId, List<MultipartFile> files) {
        Post post = postService.getPost(postId);

        String folder = post.getId() + "" + post.getProjectId();
        List<Resource> resources = new ArrayList<>();
        for (MultipartFile file : files) {

            Resource resource = s3Service.uploadFile(file, folder);
            resources.add(resource);
            resource.setPost(post);
            resource = resourceRepository.save(resource);
            List<Resource> postResources = post.getResources();
            postResources.add(resource);
            post.setResources(postResources);
        }
        postRepository.save(post);


        return resourceMapper.toListDto(resources);
    }

    public InputStream downloadResource(long resourceId) {
        Resource resource = getResourceById(resourceId);
        return s3Service.downloadFile(resource.getKey());
    }

    private Resource getResourceById(long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(
                () -> new DataValidationException("Resource not " + "found"));
    }


}
