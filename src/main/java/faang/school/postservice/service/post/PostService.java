package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;

import java.util.Optional;

public interface PostService {
    Optional<Post> getPost(long postId);
}
