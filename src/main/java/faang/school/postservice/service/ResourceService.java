package faang.school.postservice.service;

import faang.school.postservice.dto.s3.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto addResource(Long postId, MultipartFile file) {
        Post post = postService.getPostById(postId);

        String folder = post.getProjectId().toString()+post.getId();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setPost(post);
        resourceRepository.save(resource);
        return resourceMapper.toResourceDto(resource);
    }


}
