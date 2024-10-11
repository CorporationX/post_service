package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;
    private final PostRepository postRepository;

    @Value("${post.spelling-corrector.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${post.spelling-corrector.scheduler.cron}")
    public void startCheckAISpellingPosts() {
        int offset = 0;
        List<Post> draftPosts;

        do {
            Pageable pageable = PageRequest.of(offset, batchSize, Sort.by("id").ascending());
            draftPosts = postRepository.findDraftsPaginate(pageable);

            try {
                postService.correctPosts(draftPosts);
            } catch (RuntimeException exception) {
                log.error("Непредвиденная ошибка при обработки батча ({}, {}) авто корректировки",
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        exception);
            }
            offset++;
        } while (!draftPosts.isEmpty());
    }
}
