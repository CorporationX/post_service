package faang.school.postservice.service.post;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PublisherUsersBan;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PublisherUsersBan publisherUsersBan;
    @Value("${post.maxUnverifiedPosts}")
    private int maxUnverifiedPosts;

    public void banUsersWithMultipleUnverifiedPosts() {
        List<Post> posts = getUnverifiedPost();
        sendUsersToBan(posts);
    }

    private List<Post> getUnverifiedPost() {
        return postRepository.findByVerified(false);
    }

    private void sendUsersToBan(List<Post> posts) {
        posts.stream()
                .filter(post -> !post.getVerified())
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> entry.getValue() >= maxUnverifiedPosts)
                .map(Map.Entry::getKey)
                .forEach(id -> publisherUsersBan.publish(new UserEvent(id)));
    }
}
