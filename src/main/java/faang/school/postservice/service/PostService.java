package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.postCorrector.AiResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityAlreadyExistException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import faang.school.postservice.util.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "postsCache")
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ModerationDictionary moderationDictionary;
    private final Integer batchSize;
    private final RedisPublisher redisPublisher;
    private final String userBannerChannel;
    private final Integer BAD_POSTS_MAX_COUNT = 5;
    private final RestTemplate restTemplate;
    private final String postCorrectorApiKey;
    private final String postCorrectorUrl;
    private final LikeRepository likeRepository;
    private final LikeEventPublisher likeEventPublisher;

    public PostService(PostRepository postRepository, ResponsePostMapper responsePostMapper,
                       UserServiceClient userServiceClient, ProjectServiceClient projectServiceClient,
                       ModerationDictionary moderationDictionary, @Value("${post.moderator.scheduler.batchSize}") Integer batchSize,
                       RedisPublisher redisPublisher, LikeRepository likeRepository, LikeEventPublisher likeEventPublisher,
                       @Value("${spring.data.redis.channels.user_banner_channel.name}") String userBannerChannel,
                       RestTemplate restTemplate, @Value("${post.corrector.api-key}") String postCorrectorApiKey,
                       @Value("${post.corrector.url}") String postCorrectorUrl) {
        this.postRepository = postRepository;
        this.responsePostMapper = responsePostMapper;
        this.userServiceClient = userServiceClient;
        this.projectServiceClient = projectServiceClient;
        this.moderationDictionary = moderationDictionary;
        this.batchSize = batchSize;
        this.redisPublisher = redisPublisher;
        this.userBannerChannel = userBannerChannel;
        this.restTemplate = restTemplate;
        this.postCorrectorApiKey = postCorrectorApiKey;
        this.postCorrectorUrl = postCorrectorUrl;
        this.likeRepository = likeRepository;
        this.likeEventPublisher = likeEventPublisher;
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());

    }

    @Transactional
    public ResponsePostDto publish(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        if (post.isPublished()) {
            throw new IllegalArgumentException("Can't publish already published post");
        }
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post has been deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto update(UpdatePostDto dto) {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }


    @Transactional(readOnly = true)
    public ResponsePostDto getById(Long id) {
        return responsePostMapper.toDto(
                postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Post is not found"))
        );
    }

    @Transactional
    public ResponsePostDto softDelete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setDeleted(true);

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto createDraft(CreatePostDto dto) {
        Post post = new Post();

        processOwner(dto, post);

        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);

        extractHashtagsWhileCreating(dto);

        return responsePostMapper.toDto(postRepository.save(post));
    }

    @Transactional
    public void verifyContent() {
        List<Post> posts = postRepository.findAllByVerifiedAtIsNull();
        List<List<Post>> grouped = new ArrayList<>();
        if (posts.size() > batchSize) {
            int i = 0;
            while (i < posts.size() / batchSize) {
                grouped.add(posts.subList(i, i + batchSize));
                i += batchSize;
            }
            if (i < posts.size()) {
                grouped.add(posts.subList(i, posts.size()));
            }
        } else {
            grouped.add(posts);
        }

        List<CompletableFuture<Void>> completableFutures = grouped.stream()
                .map(list -> CompletableFuture.runAsync(() -> verifySublist(list)))
                .toList();

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getPostsByHashtagOrderByDate(String hashtag) {
        return responsePostMapper.toDtoList(postRepository.findByHashtagOrderByDate("#" + hashtag));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hashtags", key = "#hashtag")
    public List<ResponsePostDto> getPostsByHashtagOrderByPopularity(String hashtag) {
        return responsePostMapper.toDtoList(postRepository.findByHashtagOrderByPopularity("#" + hashtag));
    }


    public void banForOffensiveContent() {
        List<Post> posts = postRepository.findAllByVerifiedFalseAndVerifiedAtIsNotNull();

        List<Long> userIds = posts.stream().map(Post::getAuthorId).toList();

        List<Long> bannedUsers = userServiceClient.getUsersByIds(userIds).stream()
                .filter(UserDto::isBanned)
                .map(UserDto::getId)
                .toList();

        Map<Long, List<Post>> groupedByAuthor = posts.stream()
                .filter(post -> !bannedUsers.contains(post.getAuthorId()))
                .collect(Collectors.groupingBy(Post::getAuthorId));
        groupedByAuthor.entrySet().stream()
                .filter(entry -> entry.getValue().size() > BAD_POSTS_MAX_COUNT)
                .map(Map.Entry::getKey)
                .toList()
                .forEach(authorId -> redisPublisher.publishMessage(userBannerChannel, String.valueOf(authorId)));
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id " + postId + " was not found!"));
    }

    @Async
    @Transactional
    public void correctPosts() {
        List<Post> posts = postRepository.findAllByPublishedFalseAndDeletedFalse();
        List<CompletableFuture<Void>> completableFutures = posts.stream()
                .map(post -> CompletableFuture.runAsync(() -> {
                            String content = post.getContent().replaceAll(" ", "+");
                            String url = postCorrectorUrl + content + "&language=en-GB&key=" + postCorrectorApiKey;
                            ResponseEntity<AiResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, null, AiResponseDto.class);
                            if (response.getStatusCode().equals(HttpStatusCode.valueOf(200))
                                    && Objects.requireNonNull(response.getBody()).getResponse().getCorrected() != null) {
                                post.setContent(Objects.requireNonNull(response.getBody()).getResponse().getCorrected());
                            }
                        })
                )
                .toList();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    @Transactional(readOnly = true)
    public boolean existById(long postId) {
        return postRepository.existsById(postId);
    }

    @Transactional(readOnly = true)
    public Long getAuthorId(long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found. Id: " + postId))
                .getAuthorId();
    }

    private void extractHashtagsWhileCreating(CreatePostDto createPostDto) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(createPostDto.getContent());

        while (matcher.find()) {
            hashtags.add(matcher.group());
        }

        createPostDto.setHashtags(hashtags);
    }

    private void verifySublist(List<Post> subList) {
        subList.forEach(post -> {
            post.setVerified(!moderationDictionary.containsBadWord(post.getContent()));
            post.setVerifiedAt(LocalDateTime.now());
        });
        postRepository.saveAll(subList);
    }

    private void processOwner(CreatePostDto dto, Post post) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null) {
            throw new IllegalArgumentException("Both AuthorId and ProjectId can't be not null");
        }
        if (dto.getAuthorId() != null) {
            UserDto userDto = Objects.requireNonNull(userServiceClient.getUser(dto.getAuthorId()));
            post.setAuthorId(userDto.getId());
        }
        if (dto.getProjectId() != null) {
            ProjectDto projectDto = Objects.requireNonNull(projectServiceClient.getProject(dto.getProjectId()));
            post.setProjectId(projectDto.getId());
        }
    }
    @Transactional
    public ResponsePostDto likePost(UpdatePostDto dto, Long user_id) {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Post is not found"));
        checkExistLikeToPost(post, user_id);
        Like newLike = Like.builder().post(post).comment(null).userId(user_id).build();
        likeRepository.save(newLike);

        LikeEventDto likeEvent = LikeEventDto.builder().postId(post.getId()).dateTime(LocalDateTime.now())
                .likeAuthorId(user_id).postAuthor(post.getAuthorId()).build();
        likeEventPublisher.publishMessage(likeEvent);

        return responsePostMapper.toDto(post);
    }

    private void checkExistLikeToPost(Post post, Long user_id){
        List<Like> likes = post.getLikes();
        likes.stream().filter(like -> like.getUserId().equals(user_id)).findFirst().ifPresent(like -> {
            throw new EntityAlreadyExistException(String.format("User with id %s already likes post with id %s", user_id, post.getId()));
        });
    }
}
