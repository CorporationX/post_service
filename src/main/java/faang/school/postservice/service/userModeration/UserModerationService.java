package faang.school.postservice.service.userModeration;

import faang.school.postservice.contants.InfoMessage;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.UserBanPublisher;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserModerationService {
    public static final int MAX_UNVERIFIED_POSTS_BEFORE_BAN = 5;
    private final PostRepository postRepository;
    private final UserBanPublisher userBanPublisher;

    @Transactional(readOnly = true)
    public void checkAndBanUsersWithUnverifiedPosts() {
        try (Stream<Post> postStream = postRepository.streamByVerifiedFalse()) {
            Map<Long, List<Post>> userPostCounts = postStream
                    .collect(Collectors.groupingBy(Post::getAuthorId));
            userPostCounts.forEach((authorId, posts) -> {
                if (posts.size() > MAX_UNVERIFIED_POSTS_BEFORE_BAN) {
                    log.info(InfoMessage.INFO_BANNED_USER, authorId, posts.size());
                    userBanPublisher.publishUserBan(authorId);
                }
            });
        }
    }
}
