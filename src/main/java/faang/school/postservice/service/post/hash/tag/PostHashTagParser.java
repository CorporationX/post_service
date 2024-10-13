package faang.school.postservice.service.post.hash.tag;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostHashTagParser {
    private static final String HASH_TAG_PATTERN = "#(\\w++)";
    private static final String JSON_PREFIX = "[\"";
    private static final String JSON_POSTFIX = "\"]";

    public void updateHashTags(Post post) {
        log.info("Update hash-tags of post with id: {}", post.getId());
        List<String> hashTags = parseByHashTag(post.getContent());
        post.setHashTags(hashTags);
    }

    private List<String> parseByHashTag(String content) {
        log.info("Pars by hash-tag, content: {}", content);
        Pattern pattern = Pattern.compile(HASH_TAG_PATTERN);
        Matcher matcher = pattern.matcher(content);
        return matcher.results()
                .map(tag -> tag.group(1).toLowerCase())
                .toList();
    }

    public List<String> getNewHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get new hash-tags between primal: {} AND updated: {}", primalHashTags, updatedHashTags);
        List<String> newHashTags = new ArrayList<>(updatedHashTags);
        newHashTags.removeAll(primalHashTags);
        log.info("New hash-tags: {}", newHashTags);
        return newHashTags;
    }

    public List<String> getDeletedHashTags(List<String> primalHashTags, List<String> updatedHashTags) {
        log.info("Get deleted hash-tags between primal: {} AND updated: {}", primalHashTags, updatedHashTags);
        List<String> deletedHashTags = new ArrayList<>(primalHashTags);
        deletedHashTags.removeAll(updatedHashTags);
        log.info("Deleted hash-tags: {}", deletedHashTags);
        return deletedHashTags;
    }

    public String convertTagToJson(String hashTag) {
        return JSON_PREFIX + hashTag + JSON_POSTFIX;
    }
}
