package faang.school.postservice.service;

import faang.school.postservice.model.Post;

public interface PostService {
    Post findById(Long postId);
}
