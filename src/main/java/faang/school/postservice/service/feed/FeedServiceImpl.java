package faang.school.postservice.service.feed;

import faang.school.postservice.cache.author.AuthorCache;
import faang.school.postservice.cache.feed.FeedCache;
import faang.school.postservice.cache.post.PostCache;
import faang.school.postservice.config.properties.cache.feed.FeedProperties;
import faang.school.postservice.dto.cache.AuthorCacheDto;
import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.follow.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedProperties feedProperties;
    private final FeedCache feedCache;
    private final PostCache postCache;
    private final AuthorCache authorCache;
    private final PostRepository postRepository;
    private final FollowService followService;
    private final FeedMapper feedMapper;

    @Override
    public List<FeedPostDto> getFeedPage(long userId, Long afterPostId) {
        int pageSize = feedProperties.pageSize();

        List<Long> idsFromFeed = feedCache.getIds(userId, afterPostId, pageSize);

        List<PostCacheDto> entries = postCache.getAll(idsFromFeed);

        List<FeedPostDto> page = mapWithAuthors(entries);

        int remaining = pageSize - page.size();
        if (remaining <= 0) {
            return page;
        }

        Long cursorId = !page.isEmpty() ? page.get(page.size() - 1).id() : afterPostId;
        List<Long> followingAuthorIds = followService.getAllFollowingAuthorIds(userId);
        if (followingAuthorIds.isEmpty()) return page;

        List<Post> postsFromDb = (cursorId == null)
                ? postRepository.findRecentByAuthors(followingAuthorIds, remaining)
                : postRepository.findRecentByAuthorsAfterId(followingAuthorIds, cursorId, remaining);

        if (!postsFromDb.isEmpty()) {
            postsFromDb.forEach(post -> postCache.put(feedMapper.toPostCacheEntry(post)));
            List<Long> idsFromDb = postsFromDb.stream().map(Post::getId).toList();
            List<PostCacheDto> extraEntries = postCache.getAll(idsFromDb);
            page.addAll(mapWithAuthors(extraEntries));
        }

        return page;
    }

    private List<FeedPostDto> mapWithAuthors(List<PostCacheDto> entries) {
        List<FeedPostDto> result = new ArrayList<>(entries.size());
        for (PostCacheDto entry : entries) {
            if (entry == null) continue;
            FeedPostDto dto = feedMapper.toFeedPostDto(entry);
            AuthorCacheDto author = authorCache.get(entry.authorId());
            if (author != null) {
                dto = feedMapper.attachAuthor(dto, feedMapper.toFeedAuthorDto(author));
            }
            result.add(dto);
        }
        return result;
    }
}
