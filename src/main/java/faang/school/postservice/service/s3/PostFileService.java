package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.DownloadFileDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.resource.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFileService {
    private static final Integer MAX_FILE_SIZE = 10;
    private final ResourceRepository resourceRepository;
    private final PostRepository postRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ImageService imageService;

    @Transactional
    public List<ResourceDto> addFiles(Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("'Post not in database' error occurred while fetching post"));
        if (files.size() > MAX_FILE_SIZE) {
            throw new DataValidationException("Too many files");
        }
        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            byte[] bytes = imageService.resizeImage(file);
            String folder = post.getId() + "!" + post.getAuthorId();

            Resource resource = new Resource();
            resource.setName(file.getName());
            resource.setKey(s3Service.uploadFile(file, folder, bytes));
            resource.setPost(post);
            resource.setSize(file.getSize());
            resource.setType(file.getContentType());
            resource.setCreatedAt(LocalDateTime.now());
            resources.add(resource);
        }

        resourceRepository.saveAll(resources);
        return resourceMapper.toListDto(resources);
    }

    @Transactional
    public DownloadFileDto downloadFile(Long fileId) {
        Resource resource = getResource(fileId);
        byte[] bytes = s3Service.downloadFile(resource.getKey());

        return new DownloadFileDto(resource.getName(), resource.getType(), bytes);
    }

    @Transactional
    public ResourceDto deleteFile(Long fileId) {
        Resource resource = getResource(fileId);

        resourceRepository.deleteById(fileId);
        s3Service.deleteFile(resource.getKey());

        return resourceMapper.toDto(resource);
    }

    private Resource getResource(Long fileId) {
        return resourceRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("There is no matching resource in the database"));
    }
}
