package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostForFeed;
import faang.school.postservice.dto.hash.AuthorType;
import faang.school.postservice.dto.hash.FeedHash;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.AuthorRedisRepository;
import faang.school.postservice.repository.redis.FeedRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRedisRepository feedRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final AuthorRedisRepository authorRedisRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;

    public List<PostForFeed> getFeed(long userId, Long lastPostId) {
        FeedHash feedHash = getFeedHash(userId);
        List<Long> twentyPostsId = feedHash
                .getNextTwentyPostsIdByLastPostId(lastPostId);
        return createPostsForFeed(twentyPostsId);
    }

    public List<PostForFeed> createPostsForFeed(List<Long> postsId) {
        List<PostForFeed> postsForFeed = new ArrayList<>();
        for (Long postId : postsId) {
            PostHash postHash = getPostHash(postId);
            if (postHash != null) {
                PostForFeed postForFeed = createPostFromHash(postHash);
                postsForFeed.add(postForFeed);
            } else {
                Post post = getPost(postId);
                if (post != null) {
                    PostForFeed postForFeed = createPostFromBD(post);
                    postsForFeed.add(postForFeed);
                }
            }
        }
        return postsForFeed;
    }

    public PostForFeed createPostFromHash(PostHash postHash) {
        return PostForFeed.builder()
                .id(postHash.getId())
                .content(postHash.getContent())
                .authorId(postHash.getAuthorId())
                .userDto(getAuthorDto(
                        postHash.getId(),
                        AuthorType.POST_AUTHOR))
                .projectId(postHash.getProjectId())
                .publishedAt(postHash.getPublishedAt())
                .updatedAt(postHash.getUpdatedAt())
                .comments(postHash.getComments())
                .likes(postHash.getLikes())
                .views(postHash.getViews())
                .build();
    }

    public PostForFeed createPostFromBD(Post post) {
        return PostForFeed.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .userDto(getAuthorDto(
                        post.getId(),
                        AuthorType.POST_AUTHOR))
                .projectId(post.getProjectId())
                .publishedAt(post.getPublishedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(getCommentDto(post.getComments()))
                .likes(getLikeDto(post.getLikes()))
                .views(getViewer(
                        post.getComments(),
                        post.getLikes()))
                .build();
    }

    private FeedHash getFeedHash(long userId) {
        return feedRedisRepository.findById(userId).orElseThrow(() ->
                new DataValidationException("Feed not found from Redis: " + userId));
    }

    private PostHash getPostHash(long postId) {
        Optional<PostHash> postHash = postRedisRepository.findById(postId);
        return postHash.orElse(null);
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new DataValidationException("Post not found from BD: " + postId));
    }

    private UserDto getAuthorDto(long userId, AuthorType authorType) {
        return authorRedisRepository.findByIdAndAuthorType(userId, authorType).orElseThrow(() ->
                new DataValidationException("Author not found from Redis: " + userId)).getUserDto();
    }

    private ConcurrentLinkedDeque<CommentDto> getCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
    }

    private ConcurrentLinkedDeque<LikeDto> getLikeDto(List<Like> likes) {
        return likes.stream()
                .map(likeMapper::toDto)
                .collect(Collectors.toCollection(ConcurrentLinkedDeque::new));
    }

    private ConcurrentLinkedDeque<UserDto> getViewer(
            List<Comment> comments, List<Like> likes) {
        ConcurrentLinkedDeque<UserDto> views = new ConcurrentLinkedDeque<>();
        views.addAll(userServiceClient.getUsersByIds(
                comments.stream()
                        .map(Comment::getAuthorId)
                        .toList()));
        views.addAll(userServiceClient.getUsersByIds(
                likes.stream()
                        .map(Like::getUserId)
                        .toList()));
        return views;
    }
}

