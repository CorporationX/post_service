package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.dto.UserBanEventDto;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.dto.kafka.KafkaPostViewEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ResourceMapper resourceMapper;
    private final ResourceService resourceService;
    private final UserBanEventPublisher userBanEventPublisher;
    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer postProducer;
    private final KafkaPostViewProducer postViewProducer;
    @Value("${post.rule.unverified_posts_limit}")
    private int unverifiedPostsLimit;

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
        postRepository.save(post);

        cachePost(postMapper.toDto(post));
        cachePostAuthor(post.getAuthorId());
        sendKafkaPostEvent(post);
        sendKafkaPostViewEvent(post);
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

    @Transactional
    public PostDto createPost(PostDto postDto, List<MultipartFile> files) {
        postValidator.validateAccessAndContent(postDto);

        Post savedPost = postRepository.save(postMapper.toEntity(postDto));

        return createResourcesAndGetPostDto(savedPost, files);
    }

    @Transactional
    public PostDto updatePost(long postId, PostDto postDto, List<MultipartFile> files) {
        Post post = getPost(postId);
        postValidator.validateAccessAndContent(postDto);

        post.setContent(postDto.getContent());
        removeUnnecessaryResources(post, postDto);

        Post updatedPost = postRepository.save(post);

        return createResourcesAndGetPostDto(updatedPost, files);
    }

    @Transactional(readOnly = true)
    public PostDto getPostDto(long postId) {
        Post post = getPost(postId);
        postValidator.validateAccessToPost(post.getAuthorId(), post.getProjectId());
        return postMapper.toDto(post);
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
        resourceService.deleteResources(resourcesToDelete.stream()
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

    private void cachePost(PostDto postDto) {
        redisPostRepository.save(redisPostMapper.toRedisPost(postDto));
    }

    private void cachePostAuthor(long authorId) {
        RedisUser postAuthor = redisUserMapper.toRedisUser(userServiceClient.getUser(authorId));
        redisUserRepository.save(postAuthor);
    }

    private void sendKafkaPostEvent(Post post) {
        List<Long> followersIds = userServiceClient.getFollowersIds(post.getAuthorId());
        KafkaPostEvent postEvent = KafkaPostEvent.builder()
                .postId(post.getId())
                .updatedAt(post.getUpdatedAt())
                .followersIds(followersIds)
                .build();
        postProducer.sendMessage(postEvent);
    }

    private void sendKafkaPostViewEvent(Post post) {
        KafkaPostViewEvent postViewEvent = KafkaPostViewEvent.builder()
                .postId(post.getId())
                .build();
        postViewProducer.sendMessage(postViewEvent);
    }
}
