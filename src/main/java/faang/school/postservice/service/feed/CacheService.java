package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import faang.school.postservice.repository.feed.RedisPostRepository;
import faang.school.postservice.repository.feed.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    @Value("${data.redis.cache.feed.showLastComments}")
    private int showLastComments;

    public void savePost(PostDto postDto) {
        redisPostRepository.addNewPost(postDto);
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
            Map<Long, List<CommentDto>> missingComments = getCommentsFromDB(missingCommentsPostIds);
            missingComments.forEach(this::updateComments);
            resultCommentsMap.putAll(missingComments);
        }

        return resultCommentsMap;
    }

    private Map<Long, List<CommentDto>> getCommentsFromDB(Set<Long> postIds) {
        List<Comment> comments = commentRepository.findLastsByPostId(postIds, showLastComments);

        return comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getPost().getId(),
                        Collectors.mapping(commentMapper::toDto, Collectors.toList())
                ));
    }

    private void updateComments(Long postId, List<CommentDto> comments) {
        comments.forEach(commentDto -> redisPostRepository.addComment(postId, commentDto));
    }

    public Map<Long, UserDto> fetchUsers(List<PostDto> postDtos) {
        Map<Long, UserDto> resultUsersMap = new HashMap<>();
        Set<Long> processedAuthorIds = new HashSet<>();
        for (PostDto postDto : postDtos) {
            long authorId = postDto.getAuthorId();
            if (!processedAuthorIds.contains(authorId)) {
                UserDto userDto = redisUserRepository.get(authorId);
                if (userDto != null) {
                    resultUsersMap.put(authorId, userDto);
                }
                processedAuthorIds.add(authorId);
            }
        }

        processMissingUsers(resultUsersMap, postDtos);

        return resultUsersMap;
    }

    private void processMissingUsers(Map<Long, UserDto> actualUserMap, List<PostDto> postDtos) {
        Set<Long> actualUserIds = new HashSet<>(actualUserMap.keySet());
        List<Long> expectedUserIds = postDtos.stream().map(PostDto::getAuthorId).toList();
        List<Long> missingUserIds = findMissingIds(actualUserIds, expectedUserIds);

        if (!missingUserIds.isEmpty()) {
            List<UserDto> missingUsers = getUsersFromDB(missingUserIds);
            updateUsers(missingUsers);
            missingUsers.forEach(userDto -> actualUserMap.put(userDto.getId(), userDto));
        }
    }

    private List<UserDto> getUsersFromDB(List<Long> missingUserIds) {
        return userServiceClient.getUsersByIds(missingUserIds.stream().toList());
    }

    private void updateUsers(List<UserDto> missingUsers) {
        redisUserRepository.save(missingUsers);
    }

    public List<PostDto> fetchPosts(List<Long> postIds) {
        List<PostDto> postDtos = new ArrayList<>();
        for (Long postId : postIds) {
            Optional<PostDto> postFromCache = getPostFromCache(postId);

            if (postFromCache.isPresent()) {
                PostDto postDto = postFromCache.get();
                updatePostCounters(postDto);
                postDtos.add(postDto);
            }
        }

        processMissingPosts(postIds, postDtos);

        return postDtos;
    }

    private Optional<PostDto> getPostFromCache(Long postId) {
        return Optional.of(redisPostRepository.getPost(postId));
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
            List<PostDto> missingPostDtosFromDB = getPostDtosFromDB(missingPostIds);
            processNonexistentPosts(missingPostIds, missingPostDtosFromDB);
            postDtos.addAll(missingPostDtosFromDB);
        }
    }

    private List<PostDto> getPostDtosFromDB(List<Long> postsIds) {
        Iterable<Post> missingPosts = postRepository.findAllById(postsIds);
        List<Post> posts = new ArrayList<>();
        missingPosts.forEach(posts::add);

        for (Post post : posts) {
            if (post.isDeleted()) {
                log.info("Post with ID {} was found in DB but it was deleted", post.getId());
                handlePostDeletion(post.getId());
                posts.remove(post);
            }
        }

        return posts.stream().map(postMapper::toDto).toList();
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