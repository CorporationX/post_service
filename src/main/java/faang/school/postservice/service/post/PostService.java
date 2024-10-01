package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.dto.publishable.PostEvent;
import faang.school.postservice.dto.publishable.PostViewEvent;
import faang.school.postservice.dto.resource.PreviewPostResourceDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.dto.resource.UpdatableResourceDto;
import faang.school.postservice.dto.user.CachedUserDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.messages.ValidationExceptionMessage;
import faang.school.postservice.exception.post.UnexistentPostException;
import faang.school.postservice.exception.validation.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.mapper.post.ResourceMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.command.UpdatePostResourceCommand;
import faang.school.postservice.service.publisher.PostEventPublisher;
import faang.school.postservice.validator.post.PostServiceValidator;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ResourceService resourceService;

    private final UpdatePostResourceCommand updatePostResource;

    private final UserServiceClient userServiceClient;

    private final PostMapper postMapper;
    private final ResourceMapper resourceMapper;

    private final PostServiceValidator validator;
    private final PostEventPublisher postEventPublisher;

    private final KafkaPostProducer postProducer;
    private final KafkaPostViewProducer postViewProducer;

    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;

    @Transactional
    public PostDto createPostDraft(DraftPostDto draft) {

        validator.validateCreatablePostDraft(draft);

        PostDto postDto = postMapper.fromDraftPostDto(draft);
        Post post = postMapper.toEntity(postDto);

        Post savedPost = postRepository.save(post);

        List<ResourceDto> savedResources = resourceService.createResources(
                savedPost.getId(), draft.getResource()
        );

        PostDto savedPostDto = postMapper.toDto(savedPost);

        List<PreviewPostResourceDto> resourcePreviews = savedResources.stream()
                .map(resourceMapper::toPreviewPostResourceDto)
                .toList();

        savedPostDto.setResources(resourcePreviews);

        UserDto userDto = userServiceClient.getUser(savedPost.getAuthorId());
        PostEvent postEvent = PostEvent.builder()
                .postId(savedPostDto.getId())
                .authorId(savedPostDto.getAuthorId())
                .subscriberIds(userDto.getSubscriberIds())
                .build();


        CachedPostDto cachedPostDto = CachedPostDto.builder()
                .id(savedPostDto.getId())
                .authorId(savedPostDto.getAuthorId())
                .projectId(savedPostDto.getProjectId())
                .content(savedPostDto.getContent())
                .likesCount(savedPostDto.getLikesCount())
                .comments(new HashSet<>())
                .build();

        CachedUserDto cachedUserDto = CachedUserDto.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .subscriberIds(userDto.getSubscriberIds())
                .build();

        redisPostRepository.save(cachedPostDto.getId(),cachedPostDto);
        redisUserRepository.save(cachedUserDto.getId(), cachedUserDto);
        postProducer.sendEvent(postEvent);

        return savedPostDto;
    }

    @Transactional
    public PostDto publishPost(long postId) {

        Post post = getPost(postId);

        validator.validatePublishablePost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        PostEvent postEvent = new PostEvent(post.getAuthorId(), postId, List.of());
        postEventPublisher.publish(postEvent);

        return postMapper.toDto(savedPost);
    }

    private Post getPost(@NotNull Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new UnexistentPostException(postId)
        );
    }

    public CachedPostDto getPostFromCache(@NotNull Long postId) {
        return redisPostRepository.get(postId)
                .orElse(postMapper.toCachedPostDto(getPost(postId)));
    }

    @Transactional
    public PostDto updatePost(UpdatablePostDto updatablePost) {

        validator.validateUpdatablePost(updatablePost);

        Post post = getPost(updatablePost.getPostId());

        validator.verifyPostDeletion(post);

        if (updatablePost.getContent() != null) {
            post.setContent(updatablePost.getContent());
        }

        if (!post.isPublished()) {

            if (updatablePost.getScheduledAt() != null) {
                post.setScheduledAt(updatablePost.getScheduledAt());
            }

            if (updatablePost.isDeleteScheduledAt()) {
                post.setScheduledAt(null);
            }

        }

        List<UpdatableResourceDto> resource = updatablePost.getResource();
        boolean areResourceUpdated = resource != null && !resource.isEmpty();

        if (areResourceUpdated) {
            updatePostResource.execute(
                    updatablePost.getPostId(),
                    resource
            );
        }

        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        return postMapper.toDto(savedPost);
    }

    @Transactional
    public void deletePost(long postId) {

        Post post = getPost(postId);

        validator.validateDeletablePost(post);

        post.setDeleted(true);

        postRepository.save(post);
    }

    @Transactional
    public PostDto findPost(long postId) {

        Post post = getPost(postId);

        validator.verifyPostDeletion(post);
        post.setViews(post.getViews() + 1);

        PostViewEvent postViewEvent = new PostViewEvent(postId);
        postViewProducer.sendEvent(postViewEvent);

        postRepository.save(post);
        return postMapper.toDto(post);
    }


    public List<CachedPostDto> getPostsByAuthorIds(List<Long> authorIds, long startPostId, int batchSize) {
        List<Post> posts = postRepository.findPostsByAuthorIds(authorIds, startPostId, PageRequest.of(0, batchSize));
        return postMapper.toCachedPostDtoList(posts);
    }


    @Transactional(readOnly = true)
    public List<PostDto> getPosts(@NotNull GetPostsDto getPostsDto) {

        validator.validatePostPublisher(
                getPostsDto.getAuthorId(),
                getPostsDto.getProjectId()
        );

        Long authorId = getPostsDto.getAuthorId();
        Long projectId = getPostsDto.getProjectId();

        return switch (getPostsDto.getStatus()) {
            case POST -> {
                if (authorId != null) {
                    yield findAllPublishedAuthorPosts(authorId);
                } else if (projectId != null) {
                    yield findAllPublishedProjectPosts(projectId);
                } else {
                    throw new DataValidationException(ValidationExceptionMessage.POST_WITHOUT_PUBLISHER);
                }
            }
            case DRAFT -> {
                if (authorId != null) {
                    yield findAllAuthorDrafts(authorId);
                } else if (projectId != null) {
                    yield findAllProjectDrafts(projectId);
                } else {
                    throw new DataValidationException(ValidationExceptionMessage.POST_WITHOUT_PUBLISHER);
                }
            }
        };
    }

    private List<PostDto> findAllPublishedAuthorPosts(long authorId) {

        return sortAndFilterPosts(
                () -> postRepository.findByAuthorId(authorId),
                (var p) -> !p.isDeleted() && p.isPublished(),
                Comparator.comparing(Post::getCreatedAt).reversed()
        );
    }

    private List<PostDto> findAllPublishedProjectPosts(long projectId) {

        return sortAndFilterPosts(
                () -> postRepository.findByProjectId(projectId),
                (var p) -> !p.isDeleted() && p.isPublished(),
                Comparator.comparing(Post::getCreatedAt).reversed()
        );
    }

    private List<PostDto> findAllAuthorDrafts(long authorId) {

        return sortAndFilterPosts(
                () -> postRepository.findByAuthorId(authorId),
                (var p) -> !p.isDeleted() && !p.isPublished(),
                Comparator.comparing(Post::getCreatedAt).reversed()
        );
    }

    private List<PostDto> findAllProjectDrafts(long projectId) {

        return sortAndFilterPosts(
                () -> postRepository.findByProjectId(projectId),
                (var p) -> !p.isDeleted() && !p.isPublished(),
                Comparator.comparing(Post::getCreatedAt).reversed()
        );
    }

    private List<PostDto> sortAndFilterPosts(
            Supplier<List<Post>> postProvider,
            Predicate<Post> filter,
            Comparator<Post> comparator
    ) {
        var posts = postProvider.get();

        return posts.stream()
                .filter(filter)
                .sorted(comparator)
                .map(postMapper::toDto)
                .toList();

    }
}
