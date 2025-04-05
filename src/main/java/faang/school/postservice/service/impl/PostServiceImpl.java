package faang.school.postservice.service.impl;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.ExternalServiceValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.gateway.ProjectServiceGateway;
import faang.school.postservice.gateway.UserServiceGateway;
import faang.school.postservice.kafka.producer.KafkaPostProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Identifiable;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import faang.school.postservice.repository.cache.AuthorCacheRepository;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.AIService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.controller.ControllerExceptionHandler.DEFAULT_SERVICE_NAME;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    public static final String POST_WITH_ID_NOT_FOUND = "Post with id %s not found";
    public static final String POST_WITH_ID_ALREADY_PUBLISHED = "Post with ID %s is already published";
    public static final String POSTS_MUST_HAVE_ONE_AUTHOR = "Post must have exactly one author (either user or project).";
    public static final long FEED_BATCH = 20L;

    private final PostRepository postRepository;
    private final ProjectServiceGateway projectServiceGateway;
    private final UserServiceGateway userServiceGateway;
    private final PostMapper postMapper;
    private final AIService aiService;
    private final PostCacheRepository postCacheRepository;
    private final AuthorCacheRepository authorCacheRepository;
    private final FeedCacheRepository feedCacheRepository;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserRepository userRepository;
    private final UserContext userContext;

    @Override
    public List<PostDto> getFeed(PostDto postDto) {
        if (postDto == null) {
            return List.of();
        }
        if (postDto.getId() != null) {
            return List.of(getCachedPost(postDto.getId()));
        }

        Set<Long> postIds = feedCacheRepository.getPostEvents(userContext.getUserId(), FEED_BATCH);
        Set<PostDto> feed = postIds.stream()
                .map(id -> {
                    PostEvent event = postCacheRepository.getPost(id);
                    if (event == null) {
                        return postMapper.toDto(postRepository.findById(id).orElse(null));
                    } else {
                        return postMapper.toDto(event);
                    }

                })
                .collect(Collectors.toSet());

        if (feed.size() < FEED_BATCH) {
            feed.addAll(postRepository.findByAuthorIdInAndIdNotIn(
                    userRepository.findByFollowerId(userContext.getUserId()).stream()
                            .map(Identifiable::getId).toList(), new ArrayList<>(postIds),
                    Pageable.ofSize((int) (FEED_BATCH - feed.size())))
                    .stream()
                    .map(postMapper::toDto)
                    .toList()
            );
        }
        return new ArrayList<>(feed);
    }



    private PostDto getCachedPost(Long postId) {
        PostDto post = postMapper.toDto(postCacheRepository.getPost(postId));
        if (post == null) {
            post = postMapper.toDto(postRepository.findById(postId).orElseThrow(
                    () -> new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format(POST_WITH_ID_NOT_FOUND, postId))));
        }
        return post;
    }

    @Override
    public PostDto createDraft(PostDto postDto) {
        validateDraft(postDto);
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setPublished(false);
        Post post = postMapper.toEntity(postDto);
        if (postDto.getAuthorId() != null) {
            userServiceGateway.getUser(postDto.getAuthorId());
        } else if (postDto.getProjectId() != null) {
            projectServiceGateway.getProject(postDto.getProjectId());
        }

        cache(postDto, post);
        return savePostAndMapToDto(post);
    }

    private void cache(PostDto postDto, Post post) {
        postCacheRepository.cachePost(postMapper.toEvent(post));
        authorCacheRepository.cacheAuthor(postDto.getAuthorId());
        sendToKafka(postDto);
    }

    private void sendToKafka(PostDto postDto) {
        List<Long> ids = userRepository.findAllSubscribersById(postDto.getAuthorId());
        kafkaPostProducer.sendMessage(postDto.getId(), ids);
    }

    @Override
    public PostDto publish(Long postId) {
        return postRepository.findById(postId)
                .map(post -> {
                    if (post.isPublished()) {
                        throw new ExternalServiceValidationException(String.format(POST_WITH_ID_ALREADY_PUBLISHED, postId));
                    }
                    post.setPublishedAt(LocalDateTime.now());
                    post.setPublished(true);

                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format(POST_WITH_ID_NOT_FOUND, postId)));
    }

    @Override
    public PostDto update(Long id, String content) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setContent(content);
                    post.setUpdatedAt(LocalDateTime.now());

                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public PostDto softDelete(Long id) {
        return postRepository.findById(id)
                .map(post -> {
                    post.setDeleted(true);
                    return savePostAndMapToDto(post);
                })
                .orElseThrow(() -> new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public PostDto getById(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(() -> new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format(POST_WITH_ID_NOT_FOUND, id)));
    }

    @Override
    public List<PostDto> getNotDeletedDraftsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format("Drafts by user ID: %s not found", userId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format("Drafts by project ID: %s not found", projectId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format("Posts by user ID: %s not found", userId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        if (posts.isEmpty()) {
            throw new PostNotFoundException(DEFAULT_SERVICE_NAME, String.format("Posts by project ID: %s not found", projectId));
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, nullsLast(naturalOrder())))
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Async("aICheckExecutor")
    public void grammarCorrectionPost(Post post) {
        log.info("Processing grammar correction post {}", post.getId());
        try {
            post.setContent(aiService.checkGrammarPost(post));
        } catch (Exception e) {
            log.error("Error during grammar correction: {}", e.getMessage());
            return;
        }
        post.setAiChecked(true);
        postRepository.save(post);
        log.info("Finished grammar correction post {}", post.getId());
    }

    private void validateDraft(PostDto postDto) {
        if ((postDto.getAuthorId() == null && postDto.getProjectId() == null) ||
                (postDto.getAuthorId() != null && postDto.getProjectId() != null)) {
            throw new ExternalServiceValidationException(POSTS_MUST_HAVE_ONE_AUTHOR);
        }
    }

    private PostDto savePostAndMapToDto(Post post) {
        return postMapper.toDto(postRepository.save(post));
    }
}
