package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HeaterCashFeed {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Value("${spring.heater.capacity.max}")
    private int maxSizePosts;

    public void get(){
        Pageable pageable = PageRequest.of(0, maxSizePosts);
        List<Post> posts = postRepository.findNewPostsForHeat(pageable);
    }
}
