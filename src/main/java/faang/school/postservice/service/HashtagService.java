package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostMapper postMapper;

    public List<PostDto> getPostByHashtag(String hashtag) {
        var hashtagEntity = hashtagRepository.findByHashtag(hashtag.toLowerCase()).orElseThrow(
                () -> new EntityNotFoundException("Hashtag " + hashtag + " wasn`t found"));
        Set<Post> uniquePosts = new HashSet<>(hashtagEntity.getPosts());

        return uniquePosts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Async("taskExecutor")
    @Transactional
    public void parseContentToAdd(Post post) {
        try {
            var content = extractHashtags(post.getContent());
            for (var tag : content) {
                tag = tag.substring(1).toLowerCase();
                var hashtag = hashtagRepository.findByHashtag(tag);
                String finalTag = tag;
                Hashtag hashtagEntity = hashtag.orElseGet(() ->
                        hashtagRepository.save(Hashtag.builder().tag(finalTag).posts(new ArrayList<>()).build())
                );
                hashtagEntity.getPosts().add(post);
            }
        } catch (Exception e) {
            log.error("Exception " + e.getCause() + " " + e.getMessage());
        }
    }

    @Async("taskExecutor")
    @Transactional
    public void parseContentToUpdate(Post post, String previousContent) {
        var oldHashtags = extractHashtags(previousContent);
        var newHashtags = extractHashtags(post.getContent());

        List<String> hashtagsToAdd = newHashtags.stream()
                .filter(hashtag -> !oldHashtags.contains(hashtag))
                .collect(Collectors.toList());

        List<String> hashtagsToRemove = oldHashtags.stream()
                .filter(hashtag -> !newHashtags.contains(hashtag))
                .collect(Collectors.toList());
        System.out.println(hashtagsToAdd);
        System.out.println(hashtagsToRemove);
        updatePostHashtags(post, hashtagsToAdd, hashtagsToRemove);
    }

    private void updatePostHashtags(Post post, List<String> hashtagsToAdd, List<String> hashtagsToRemove) {
        for (String hashtagText : hashtagsToAdd) {
            String tag = hashtagText.substring(1).toLowerCase();
            Optional<Hashtag> hashtagOptional = hashtagRepository.findByHashtag(tag);

            Hashtag hashtag = hashtagOptional.orElseGet(() ->
                    hashtagRepository.save(Hashtag.builder().tag(tag).posts(new ArrayList<>()).build())
            );
            hashtag.getPosts().add(post);
        }

        for (String hashtagText : hashtagsToRemove) {
            String tag = hashtagText.substring(1).toLowerCase();
            var hashtag1 = hashtagRepository.findByHashtag(tag);
            var hashtag = hashtag1.get(); // always exists
            hashtagRepository.deletePostHashtag(post.getId(), hashtag.getId());
        }
    }

    public static List<String> extractHashtags(String input) {
        Set<String> hashtags = new HashSet<>();
        Pattern pattern = Pattern.compile("#\\w+");

        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return new ArrayList<>(hashtags);
    }
}
