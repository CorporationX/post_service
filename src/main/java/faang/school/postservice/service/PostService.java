package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.redis.PostEventDto;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.publisher.UserBannerPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.async.PostAsyncService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostViewEventService postViewEventService;
    private final YandexSpellCorrectorService spellCorrectorService;
    private final PostMapper postMapper;
    private final PostAsyncService postAsyncService;
    private final UserContext userContext;
    private final PostEventPublisher postEventPublisher;
    private final UserBannerPublisher userBannerPublisher;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("Post with id " + postId + " not found"));
    }

    @Transactional
    public PostDto createPost(PostDto post) {
        ProjectDto project = null;
        UserDto user = null;

        if (post.getProjectId() != null) {
            project = projectServiceClient.getProject(post.getProjectId());
        } else if (post.getAuthorId() != null) {
            user = userServiceClient.getUser(post.getAuthorId());
        }

        postValidator.validatePostCreator(post, project, user);
        postValidator.validatePostContent(post);

        Post postEntity = postMapper.toPost(post);
        PostDto createdPost = postMapper.toDto(postRepository.save(postEntity));
        sendPostCreatedEvent(createdPost);
        return createdPost;
    }

    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validatePublishPost(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public PostDto updatePost(PostDto postUpdateDto) {
        Post post = getPostById(postUpdateDto.getId());
        postValidator.validationOfPostUpdate(postUpdateDto, post);

        Post updatedPost = postMapper.toPost(postUpdateDto);

        return postMapper.toDto(postRepository.save(updatedPost));
    }

    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        Post post = getPostById(postId);
        Long userId = userContext.getUserId();

        PostViewEventDto postViewEventDto = postViewEventService.getPostViewEventDto(userId, post);

        postViewEventService.publishEventToChannel(postViewEventDto);

        return postMapper.toDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByAuthorId(Long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(user);
        List<Post> draftsByAuthorId = postRepository.findDraftsByAuthorId(user.getId());
        return getSortedDrafts(draftsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedDraftsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> draftsByProjectId = postRepository.findDraftsByProjectId(project.getId());
        return getSortedDrafts(draftsByProjectId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByAuthorId(Long authorId) {
        UserDto authorDto = userServiceClient.getUser(authorId);
        postValidator.validateAuthor(authorDto);
        List<Post> publishedPostsByAuthorId = postRepository.findPublishedPostsByAuthorId(authorDto.getId());

        Long userId = userContext.getUserId();

        publishedPostsByAuthorId.forEach(post -> {
            PostViewEventDto postViewEventDto = postViewEventService.getPostViewEventDto(userId, post);

            postViewEventService.publishEventToChannel(postViewEventDto);
        });

        return getSortedPublishedPosts(publishedPostsByAuthorId);
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        postValidator.validateProject(project);
        List<Post> publishedPostsByProjectId = postRepository.findPublishedPostsByProjectId(project.getId());

        Long userId = userContext.getUserId();

        publishedPostsByProjectId.forEach(post -> {
            PostViewEventDto postViewEventDto = postViewEventService.getPostViewEventDto(userId, post);

            postViewEventService.publishEventToChannel(postViewEventDto);
        });

        return getSortedPublishedPosts(publishedPostsByProjectId);
    }

    @Transactional
    public boolean softDeletePost(Long postId) {
        Post post = getPostById(postId);

        postValidator.validationOfPostDelete(post);

        post.setDeleted(true);
        postRepository.save(post);
        return true;
    }

    private List<PostDto> getSortedDrafts(List<Post> draftsByAuthorId) {
        return draftsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPublishedPosts(List<Post> publishedPostsByAuthorId) {
        return publishedPostsByAuthorId.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt))
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional
    public void publishScheduledPosts(int partitionSize) {
        log.info("Scheduled posts publishing started");
        List<Post> posts = postRepository.findReadyToPublish();
        log.info("Scheduled publication of posts in the amount: {}", posts.size());
        if (posts.size() > partitionSize) {
            List<List<Post>> partitions = ListUtils.partition(posts, posts.size() / partitionSize);
            partitions.forEach(postAsyncService::publishPosts);
        } else if (posts.size() > 0) {
            postAsyncService.publishPosts(posts);
            log.info("Scheduled posts publishing finished");
        }
    }

    @Transactional
    public void autoCorrectionSpelling() {
        log.info("Spelling correction started");
        List<Post> posts = postRepository.findReadyToSpellCorrection();

        if (posts.isEmpty()) {
            log.info("No posts for correction");
            return;
        }

        posts.forEach(this::getCorrectedPost);

        log.info("Corrected {} posts", posts.size());
    }

    @Transactional
    public PostDto manualCorrectionSpelling(Long postId) {
        Post post = getPostById(postId);
        Post correctedPost = getCorrectedPost(post);
        return postMapper.toDto(postRepository.save(correctedPost));
    }

    private Post getCorrectedPost(Post post) {
        String correctedText = spellCorrectorService.getCorrectedText(post.getContent());
        post.setContent(correctedText);
        post.setSpellCheckedAt(LocalDateTime.now());

        return post;
    }

    private void sendPostCreatedEvent(PostDto createdPost) {
        PostEventDto postCreateEventDto = PostEventDto.builder()
                .authorId(createdPost.getAuthorId())
                .postId(createdPost.getId())
                .build();
        postEventPublisher.publish(postCreateEventDto);
    }

    @Transactional(readOnly = true)
    public void publishAuthorBanner() {
        List<Post> posts = postRepository.findUnverifiedPosts();
        Map<Long, List<Post>> groupedPosts = posts.stream().collect(Collectors.groupingBy(Post::getAuthorId));

        groupedPosts.entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 5)
                .forEach(entry -> userBannerPublisher.publish(entry.getKey()));

    }
}