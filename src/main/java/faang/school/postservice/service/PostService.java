package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;

    // TODO: проверка что пользователь или проект существуют
    public Post createDraftPost(final Post post) {
        postValidator.validatePost();
        Post savedPost = postRepository.save(post);
        log.info("Post with id {} was created", savedPost.getId());
        return savedPost;
    }
}
