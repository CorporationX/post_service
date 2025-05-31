package faang.school.postservice.service;

import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.model.Post;

public interface PostService {
    CreatePostDto create(CreatePostDto createPostDto);
    Post findPostById(long postId);
    void banUsersIfRequired();
}
