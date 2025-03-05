package faang.school.postservice.service.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.dto.feed.PostDTO;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.service.cash.CommentCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final CommentCacheService commentCacheService;
    private final UserFeedZSetService userFeedZSetService;
    private final PostMapper postMapper;

    @Value("${spring.data.cache.feed.feed-size}")
    private int feedSize;

    @Value("${spring.data.cache.feed.max-page-size}")
    private int maxPageSize;

    public FeedResponse getFeed(Long userId, Long lastPostId, int pageSize) {
        pageSize = validateAndAdjustPageSize(pageSize);
        List<Long> postIds = getPostIdsForFeed(userId, lastPostId, pageSize);
        List<PostDTO> posts = fetchAndMapPosts(postIds);
        return buildFeedResponse(posts, pageSize);
    }

    private int validateAndAdjustPageSize(int pageSize) {
        return Math.min(pageSize, maxPageSize);
    }

    private List<Long> getPostIdsForFeed(Long userId, Long lastPostId, int pageSize) {
        List<Long> postIds = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        if (postIds.isEmpty()) {
            loadUserFeedFromDatabase(userId);
            postIds = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        }
        return postIds;
    }

    protected void loadUserFeedFromDatabase(Long userId) {
        postRepository.findLatestPostsForUser(userId, PageRequest.of(0, feedSize))
                .stream()
                .filter(Post::canBeAddedToFeed)
                .forEach(post -> userFeedZSetService.addPostToFeed(
                        userId, post.getId(), post.getCreatedAt()));
    }

    private List<PostDTO> fetchAndMapPosts(List<Long> postIds) {
        return postIds.stream()
                .map(this::getPost)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<PostDTO> getPost(Long postId) {
        return getCachedPost(postId)
                .or(() -> getDatabasePost(postId));
    }

    private Optional<PostDTO> getCachedPost(Long postId) {
        return postCacheRepository.findById(postId)
                .flatMap(post -> {
                    Optional<Post> dbPostOpt = postRepository.findById(postId)
                            .filter(Post::canBeAddedToFeed);

                    return dbPostOpt.isPresent() ? Optional.of(mapToDTO(post)) : Optional.empty();
                });
    }

    private Optional<PostDTO> getDatabasePost(Long postId) {
        return postRepository.findById(postId)
                .filter(Post::canBeAddedToFeed)
                .map(post -> mapToDTO(cachePost(post)));
    }

    private PostCache cachePost(Post post) {
        PostCache postCache = postMapper.toPostCache(post);
        postCache.setLastComments(commentCacheService.fetchLatestComments(post.getId()));
        return postCacheRepository.save(postCache);
    }

    private PostDTO mapToDTO(PostCache post) {
        PostDTO dto = postMapper.postCacheToPostDTO(post);
        if (dto.getLastComments() == null) {
            dto.setLastComments(mapComments(post.getLastComments()));
        }
        return dto;
    }

    private List<CommentDto> mapComments(LinkedHashSet<CommentCache> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(this::mapCommentToDTO)
                .collect(Collectors.toList());
    }

    private CommentDto mapCommentToDTO(CommentCache comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .postId(comment.getPostId())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private FeedResponse buildFeedResponse(List<PostDTO> posts, int pageSize) {
        return FeedResponse.builder()
                .posts(posts)
                .hasMore(posts.size() >= pageSize)
                .lastPostId(posts.isEmpty() ? null : posts.get(posts.size() - 1).getId())
                .build();
    }
}