package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserBanEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final UserBanEventPublisher userBanEventPublisher;

    public void createPostDraft(PostDto postDto) {
        postValidator.validatePostOwnerExists(postDto);
        postValidator.validatePost(postDto);
        postRepository.save(postMapper.toEntity(postDto));
    }

    @Transactional
    public void publishPost(long postId, long ownerId) {
        postValidator.validatePostByOwner(postId, ownerId);
        Post post = getPost(postId);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
    }

    @Transactional
    public void updatePost(long postId, long ownerId, PostDto postDto) {
        postValidator.validatePostByOwner(postId, ownerId);
        Post post = getPost(postId);
        post.setContent(postDto.getContent());
    }

    @Transactional
    public void deletePost(long postId, long ownerId) {
        postValidator.validatePostByOwner(postId, ownerId);
        Post post = getPost(postId);
        post.setDeleted(true);
    }

    public PostDto getPostById(long postId) {
        return postMapper.toDto(getPost(postId));
    }

    public Post getPost(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post not found"));
    }

    @Transactional
    public List<PostDto> getAuthorDrafts(long authorId) {
        postValidator.validateAuthor(authorId);
        return sortDrafts(postRepository.findByAuthorId(authorId));
    }

    @Transactional
    public List<PostDto> getProjectDrafts(long projectId) {
        postValidator.validateProject(projectId);
        return sortDrafts(postRepository.findByProjectId(projectId));
    }

    @Transactional
    public List<PostDto> getAuthorPosts(long authorId) {
        postValidator.validateAuthor(authorId);
        return sortPosts(postRepository.findByAuthorId(authorId));
    }

    @Transactional
    public List<PostDto> getProjectPosts(long projectId) {
        postValidator.validateProject(projectId);
        return sortPosts(postRepository.findByProjectId(projectId));
    }

    public void checkAndBanAuthors() {
        Map<Long, Integer> postCountByAuthorId = new HashMap<>();

        postRepository.findAllNotVerified().forEach(post ->
                postCountByAuthorId.merge(post.getAuthorId(), 1, Integer::sum)
        );

        postCountByAuthorId.forEach((authorId, postCount) -> {
            if (postCount > 5) {
                userBanEventPublisher.publish(
                        UserBanEvent.builder().userId(authorId).build()
                );
                log.debug("User ban event published with authorId = {} with amount of not verified posts = {}",
                        authorId, postCount);
            }
        });
        log.info("check and ban authors method completed");
    }

    public List<PostDto> sortDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<PostDto> sortPosts(List<Post> posts) {
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}
