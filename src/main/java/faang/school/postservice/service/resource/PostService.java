package faang.school.postservice.service.resource;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final ResourceService resourceService;

    public PostDto createPostDraft(PostDto postDto, List<MultipartFile> files) {
        //validator

        Post savedPost = savePostAndAddFiles(postMapper.toEntity(postDto), files);

        return postMapper.toDto(savedPost);
    }

    public PostDto updatePost(long postId, PostDto postDto, List<MultipartFile> files) {
        //validator
        Post post = getPost(postId);
        post.setContent(postDto.getContent());
        removeUnnecessaryResources(post, postDto);

        Post updatedPost = savePostAndAddFiles(post, files);

        return postMapper.toDto(updatedPost);
    }

    private void removeUnnecessaryResources(Post post, PostDto postDto) {
        List<Resource> existenceResources = post.getResources();
        List<Long> existenceResourceIds = new ArrayList<>(existenceResources.stream()
                .map(Resource::getId)
                .toList());

        List<Long> resourceIdsByDto = postDto.getResourceIds();
        if (resourceIdsByDto != null){
            existenceResourceIds.removeAll(resourceIdsByDto);
        }

        existenceResourceIds.forEach(id -> {
            Resource deletedResource = resourceService.deleteResourceToPost(id);
            post.getResources().remove(deletedResource);
        });
    }

    private Post savePostAndAddFiles(Post postMapper, List<MultipartFile> files) {
        Post savedPost = postRepository.save(postMapper);

        if (files != null) {
            List<Resource> resources = resourceService.addFilesToPost(files, savedPost);
            savedPost.setResources(resources);
        }

        return savedPost;
    }

    public Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id = %s not found", postId)));
    }
}