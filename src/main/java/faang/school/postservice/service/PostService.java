package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;

    @Transactional
    public PostDto create(PostDto postDto) {
        log.info("Trying to create post with ID: {}", postDto.getId());
        postValidator.validateAuthorIdAndProjectId(postDto.getAuthorId(), postDto.getProjectId());
        Post post = postRepository.save(postMapper.toEntity(postDto));
        log.info("Post with ID:{} created.", postDto.getId());
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publish(Long postId) {
        log.info("Trying to publish post with ID: {}", postId);
        Post post = findById(postId);
        postValidator.validatePublicationPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        log.info("Post with ID: {} published.", postId);
        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto update(Long postId, String content) {
        log.info("Trying to update post with ID: {}", postId);
        Post post = findById(postId);
        post.setContent(content);
        postRepository.save(post);
        log.info("Post with ID:{} created. Content updated on {}", postId, content);
        return postMapper.toDto(post);
    }

    @Transactional
    public void deleteById(Long postId) {
        log.info("Trying to delete post with ID: {}", postId);
        Post post = findById(postId);
        post.setDeleted(true);
        postRepository.save(post);
        log.info("A post with this ID: {} has been added to the deleted list.", postId);
    }

    @Transactional
    public Post findById(Long postId) {
        log.info("Attempting to find post with ID: {}", postId);
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error(String.format("Post with this ID: %s was not found", postId));
                    return new DataValidationException(String.format("Post with this ID: %s was not found", postId));
                });
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long userId) {
        log.info("Trying to get drafts of posts, where the author is a user with ID: {}", userId);
        List<PostDto> draftsPostsByUser = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
        log.info("Found {} posts for author with ID: {}", draftsPostsByUser.size(), userId);
        return draftsPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        log.info("Trying to get drafts of posts, where the author is a project with ID: {}", projectId);
        List<PostDto> draftsPostsByProject = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
        log.info("Found {} posts for author with ID: {}", draftsPostsByProject.size(), projectId);
        return draftsPostsByProject;
    }

    @Transactional
    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(Long userId) {
        log.info("Trying to get all published, non-deleted posts authored by a user with a given id: {}", userId);
        List<PostDto> publishedPostsByUser = postRepository.findByAuthorId(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
        log.info("Found {} posts for author with ID: {}", publishedPostsByUser.size(), userId);
        return publishedPostsByUser;
    }

    @Transactional
    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(Long projectId) {
        log.info("Trying to get all published, non-deleted posts authored by a project with a given id: {}", projectId);
        List<PostDto> publishedPostsByProject = postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .toList();
        log.info("Found {} posts for author with ID: {}", publishedPostsByProject.size(), projectId);
        return publishedPostsByProject;
    }
}