package faang.school.postservice.service.resource;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.amazonS3.AmazonS3Service;
import faang.school.postservice.validator.image.ImageValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ImageValidator imageValidator;
    private final ResourceMapper resourceMapper;
    private final AmazonS3Service amazonS3Service;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public ResourceDto addFile(long postId, MultipartFile file) {
        Post post = postRepository.findById(postId).orElseThrow(()-> {
            log.error("Couldn't find post in Repository {}. ResourceService-addFile", postId);
            return new EntityNotFoundException("Couldn't find post in Repository ID = " + postId);
        });

        imageValidator.validateFileSize(file);
        String folderName = String.format("post%d", postId);
        ResourceDto resourceDto = amazonS3Service.uploadFile(file, folderName);
        resourceDto.setPostId(postId);

        Resource resourceToSave = resourceMapper.toEntity(resourceDto);
        Resource savedResource = resourceRepository.save(resourceToSave);

        post.getResources().add(savedResource);
        postRepository.save(post);


        return resourceMapper.toDto(savedResource);
    }
}
