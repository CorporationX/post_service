package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorBannerService{
    private final PostRepository postRepository;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;


    @Scheduled(cron = "${scheduled.author-banner}")
    public void banPosts(){
        List<Post> unverifiedPosts = postRepository.findAllByVerified(false);
        Map<Long, Long> unverifiedPostsByUsers =  unverifiedPosts.stream()
                .collect(Collectors.groupingBy((post -> post.getAuthorId()), Collectors.counting()));
        List<Long> userIdsToBan = unverifiedPostsByUsers.entrySet().stream()
                .filter((entry) -> entry.getValue() >= 5)
                .map(entry -> entry.getKey())
                .toList();
        if (!userIdsToBan.isEmpty()){
            redisTemplate.convertAndSend(channelTopic.getTopic(), List.of(1,2,3,45,6));
            redisTemplate.opsForList().leftPush(userIdsToBan, Integer.class);
        }
        log.info("Event Published!");
    }
}
