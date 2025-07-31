package faang.school.postservice.newsfeed;

import faang.school.postservice.dto.post.PostOutputDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsFeedProcessor {
    @Async("newsFeedExecutor")
    public void postPublish(PostOutputDto postOutputDto) {

    }
}
