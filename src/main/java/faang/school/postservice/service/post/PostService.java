package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentForFeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.producer.KafkaPostEventProducer;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static faang.school.postservice.exception.message.PostOperationExceptionMessage.RE_DELETING_POST_EXCEPTION;
import static faang.school.postservice.exception.message.PostValidationExceptionMessage.NON_EXISTING_POST_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final PostVerifier postVerifier;
    private final KafkaPostEventProducer kafkaPostEventProducer;
    private final RedisPostCache postCache;


    @Transactional
    public PostDto createPost(@Valid PostDto postDto) {
        postVerifier.verifyAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postDraft = postMapper.toEntity(postDto);
        postDraft.setPublished(false);
        postDraft.setDeleted(false);

        return postMapper.toDto(postRepository.save(postDraft));
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post postToBePublished = getPost(postId);

        postVerifier.verifyIsPublished(postToBePublished);

        postToBePublished.setPublished(true);
        postToBePublished.setPublishedAt(LocalDateTime.now());

        Post publishedPost = postRepository.save(postToBePublished);
        PostDto publishedPostDto = postMapper.toDto(publishedPost);

        handlePostPublication(publishedPostDto);

        return publishedPostDto;
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        postVerifier.verifyAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postToBeUpdated = getPost(postDto.getId());

        postVerifier.verifyPostMatchingSystem(postDto, postToBeUpdated);

        postToBeUpdated.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(postToBeUpdated));
    }

    @Transactional
    public Long incrementPostViews(Long postId) {
        Post post = getPost(postId);

        if (!post.isPublished()) {
            return 0L;
        }

        post.incrementViews();
        return postRepository.save(post).getViews();
    }

    @Transactional
    public void updatePosts(List<Post> posts) {
        postRepository.saveAll(posts);
    }

    @Transactional
    public void deletePost(long postId) {
        Post postToBeDeleted = getPost(postId);

        if (postToBeDeleted.isDeleted()) {
            throw new DataValidationException(RE_DELETING_POST_EXCEPTION.getMessage());
        }

        postToBeDeleted.setDeleted(true);

        postMapper.toDto(postRepository.save(postToBeDeleted));
    }

    @Transactional(readOnly = true)
    public PostDto getPostById(long postId) {
        return postMapper.toDto(getPost(postId));
    }

    @Transactional(readOnly = true)
    public List<Post> getAllDrafts() {
        return postRepository.findAllDrafts();
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsOfUser(long userId) {
        postVerifier.verifyUserExistence(userId);

        return getSortedDrafts(postRepository.findByAuthorId(userId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getDraftsOfProject(long projectId) {
        postVerifier.verifyProjectExistence(projectId);

        return getSortedDrafts(postRepository.findByProjectId(projectId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsOfUser(long userId) {
        postVerifier.verifyUserExistence(userId);

        return getSortedPosts(postRepository.findByAuthorId(userId));
    }

    /**
     * @param userId        user whose feed will be returned
     * @param batchSize     how much posts method will return
     * @param postPointerId returned posts should be published before this post
     * @return batch of posts dtos
     */
    @Transactional(readOnly = true)
    public List<PostForFeedDto> getFeedForUser(Long userId, int batchSize, Optional<Long> postPointerId) {
        List<Long> userSubscriptions = userServiceClient.getFollowingIds(userId);

        final List<Post> postsBatch = new ArrayList<>();
        postPointerId.ifPresentOrElse(
                pointer -> postsBatch.addAll(postRepository.getFeedForUser(userSubscriptions, pointer, batchSize)),
                () -> postsBatch.addAll(postRepository.getFeedForUser(userSubscriptions, batchSize))
        );

        return postsBatch.stream()
                .map(
                        post -> PostForFeedDto.builder()
                                .postId(post.getId())
                                .postAuthorId(post.getAuthorId())
                                .content(post.getContent())
                                .likesList(likeMapper.toDto(post.getLikes()))
                                .comments(getCommentsForFeed(post))
                                .viewsCounter(0L)
                                .build()
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostDto> getPostsOfProject(long projectId) {
        postVerifier.verifyProjectExistence(projectId);

        return getSortedPosts(postRepository.findByProjectId(projectId));
    }

    private void handlePostPublication(PostDto publishedPost) {
        kafkaPostEventProducer.sendPostEvent(publishedPost);

        PostForFeedDto postForFeedDto = PostForFeedDto.builder()
                .postId(publishedPost.getId())
                .postAuthorId(publishedPost.getAuthorId())
                .content(publishedPost.getContent())
                .publishedAt(publishedPost.getPublishedAt())
                .viewsCounter(0L)
                .build();

        postCache.save(postForFeedDto);
    }

    private LinkedHashSet<CommentForFeedDto> getCommentsForFeed(Post post) {
        Map<Long, UserDto> authors = getCommentsAuthors(post);

        List<CommentForFeedDto> commentDtos = commentMapper.toDto(post.getComments()).stream()
                .map(comment -> new CommentForFeedDto(comment, authors.get(comment.getAuthorId())))
                .toList();
        return new LinkedHashSet<>(commentDtos);
    }

    private Map<Long, UserDto> getCommentsAuthors(Post post) {
        List<Long> authorsIds = post.getComments().stream()
                .map(Comment::getAuthorId)
                .toList();

        if (authorsIds.size() == 0) {
            return new HashMap<>();
        }

        return userServiceClient.getUsersByIds(authorsIds).stream()
                .collect(Collectors.toMap(UserDto::getId, Function.identity()));
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_POST_EXCEPTION.getMessage()));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPosts(List<Post> posts) {
        return posts.stream()
                .filter(Post::isPublished)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }
}
