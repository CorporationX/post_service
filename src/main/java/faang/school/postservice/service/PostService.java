package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;

    @Transactional
    public void moderateAll() {
        log.info("Moderate posts");
        List<Post> posts = postRepository.findNotVerifiedPosts();
        posts.forEach(post -> {
            post.setVerified(moderationDictionary.checkString(post.getContent()));
            post.setVerifiedDate(LocalDateTime.now());
        });
    }
}
