package faang.school.postservice.service;

import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    public static final String POST_BY_ID_NOT_FOUND = "Post by id [{}] not found";
    private final PostRepository postRepository;
    private final Utils utils;

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(utils.format(POST_BY_ID_NOT_FOUND, postId)));
    }
}
