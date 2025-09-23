package faang.school.postservice.factory.post;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostPublishedEventFactory {

    private final PostMapper postMapper;

    public PostPublishedEvent fromPostAndFollowerIds(Post post, List<Long> followerIds) {
        PostPublishedEvent event = postMapper.toPostPublishedEvent(post);
        event.setSubscribers(followerIds);
        return event;
    }
}
