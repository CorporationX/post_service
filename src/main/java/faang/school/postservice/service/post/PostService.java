package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final ResourceService resourceService;

    @Transactional
    public PostDto create(PostDto postDto, MultipartFile[] images) {
        postValidator.validatePostAuthor(postDto);
        postValidator.validateIfAuthorExists(postDto);
        Post post = postRepository.save(postMapper.toEntity(postDto));
        log.info("Post saved: {}", post);
        post.setResources(new ArrayList<>());
        if (images != null) {
            postValidator.validateImagesCount(images.length);
            for (MultipartFile file : images) {
                Resource resource = resourceService.saveImage(file, post);
                post.getResources().add(resource);
            }
        }
        return postMapper.toDto(post);
    }

    public PostDto getPostById(long postId) {
        Post post = getPost(postId);
        return postMapper.toDto(post);
    }

    public PostDto publish(long postId) {
        Post post = getPost(postId);
        postValidator.validateIfPostIsPublished(post);
        postValidator.validateIfPostIsDeleted(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto update(PostDto postDto, MultipartFile[] images) {
        Post post = getPost(postDto.getId());
        postValidator.validateUpdatedPost(post, postDto);
        post.setContent(postDto.getContent());
        if (images != null) {
            postValidator.validateImagesCount(post.getResources().size(), images.length);
            for (MultipartFile file : images) {
                Resource resource = resourceService.saveImage(file, post);
                post.getResources().add(resource);
            }
        }
        return postMapper.toDto(post);
    }

    public void delete(long postId) {
        Post post = getPost(postId);
        postValidator.validateIfPostIsDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
    }

    public List<PostDto> getCreatedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getCreatedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted((post1, post2) -> post2.getCreatedAt().compareTo(post1.getCreatedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByUserId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    public List<PostDto> getPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted((post1, post2) -> post2.getPublishedAt().compareTo(post1.getPublishedAt()))
                .toList();
        return postMapper.toDto(posts);
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist by id: " + postId));
    }
}
