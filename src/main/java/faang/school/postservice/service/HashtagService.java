package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void parseContentToAdd(Post post) {
        try {
            var hashtags = extractHashtags(post.getContent());
            for (var tag : hashtags) {
                var hashtag = hashtagRepository.findByHashtag(tag);
                Hashtag hashtagEntity = hashtag.orElseGet(() ->
                        hashtagRepository.save(Hashtag.builder().tag(tag).posts(new ArrayList<>()).build())
                );
                hashtagEntity.getPosts().add(post);
            }
        } catch (Exception e) {
            log.error("Exception " + e.getCause() + " " + e.getMessage());
            throw e;
        }
    }

    @Async("taskExecutor")
    @Transactional
    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void parsePostContentAndSaveHashtags(Post post, String previousContent) {
        var oldHashtags = extractHashtags(previousContent);
        var newHashtags = extractHashtags(post.getContent());

        List<String> hashtagsToAdd = newHashtags.stream()
                .filter(hashtag -> !oldHashtags.contains(hashtag))
                .collect(Collectors.toList());

        List<String> hashtagsToRemove = oldHashtags.stream()
                .filter(hashtag -> !newHashtags.contains(hashtag))
                .collect(Collectors.toList());
        updatePostHashtags(post, hashtagsToAdd, hashtagsToRemove);
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    private void updatePostHashtags(Post post, List<String> hashtagsToAdd, List<String> hashtagsToRemove) {
        for (String tag : hashtagsToAdd) {
            Optional<Hashtag> hashtagOptional = hashtagRepository.findByHashtag(tag);
            Hashtag hashtag = hashtagOptional.orElseGet(() ->
                    hashtagRepository.save(Hashtag.builder().tag(tag).posts(new ArrayList<>()).build())
            );
            hashtag.getPosts().add(post);
        }

        for (String tag : hashtagsToRemove) {
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
            String tag = matcher.group();
            tag = tag.substring(1).toLowerCase();
            hashtags.add(tag);
        }
        return new ArrayList<>(hashtags);
    }
}
