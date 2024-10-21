package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import faang.school.postservice.repository.feed.RedisPostRepository;
import faang.school.postservice.repository.feed.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;
    private final CacheTransactionalService cacheTransactionalService;

    @Value("${data.redis.cache.feed.showLastComments}")
    private int showLastComments;

    public void savePost(PostDto postDto) {
        redisPostRepository.addNewPost(postDto);
    }

    @Async("feedExecutor")
    public void savePosts(List<PostDto> postDtos) {
        postDtos.forEach(this::savePost);
    }

    public void updatePost(PostDto postDto) {
        savePost(postDto);
        redisPostRepository.deleteCommentCounter(postDto.getId());
        redisPostRepository.deleteLikeCounter(postDto.getId());
    }

    public void addNewComment(Long postId, FeedCommentEvent event) {
        saveComment(postId, event);
        redisPostRepository.incrementComment(postId);
    }

    public void saveComment(Long postId, FeedCommentEvent event) {
        CommentDto commentDto = commentMapper.fromFeedCommentEventToDto(event);
        redisPostRepository.addComment(postId, commentDto);
    }

    @Async("feedExecutor")
    public void saveComments(Long postId, List<CommentDto> commentDtos) {
        commentDtos.forEach(commentDto -> redisPostRepository.addComment(postId, commentDto));
    }

    public void handlePostDeletion(Long postId) {
        redisPostRepository.deletePost(postId);
        redisPostRepository.deleteComments(postId);
        redisPostRepository.deleteCommentCounter(postId);
        redisPostRepository.deleteLikeCounter(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }

    public void deleteComment(Long postId, Long commentId) {
        redisPostRepository.deleteComment(postId, commentId);
        redisPostRepository.decrementComment(postId);
    }

    public void incrementLike(Long postId) {
        redisPostRepository.incrementLike(postId);
    }

    public void decrementLike(Long postId) {
        redisPostRepository.decrementLike(postId);
    }

    public void addUserToCache(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        redisUserRepository.save(userDto);
    }

    public Map<Long, List<CommentDto>> fetchComments(List<PostDto> postDtos) {
        Map<Long, List<CommentDto>> resultCommentsMap = new HashMap<>();
        Set<Long> missingCommentsPostIds = new HashSet<>();

        for (PostDto postDto : postDtos) {
            long postId = postDto.getId();
            long commentsCount = postDto.getCommentsCount();
            if (commentsCount > 0) {
                List<CommentDto> comments = redisPostRepository.getComments(postId);
                if (comments.size() < showLastComments && comments.size() < commentsCount) {
                    missingCommentsPostIds.add(postId);
                } else {
                    resultCommentsMap.put(postId, comments);
                }
            }
        }

        if (!missingCommentsPostIds.isEmpty()) {
            Map<Long, List<CommentDto>> missingComments = cacheTransactionalService.getCommentsFromDB(missingCommentsPostIds);
            missingComments.forEach(this::updateComments);
            resultCommentsMap.putAll(missingComments);
        }

        return resultCommentsMap;
    }

    private void updateComments(Long postId, List<CommentDto> comments) {
        comments.forEach(commentDto -> redisPostRepository.addComment(postId, commentDto));
    }

    public Map<Long, UserDto> fetchUsers(Set<Long> userIds) {
        Map<Long, UserDto> userMap = userIds.stream()
                .collect(Collectors.toMap(
                        authorId -> authorId,
                        redisUserRepository::get,
                        (existing, replacement) -> existing
                ));

        processMissingUsers(userMap);

        return userMap;
    }

    private void processMissingUsers(Map<Long, UserDto> actualUserMap) {
        List<Long> missingUserIds = actualUserMap.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .toList();

        if (!missingUserIds.isEmpty()) {
            List<UserDto> missingUsers = getUsersFromDB(missingUserIds);
            saveUsers(missingUsers);
            missingUsers.forEach(userDto -> actualUserMap.put(userDto.getId(), userDto));
        }
    }

    private List<UserDto> getUsersFromDB(List<Long> missingUserIds) {
        return userServiceClient.getUsersByIds(missingUserIds);
    }

    @Async("feedExecutor")
    public void saveUsers(List<UserDto> userDtos) {
        redisUserRepository.save(userDtos);
    }

    public List<PostDto> fetchPosts(List<Long> postIds) {
        List<PostDto> postDtos = postIds.stream()
                .map(this::getPostFromCache)
                .filter(Optional::isPresent)
                .map(optionalPostDto -> {
                    PostDto postDto = optionalPostDto.get();
                    updatePostCounters(postDto);
                    return postDto;
                })
                .toList();

        processMissingPosts(postIds, postDtos);

        return postDtos;
    }

    private Optional<PostDto> getPostFromCache(Long postId) {
        return Optional.ofNullable(redisPostRepository.getPost(postId));
    }

    private void updatePostCounters(PostDto postDto) {
        Long postId = postDto.getId();

        Long likesDelta = redisPostRepository.getLikesCounter(postId);
        Long commentsDelta = redisPostRepository.getCommentsCounter(postId);

        long totalLikes = postDto.getLikesCount() + (likesDelta != null ? likesDelta : 0);
        long totalComments = postDto.getCommentsCount() + (commentsDelta != null ? commentsDelta : 0);

        postDto.setLikesCount(Math.max(totalLikes, 0));
        postDto.setCommentsCount(Math.max(totalComments, 0));
    }

    private void processMissingPosts(List<Long> expectedPostIds, List<PostDto> postDtos) {
        Set<Long> actualPostIds = postDtos.stream().map(PostDto::getId).collect(Collectors.toSet());
        List<Long> missingPostIds = findMissingIds(actualPostIds, expectedPostIds);

        if (!missingPostIds.isEmpty()) {
            List<PostDto> missingPostDtosFromDB = cacheTransactionalService.getPostDtosFromDB(missingPostIds);
            processNonexistentPosts(missingPostIds, missingPostDtosFromDB);
            postDtos.addAll(missingPostDtosFromDB);
        }
    }

    private void processNonexistentPosts(List<Long> expectedIds, List<PostDto> actualPosts) {
        Set<Long> actualIds = actualPosts.stream().map(PostDto::getId).collect(Collectors.toSet());
        List<Long> missingIds = findMissingIds(actualIds, expectedIds);
        missingIds.forEach(id -> {
            handlePostDeletion(id);
            redisFeedRepository.deletePostFromAllFeeds(id);
        });
    }

    private List<Long> findMissingIds(Set<Long> actualUserIds, List<Long> expectedUserIds) {
        return expectedUserIds.stream()
                .filter(id -> !actualUserIds.contains(id))
                .toList();
    }
}
