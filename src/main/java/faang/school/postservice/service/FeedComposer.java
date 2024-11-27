package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.mapper.post.CacheablePostMapper;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.repository.UserCacheRepository;
import faang.school.postservice.repository.UserRepository;
import faang.school.postservice.repository.post.PostCacheRepository;
import faang.school.postservice.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedComposer {
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CacheablePostMapper cacheablePostMapper;
    private final UserMapper userMapper;
    private final UserCacheRepository userCacheRepository;

    @Transactional
    public List<PostForFeedDto> getPostForFeed(List<Long> postIds) {
        List<CacheablePost> cacheablePosts = postIds.stream()
                .map(id -> postCacheRepository.findById(id)
                        .orElse(cacheablePostMapper.toCacheablePost(postRepository.getReferenceById(id))))
                .toList();

        List<PostForFeedDto> posts = new ArrayList<>();
        for (CacheablePost post : cacheablePosts) {
            PostForFeedDto postForFeedDto = cacheablePostMapper.toPostForFeedDto(post);
            postForFeedDto.setAuthor(
                    userMapper.toUserForFeedDto(
                            userCacheRepository.findById(post.getAuthorId())
                                    .orElse(userMapper.toCacheable(userRepository.getReferenceById(post.getAuthorId())))
                    )
            );
            posts.add(postForFeedDto);
        }

        return posts;
    }
}
