package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SpellCheckerService spellCheckerService;

    @Async(value = "threadPool")
    public void correctPostsContent(List<Post> postList) {
        for (Post post : postList) {
            Optional<String> checkedPostContent = spellCheckerService.checkMessage(post.getContent());
            checkedPostContent.ifPresent(post::setContent);
        }
        postRepository.saveAll(postList);
    }
}