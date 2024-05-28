package faang.school.postservice.service;

import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new DataLikeValidation("Поста с id " + postId + " нет в базе данных."));
    }
}