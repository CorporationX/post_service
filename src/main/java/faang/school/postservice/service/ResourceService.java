package faang.school.postservice.service;

import faang.school.postservice.dto.ResourceDto;
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

import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final PostService postService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto addResource(Long postId, MultipartFile file) {
        Post post = postService.getPost(postId);

        String folder = post.getId() + "" + post.getProjectId();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setPost(post);
        resource = resourceRepository.save(resource);

        List<Resource> postResources = post.getResources();
        postResources.add(resource);
        post.setResources(postResources);

        postRepository.save(post);

        return resourceMapper.toDto(resource);
    }
}
