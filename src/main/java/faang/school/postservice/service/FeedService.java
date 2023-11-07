package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.*;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final PostMapper postMapper;

    public FeedDto getFeed(Long postId) {
        Long userId = getUser();
        RedisUser userById = redisUserRepository.findById(userId).orElseThrow();

        FeedDto feedDto = new FeedDto();
        feedDto.setPosts(new LinkedHashSet<>());
        feedDto.setRedisUser(userById);

        if (postId == null) {
            LinkedHashSet<PostPair> feedByUserId = getFeedByUserId(userById.getUserId());
            int postsFetched = 0;

            for (PostPair postPair : feedByUserId) {
                long pairPostId = postPair.getPostId();

                PostCacheDto postRedisDtoByPostId = getPostRedisDtoByPostId(pairPostId);
                feedDto.getPosts().add(postRedisDtoByPostId);
                 postsFetched++;

                if (postsFetched >= 20) {
                    break;
                }
            }

            if (postsFetched < 20) {
                List<Long> followees = userById.getUserDto().getFollowees();
                for (Long followee : followees) {
                    List<Post> postByAuthorId = postRepository.findByAuthorId(followee);
                    for (Post post : postByAuthorId) {
                        PostDto postDto = postMapper.toDto(post);
                        feedDto.getPosts().add(postMapper.toPostCacheDto(postDto));
                        postsFetched++;

                        if (postsFetched >= 20) {
                            break;
                        }
                    }
                }
            }

            log.info("Feed was successfully retrieved for user with ID: {}", userId);
            return feedDto;
        }

        return findPostById(postId, feedDto);
    }

    private FeedDto findPostById(Long postId, FeedDto feedDto) {
        PostCacheDto postCacheDto = getPostRedisDtoByPostId(postId);
        feedDto.getPosts().add(postCacheDto);

        if (postCacheDto == null) {
            Post postById = postRepository.findById(postId).orElseThrow();
            PostDto postDto = postMapper.toDto(postById);
            feedDto.getPosts().add(postMapper.toPostCacheDto(postDto));
        }

        log.info("Feed was successfully retrieved for user with ID: {}", getUser());
        return feedDto;
    }

    private PostCacheDto getPostRedisDtoByPostId(Long postId) {
        Optional<RedisPost> redisPost = redisPostRepository.findById(postId);
        return redisPost.get().getPostCacheDto();
    }

    private LinkedHashSet<PostPair> getFeedByUserId(Long userId) {
        RedisFeed redisFeed = redisFeedRepository.findById(userId).orElseThrow();
        return redisFeed.getPosts();
    }

    private Long getUser() {
        Long userId = userContext.getUserId();
        return userId;
    }
}

