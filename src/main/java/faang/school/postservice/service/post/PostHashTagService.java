package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostHashTagService {
    private static final String HASH_TAG_PATTERN = "#(\\w++)";

    public Post updateHashTags(Post post) {
        log.info("Update hash-tags of post with id: {}", post.getId());
        List<String> hashTags = parseByHashTag(post.getContent());
        post.setHashTags(hashTags);
        return post;
    }

    public List<String> toClone(List<String> hashTags) {
        return hashTags
                .stream()
                .toList();
    }

    private List<String> parseByHashTag(String content) {
        log.info("Pars by hash tag, content: {}", content);
        Pattern pattern = Pattern.compile(HASH_TAG_PATTERN);
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                .map(tag -> tag.group(1))
                .toList();
    }
}
