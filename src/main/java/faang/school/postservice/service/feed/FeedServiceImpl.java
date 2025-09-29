package faang.school.postservice.service.feed;

import faang.school.postservice.cache.author.AuthorCache;
import faang.school.postservice.cache.comment.PostCommentCache;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.config.properties.cache.comment.CommentCacheProperties;
import faang.school.postservice.config.properties.cache.feed.FeedProperties;
import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.cache.FeedCommentCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FeedServiceImpl implements FeedService {

    private final CommentCacheProperties commentProps;
    private final FeedProperties feedProperties;
    private final FeedCache feedCache;
    private final PostCache postCache;
    private final AuthorCache authorCache;
    private final PostRepository postRepository;
    private final FollowService followService;
    private final FeedMapper feedMapper;
    private final PostCommentCache postCommentCache;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<FeedPostDto> getFeedPage(long userId, Long afterPostId) {
        int pageSize = feedProperties.pageSize();

        List<Long> idsFromFeed = feedCache.getIds(userId, afterPostId, pageSize);
        if (idsFromFeed.isEmpty()) {
            return loadFromDb(userId, afterPostId, pageSize);
        }

        List<PostCacheDto> postsFromCache = postCache.getAll(idsFromFeed);
        List<FeedPostDto> page = assemble(postsFromCache);

        int remaining = pageSize - page.size();
        if (remaining > 0) {
            Long nextAfterId = page.isEmpty() ? afterPostId : page.get(page.size() - 1).id();
            List<FeedPostDto> more = loadFromDb(userId, nextAfterId, remaining);
            if (!more.isEmpty()) {
                page.addAll(more);
            }
        }

        return page;
    }


    private List<FeedPostDto> loadFromDb(long userId, Long afterPostId, int limit) {
        List<Long> following = followService.getAllFollowingAuthorIds(userId);
        if (following.isEmpty() || limit <= 0) return List.of();

        List<Post> posts = (afterPostId == null)
                ? postRepository.findRecentByAuthors(following, limit)
                : postRepository.findRecentByAuthorsAfterId(following, afterPostId, limit);
        if (posts.isEmpty()) return List.of();

        List<PostCacheDto> cacheDtos = feedMapper.toPostCacheEntryList(posts);
        postCache.putAll(cacheDtos);

        addToUserFeed(userId, posts);

        return assemble(cacheDtos);
    }

    private void addToUserFeed(long userId, List<Post> posts) {
        List<Long> ids = new ArrayList<>(posts.size());
        List<Instant> scores = new ArrayList<>(posts.size());
        for (Post post : posts) {
            ids.add(post.getId());
            scores.add(postScore(post));
        }
        feedCache.addAllPostsForUser(userId, ids, scores);
    }

    private List<FeedPostDto> assemble(List<PostCacheDto> entries) {
        List<Long> authorIds = feedMapper.toAuthorIds(entries);

        List<AuthorCacheDto> authors = authorCache.getAll(authorIds);

        List<FeedPostDto> out = new ArrayList<>(entries.size());
        for (int i = 0; i < entries.size(); i++) {
            PostCacheDto e = entries.get(i);
            if (e == null) continue;

            FeedPostDto dto = feedMapper.toFeedPostDto(e);

            AuthorCacheDto author = (i < authors.size()) ? authors.get(i) : null;
            if (author != null) {
                dto = feedMapper.attachAuthor(dto, feedMapper.toFeedAuthorDto(author));
            }

            List<FeedCommentDto> comments = loadLastComments(e.id());
            dto = feedMapper.attachComments(dto, comments);

            out.add(dto);
        }
        return out;
    }

    private List<FeedCommentDto> loadLastComments(Long postId) {
        List<FeedCommentCacheDto> cached = postCommentCache.getLast(postId, commentProps.maxSize());
        if (!cached.isEmpty()) {
            return feedMapper.toFeedCommentDtosFromCache(cached);
        }
        int safe = Math.max(1, commentProps.maxSize());
        List<Comment> last = commentRepository.findLatestByPostId(postId, safe);
        return commentMapper.toFeedCommentDtos(last);
    }

    private static Instant postScore(Post post) {
        if (post.getPublishedAt() != null) {
            return post.getPublishedAt().toInstant(ZoneOffset.UTC);
        }
        if (post.getCreatedAt() != null) {
            return post.getCreatedAt().toInstant(ZoneOffset.UTC);
        }
        return Instant.EPOCH;
    }
}