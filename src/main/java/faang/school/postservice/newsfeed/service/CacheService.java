package faang.school.postservice.newsfeed.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.newsfeed.dto.PostCacheDto;
import faang.school.postservice.newsfeed.dto.UserCacheDto;
import faang.school.postservice.newsfeed.repository.PostCacheRepository;
import faang.school.postservice.newsfeed.repository.UsersCacheRepository;
import faang.school.postservice.newsfeed.util.mapping.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final UsersCacheRepository usersCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    @Async("newsFeedExecutor")
    public void findAndPutUserToCache(Long authorId) {
        UserCacheDto userForFeed = userServiceClient.getUserForFeed(authorId);
        usersCacheRepository.putUser(userForFeed);
    }

    @Async("newsFeedExecutor")
    public void putPostToCache(PostOutputDto postOutputDto) {
        PostCacheDto postCacheDto = postMapper.toPostCacheDto(postOutputDto);
        postCacheRepository.putPost(postCacheDto);
    }
}
