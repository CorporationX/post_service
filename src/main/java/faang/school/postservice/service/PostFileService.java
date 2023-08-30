package faang.school.postservice.service;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFileService {

    private static final Integer MAX_FILE_SIZE = 10;
    private static final Integer MAX_FILE_COUNT = 5242880;

    private final ResourceRepository resourceRepository;
    private final PostService postService;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final PostMapper postMapper;


    @Transactional
    public ResourceDto addFiles(Long postId, List<MultipartFile> files) {
        Post post = postMapper.toEntity(postService.getPostById(postId));
        if (files.size() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Too many files");
        }
        Resource resource = new Resource();
        for (MultipartFile file : files) {
            if (file.getSize() > MAX_FILE_COUNT) {
                throw new IllegalArgumentException("Too large file");
            }

            String folder = post.getId() +"!"+ post.getAuthorId();
            resource = s3Service.uploadFile(file, folder);
            resource.setPost(post);

        }
        resource = resourceRepository.save(resource);
        return resourceMapper.toDto(resource);
    }

    @Transactional
    public InputStream downloadFile(Long fileId) {
        Resource resource =;
        return s3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public void deleteFile( Long fileId) {
        resourceRepository.findById(fileId).ifPresent(resourceRepository::delete);
    }
}
