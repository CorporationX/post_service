package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.dto.UserBanEventDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.publisher.kafka.PostKafkaProducer;
import faang.school.postservice.publisher.redis.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.RedisPostCacheService;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.ResourceValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final ResourceValidator resourceValidator;
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;
    private final UserBanEventPublisher userBanEventPublisher;
    private final RedisPostCacheService redisPostCacheService;
    private final PostKafkaProducer postKafkaProducer;
    @PersistenceContext
    private final EntityManager entityManager;
    @Value("${post.content_to_post.max_amount.video}")
    private int maxVideo;
    @Value("${post.rule.unverified_posts_limit}")
    private int unverifiedPostsLimit;

    @Transactional
    public PostDto createPost(PostDto postDto, List<MultipartFile> files) {
        postValidator.validateAccessAndContent(postDto);

        Post post = postMapper.toEntity(postDto);
        post.setVerified(true);
        Post savedPost = postRepository.save(post);

        return createResourcesAndGetPostDto(savedPost, files);
    }

    @Transactional
    public void publishPost(long postId) {
        Post post = getPost(postId);
        Long authorId = post.getAuthorId();
        LocalDateTime publishedAt = LocalDateTime.now();
        postValidator.validateAccessToPost(authorId, post.getProjectId());

        post.setPublished(true);
        post.setPublishedAt(publishedAt);

        redisPostCacheService.savePost(postMapper.toDto(post));
        postKafkaProducer.publish(postId, authorId, publishedAt);
    }

    @Transactional
    public PostDto updatePost(long postId, PostDto postDto, List<MultipartFile> files) {
        Post post = getPost(postId);
        postValidator.validateAccessAndContent(postDto);

        post.setContent(postDto.getContent());
        removeUnnecessaryResources(post, postDto);

        Post updatedPost = postRepository.save(post);

        PostDto resourcesAndGetPostDto = createResourcesAndGetPostDto(updatedPost, files);

        redisPostCacheService.savePost(postMapper.toDto(updatedPost));
        return resourcesAndGetPostDto;
    }

    @Transactional
    public void deletePost(long postId) {
        Post post = getPost(postId);
        postValidator.validateAccessToPost(post.getAuthorId(), post.getProjectId());
        post.setDeleted(true);

        redisPostCacheService.deletePostById(postId);
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

    @Transactional(readOnly = true)
    public PostDto getPostDto(long postId) {
        Post post = getPost(postId);
        postValidator.validateAccessToPost(post.getAuthorId(), post.getProjectId());
        return postMapper.toDto(post);
    }

    @Transactional
    public List<ResourceDto> addVideo(long postId, List<MultipartFile> files) {
        Post post = getPost(postId);
        int amount = post.getResources().size();

        List<MultipartFile> validFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            if (amount < maxVideo) {
                resourceValidator.videoIsValid(file);
                validFiles.add(file);
                amount++;
            }
        }
        return resourceService.addResources(postId, validFiles);
    }

    @Transactional
    public void deleteVideo(long postId, List<Long> resourceIds) {
        resourceService.deleteResources(postId, resourceIds);
    }


    private PostDto createResourcesAndGetPostDto(Post post, List<MultipartFile> files) {
        if (files == null) {
            return postMapper.toDto(post);
        }

        List<ResourceDto> savedResources = resourceService.createResources(post, files);
        List<ResourceDto> resourcesByPost = post.getResources().stream()
                .map(resourceMapper::toDto)
                .toList();

        List<ResourceDto> allResources = new ArrayList<>(resourcesByPost);
        allResources.addAll(savedResources);

        PostDto postDto = postMapper.toDto(post);
        postDto.setResourceIds(allResources.stream().map(ResourceDto::getId).toList());

        return postDto;
    }

    private void removeUnnecessaryResources(Post post, PostDto postDto) {
        List<Long> resourceIdsFromDto = Optional.ofNullable(postDto.getResourceIds())
                .orElse(new ArrayList<>());

        List<Resource> resourcesToDelete = post.getResources().stream()
                .filter(resource -> !resourceIdsFromDto.contains(resource.getId()))
                .toList();

        post.getResources().removeAll(resourcesToDelete);
        resourceService.deleteResources(post.getId(), resourcesToDelete.stream()
                .map(Resource::getId)
                .toList()
        );
    }

    public void checkAndBanAuthors() {
        postRepository.findAuthorIdsByNotVerifiedPosts(unverifiedPostsLimit).forEach(
                authorId -> {
                    log.debug("User with id = {} has more then {} unverified posts", authorId, unverifiedPostsLimit);
                    userBanEventPublisher.publish(new UserBanEventDto(authorId));
                }
        );
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

    public List<Post> getPosts (List<Long> authorIds, @Nullable Long fromPostId, int postQuantity) {
        LocalDateTime publishedAt = null;
        if (fromPostId != null) {
            publishedAt = getPost(fromPostId).getPublishedAt();
        }
        return postRepository.getNextPostsByAuthorIds(entityManager, authorIds, publishedAt, postQuantity);
    }
}