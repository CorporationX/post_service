package faang.school.postservice.messaging.listening;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    private Set<HashtagDto> findHashtags(String content, Long postId) {
        List<String> hashtags = new ArrayList<>();
        while (!content.isBlank()) {
            content = content.substring(content.indexOf("#", 1));
            int endOfHashtag = content.indexOf("\\s");
            if (endOfHashtag > content.indexOf("#") || endOfHashtag == -1) {
                endOfHashtag = content.indexOf("#", 1);
                if (endOfHashtag == -1){
                    endOfHashtag = content.length()-1;
                }
            }
            hashtags.add(content.substring(0, endOfHashtag).trim());
            if (content.matches("^#\\S[^#]+$")) break;
        }
        return hashtags.stream()
                .distinct()
                .map(hashtag -> HashtagDto.builder().postId(postId).content(hashtag).build())
                .collect(Collectors.toSet());
    }


}
