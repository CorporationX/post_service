package faang.school.postservice.service.hashtag;

import faang.school.postservice.dto.post.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.HashtagMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final HashtagMapper hashtagMapper;
    private final PostMapper postMapper;

    private final HashMap<Hashtag, List<Post>> hashtagsWithPosts = new HashMap<>();

    @PostConstruct
    public void init() {
        updateHashtagMap();
    }

    @Scheduled(fixedRate = 60000)
    public void updateHashtagMap() {
        synchronized (hashtagsWithPosts) {
            hashtagsWithPosts.clear();
            hashtagRepository.findAll()
                    .forEach(hashtag -> hashtagsWithPosts.put(hashtag, hashtag.getPosts()));
        }
    }

    public List<PostDto> getPostsByHashtag(String hashtag) {
        return postMapper.toDto(hashtagsWithPosts.get(hashtag));
    }

    public List<HashtagDto> findTopXPopularHashtags(int x) {
        return hashtagMapper.toDto(hashtagRepository.findTopXPopularHashtags(PageRequest.of(0, x)));
    }
}
