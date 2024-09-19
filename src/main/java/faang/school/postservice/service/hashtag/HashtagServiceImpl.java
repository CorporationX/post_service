package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    @Transactional
    @Async
    public void createHashtags(Post post) {
        List<Hashtag> postHashtags = processHashtags(post);

        post.setHashtags(postHashtags);

        hashtagRepository.saveAll(postHashtags);
    }

    @Override
    @Transactional
    @Async
    public void updateHashtags(Post post) {
        List<Hashtag> postHashtags = processHashtags(post);

        post.getHashtags().removeIf(hashtag -> !postHashtags.contains(hashtag));

        postHashtags.forEach(hashtag -> {
            if (!post.getHashtags().contains(hashtag)) {
                post.getHashtags().add(hashtag);
            }
        });

        hashtagRepository.saveAll(postHashtags);
    }

    @Override
    public List<Post> findPostsByHashtag(String hashtag) {
        return hashtagRepository
                .findByName(hashtag)
                .map(Hashtag::getPosts)
                .orElseGet(ArrayList::new);
    }

    private List<Hashtag> processHashtags(Post post) {
        List<String> foundHashtags = findHashtags(post.getContent());

        return foundHashtags.stream()
                .map(tag -> hashtagRepository.findByName(tag)
                        .orElseGet(() -> Hashtag.builder().name(tag).build()))
                .toList();
    }

    private List<String> findHashtags(String content) {
        List<String> foundHashtags = new ArrayList<>();

        if (content == null || content.isBlank()){
            return foundHashtags;
        }

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            foundHashtags.add(matcher.group());
        }

        return foundHashtags;
    }
}
