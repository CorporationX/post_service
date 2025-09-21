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
    private final PostRepository postRepository;

    public PostPublishedEvent fromPost(Post post) {
        PostPublishedEvent event = postMapper.toPostPublishedEvent(post);
        List<Long> followers = postRepository.findAllFollowers(post.getAuthorId(), post.getProjectId());
        event.setSubscribers(followers);
        return event;
    }
}
