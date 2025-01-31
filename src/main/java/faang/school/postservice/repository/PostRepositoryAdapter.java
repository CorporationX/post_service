package faang.school.postservice.repository;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter {
    private final PostRepository postRepository;

    public Post findById(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(String.format("Пост с id:%s не найден!", postId)));
        if (post.isDeleted()) {
            throw new DataValidationException(String.format("Пост с id:%s удален!", postId));
        }
        return post;
    }
}
