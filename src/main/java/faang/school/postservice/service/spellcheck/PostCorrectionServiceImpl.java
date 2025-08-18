package faang.school.postservice.service.spellcheck;

import faang.school.postservice.config.properties.SpellCheckAsyncProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCorrectionServiceImpl implements PostCorrectionService {

    private final PostService postService;
    private final AsyncPostSpellCheckerService asyncService;
    private final SpellCheckAsyncProperties spellCheckAsyncProperties;

    @Override
    public void correctAllUnpublishedPosts() {
        List<Post> posts = postService.getUnpublishedPosts();
        if (posts.isEmpty()) {
            log.info("No unpublished posts to correct");
            return;
        }

        List<List<Post>> batches = splitIntoBatches(posts, spellCheckAsyncProperties.batchSize());
        log.info("Found {} posts split into {} batches", posts.size(), batches.size());

        for (List<Post> batch : batches) {
            asyncService.correctPostBatchAsync(batch);
        }
    }

    private List<List<Post>> splitIntoBatches(List<Post> posts, int size) {
        List<List<Post>> batches = new ArrayList<>();
        for (int i = 0; i < posts.size(); i += size) {
            batches.add(posts.subList(i, Math.min(i + size, posts.size())));
        }
        return batches;
    }
}
