package faang.school.postservice.service.impl.post.async;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostServiceAsync;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceAsyncImpl implements PostServiceAsync {

    private final PostRepository postRepository;
    private final ModerationDictionary dictionary;

    @Override
    @Async("fixedThreadPools")
    public void moderatePostsByBatches(List<Post> posts) {
        posts.forEach(post -> {
            boolean badWordsExist = dictionary.containsBadWords(post.getContent());

            post.setVerified(!badWordsExist);
            post.setVerifiedDate(LocalDateTime.now());
        });

        postRepository.saveAll(posts);
    }
}
