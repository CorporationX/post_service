package faang.school.postservice.service;

import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostContextMapper context;

    public Post getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with the same id does not exist"));

        long countLike = getCountLikeForPost(post);
        context.getCountLikeEveryonePost().put(postId, countLike);

        return post;
    }

    private long getCountLikeForPost(Post post) {
        return post.getLikes().size();
    }
}