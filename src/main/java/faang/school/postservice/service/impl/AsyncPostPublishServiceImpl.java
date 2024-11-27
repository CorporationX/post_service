package faang.school.postservice.service.impl;

import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.AsyncPostPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncPostPublishServiceImpl implements AsyncPostPublishService {

    private final PostRepository postRepository;

    @Async("publishedPostThreadPool")
    public void publishPost(List<Post> posts) {
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
    }
}
