package faang.school.postservice.service.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.feed.FeedProperties;
import faang.school.postservice.dto.feed.UserNewsFeedDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.feed.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHeaterService {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final FeedCacheRepository feedCacheRepository;
    private final FeedProperties feedProperties;

    public void heat() {

    }

    private List<List<UserNewsFeedDto>> usersSublist() {
        List<UserNewsFeedDto> users = userServiceClient.getAllUsersInSystem();
        return ListUtils.partition(users, feedProperties.getThreadPool()
                .getInitialPoolSize());
    }

    private List<Post> getAllPostsForUser(List<Long> folowees){
        return postRepository.findAllByAuthorIdIn(folowees);
    }

    @Async("feedHeaterExecutor")
    private void asc(){

    }
}
