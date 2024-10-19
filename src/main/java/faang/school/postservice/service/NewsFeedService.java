package faang.school.postservice.service;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.NewsFeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.cache.NewsFeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsFeedService {
    private final NewsFeedCacheRepository newsFeedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${spring.data.redis.post.prefix}")
    private String postPrexif;
    @Value("${spring.data.redis.comment.prefix}")
    private String commentPrexif;
    @Value("${spring.data.redis.user.prefix}")
    private String userPrexif;
    @Value("${spring.data.redis.feed.prefix}")
    private String feedPrexif;

    @Value("${spring.data.redis.feed.posts-per-page}")
    private int postsPerPage;

    public List<PostRedis> getPostFromNewsFeed(long userId) {
        String cacheKey = feedPrexif + userId;
        if (!newsFeedCacheRepository.existsById(cacheKey)) {
            return Collections.emptyList();
        }
        List<String> postKeyIds = newsFeedCacheRepository.popPostsFromFeed(userId, postsPerPage);
        List<PostRedis> resultPosts = new ArrayList<>();

        for (String postKeyId : postKeyIds) {
            PostRedis post = postCacheRepository.findById(postKeyId)
                    .orElseGet(() -> {
                        Long parsedPostId = parsePostId(postKeyId);
                        return parsedPostId != null ? findByIdFromDb(parsedPostId) : null;
                    });
            postCacheRepository.incrementViews(postKeyId);
            if (post != null) {
                resultPosts.add(post);
            }
        }
        return resultPosts;
    }

    private PostRedis findByIdFromDb(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> {
            return new EntityNotFoundException("Couldn't find post in the repository");
        });
       return postMapper.toPostRedisFromEntity(post);
    }

    private Long parsePostId(String postId) {
        try {
            return Long.parseLong(postId.substring(postPrexif.length()));
        } catch (NumberFormatException e) {
            System.err.println("Couldn't make id from key: " + postId);
            return null;
        }
    }
}
