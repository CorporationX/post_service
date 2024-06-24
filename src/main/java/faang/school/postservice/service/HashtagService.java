package faang.school.postservice.service;

import faang.school.postservice.jpa.HashtagJpaRepository;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagJpaRepository hashtagJpaRepository;
    private final HashtagRepository hashtagRepository;
    private final CacheManager cacheManager;

    @Async
    @Transactional
    public void parsePostAndCreateHashtags(Post post) {
        log.info("Parse post content with ID: {}", post.getId());
        List<String> tags = getHashtags(post.getContent());
        tags.stream()
                .map(tag -> Hashtag.builder()
                        .name(tag)
                        .build())
                .forEach(tag -> create(tag, post));
    }

    private void create(Hashtag hashtag, Post post) {
        log.info("Save hashtag = {}", hashtag);
        Optional<Hashtag> foundHashtag = hashtagJpaRepository.findByName(hashtag.getName());

        if (foundHashtag.isPresent()) {
            hashtag = foundHashtag.get();
            hashtag.getPosts().add(post);
        } else {
            hashtag.getPosts().add(post);
        }
        post.getHashtags().add(hashtag);

        Objects.requireNonNull(cacheManager.getCache("posts")).evict(hashtag.getName());
        hashtagJpaRepository.save(hashtag);
    }

    private List<String> getHashtags(String content) {
        return Arrays.stream(content.split("[.,!;: ]+"))
                .filter(word -> word.startsWith("#"))
                .map(word -> word.substring(1))
                .toList();
    }

    @Transactional(readOnly = true)
    public Hashtag findByName(String name) {
        return hashtagRepository.findByName(name);
    }
}
