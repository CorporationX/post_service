package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.hashtag.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private final HashtagRepository hashtagRepository;

    @Override
    public void createHashtags(Post post) {
        List<String> hashtags = findHashtags(post.getContent());

        List<Hashtag> entityHashtags = hashtags.stream()
                .map(tag -> Hashtag.builder()
                        .name(tag)
                        .build())
                .peek(hashtag -> hashtag.getPosts().add(post))
                .toList();

        hashtagRepository.saveAll(entityHashtags);
    }

    @Override
    public List<Post> findPostsByHashtag(String hashtag) {
        Hashtag hashtag1 = hashtagRepository.findByName(hashtag);
        return hashtag1.getPosts();
    }

    private List<String> findHashtags(String content) {
        List<String> foundHashtags = new ArrayList<>();

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            foundHashtags.add(matcher.group());
        }

        return foundHashtags;
    }
}
