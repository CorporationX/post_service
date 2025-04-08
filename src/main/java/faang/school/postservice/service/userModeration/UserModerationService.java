package faang.school.postservice.service.userModeration;

import faang.school.postservice.contants.InfoMessage;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserModerationService {
    private final PostRepository postRepository;
    private final UserBanPublisher userBanPublisher;

    public void checkAndBanUsersWithUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findByVerifiedFalse();

        Map<Long, List<Post>> userPostCounts = unverifiedPosts.stream()
                .collect(Collectors.groupingBy(Post::getAuthorId));

        userPostCounts.forEach((authorId, posts) -> {
            if(posts.size() > 5) {
                log.info(InfoMessage.INFO_BANNED_USER, authorId, posts.size());
                userBanPublisher.publishUserBan(authorId);
            }
        });
    }
}
