package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;

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
        Long userId = userContext.getUserId();
        PostCacheDto postCache = redisPostRepository.getPostById(postId);
        UserDto userById = redisUserRepository.getUserById(userId);

        LinkedHashSet<Long> postByUserId = redisFeedRepository.getPostByUserId(userId);
        if (postId == null) {
            FeedDto feedDto = new FeedDto();
            for (Long postIds : postByUserId) {
                PostCacheDto postCacheDto = redisPostRepository.getPostById(postIds);

                if (postByUserId.isEmpty()) {
                    Post postById = postRepository.findById(postIds).orElseThrow();
                    PostDto postDto = postMapper.toDto(postById);
                    feedDto.getPostCache().add(postMapper.toPostCacheDto(postDto));
                }

                feedDto.getPostCache().add(postCacheDto);
                feedDto.setUserDto(userById);
            }
            return feedDto;
        }

        FeedDto feedDto = new FeedDto();
        feedDto.getPostCache().add(postCache);
        feedDto.setUserDto(userById);
        return feedDto;
    }
}
