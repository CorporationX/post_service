package faang.school.postservice.service.feed;

import faang.school.postservice.model.feed.PostCache;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RedisFeedService {

    private final RedisFeedRepository redisFeedRepository;

    public void save(PostCache post) {
        redisFeedRepository.save(post);
    }

    public Iterable<PostCache> getAll() {
        return redisFeedRepository.findAll();
    }
}
