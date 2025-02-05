package faang.school.postservice.service;

import faang.school.postservice.config.PostConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostConfig postConfig;
    private final ExecutorService executorService;

    public void publishScheduledPosts() {
        int batchSize = postConfig.getBatchSize();
        int page = 0;

        while (true) {
            Page<Post> postPage = postRepository.findReadyToPublish(PageRequest.of(page, batchSize));

            if (postPage.isEmpty()) break;

            List<Post> posts = postPage.getContent();
            executorService.submit(() -> publishBatch(posts));
            page++;
        }
    }

    private void publishBatch(List<Post> posts) {
        try {
            posts.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });

            postRepository.saveAll(posts);
        } catch (Exception e) {
            log.error("Ошибка при публикации батча из {} постов", posts.size(), e);
        }
    }
}
