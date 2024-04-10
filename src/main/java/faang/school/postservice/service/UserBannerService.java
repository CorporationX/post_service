package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.UserBannerPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserBannerService {
    private final PostRepository postRepository;
    private final UserBannerPublisher userBannerPublisher;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "${scheduled.author-banner}")
    @Transactional
    public void banPosts() {
        List<Post> unverifiedPosts = postRepository.findAllByVerified(false);
        Map<Long, Long> unverifiedPostsByUsers = unverifiedPosts.stream()
                .collect(Collectors.groupingBy((Post::getAuthorId), Collectors.counting()));
        List<Long> userIdsToBan = unverifiedPostsByUsers.entrySet().stream()
                .filter((entry) -> entry.getValue() >= 5)
                .map(Map.Entry::getKey)
                .toList();

        if (!userIdsToBan.isEmpty()) {
            try {
                String message = objectMapper.writeValueAsString(userIdsToBan);
                userBannerPublisher.publish(message);
            } catch (JsonProcessingException e) {
                log.warn("Object mapping of a list is not successful", e);
                throw new RuntimeException(e);
            }
            postRepository.deleteAllByAuthorIdIn(userIdsToBan);

        }
        log.info("Event Published!");
    }
}
