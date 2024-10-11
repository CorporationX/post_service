package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.redis.PostCacheService;
import faang.school.postservice.service.redis.UserCacheService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeater {
    @Value("${news-feed.heat.post_amount}")
    private int postAmount;
    @Value(value = "${news-feed.cache.suffix.feed}")
    private String feedSuffix;
    @Value(value = "${news-feed.cache.suffix.post}")
    private String postSuffix;
    @Value(value = "${news-feed.cache.suffix.user}")
    private String userSuffix;
    private final RedissonClient redissonClient;
    private final UserServiceClient userServiceClient;
    private final UserCacheService userCacheService;
    private final PostCacheService postCacheService;
    public void generateFeed(){
        TreeSet<FeedDto> feeds = new TreeSet<>(Comparator.comparing(f -> f.getPostInfo().getUpdatedAt()));

        log.info("Generating feed...");
        List<UserDto> listUserDto = userServiceClient.getAllUsers();
//        List<> followers = userServiceClient.getFollowers(id);
//        listUserDto.get(0).get
    }

    private void fillRedisUserCache(){
        userCacheService.loadAndSaveAllUsers();
    }

    private void fillRedisPostCache(){

        postCacheService.findUserPosts(userId, postAmount);
    }

}
