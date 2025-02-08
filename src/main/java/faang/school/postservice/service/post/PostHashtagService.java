package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostHashtagService {
    private final PostHashtagCacheService postHashtagCacheService;

    @Transactional(readOnly = true)
    public List<Post> getLimitedPostsByHashtag(String hashtag, int limit) {
        List<Post> allPosts = postHashtagCacheService.getPostsByHashtag(hashtag);
        return allPosts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}