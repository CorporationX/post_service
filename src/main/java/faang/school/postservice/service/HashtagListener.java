package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class HashtagListener<T> extends AbstractPostListener<PostDto> implements MessageListener {

    public HashtagListener(ObjectMapper objectMapper, HashtagService hashtagService) {
        super(objectMapper, hashtagService);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleData(message, PostDto.class,
                (postDto) -> hashtagService.saveHashtags(findHashtags(postDto.getContent(), postDto.getId())));
    }

    Set<HashtagDto> findHashtags(String content, Long postId) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\S+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            hashtags.add(matcher.group());
        }
        return hashtags.stream()
                .distinct()
                .map(hashtag -> HashtagDto.builder().postId(postId).content(hashtag).build())
                .collect(Collectors.toSet());
    }
}

