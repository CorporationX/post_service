package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.BatchProcessService;
import faang.school.postservice.service.SpellCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BatchProcessServiceImpl implements BatchProcessService {
    private final String AUTOCORRECTION_WARNING = "\n[Automatic correction of spelling and grammatical errors " +
            "has been applied to the post text. Please review the text before publishing!]";

    private final SpellCheckService spellCheckService;
    private final PostRepository postRepository;

    @Override
    @Async("postOperationsAsyncExecutor")
    public CompletableFuture<Void> processBatch(List<Post> postsBatch) {

        postsBatch.forEach(post -> {
            String language = spellCheckService.detectLanguage(post.getContent());
            if ("en".equals(language)) {
                String correctedText = spellCheckService.autoCorrect(post.getContent(), language);
                post.setContent(correctedText + AUTOCORRECTION_WARNING);
                post.setSpellCheckCompleted(true);
            }
        });

        postRepository.saveAll(postsBatch);
        return CompletableFuture.completedFuture(null);
    }
}
