package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserContext userContext;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;

    public List<PostDto> getFeed(Long postId) {
        Long userId = userContext.getUserId();
        return null;
    }
}
