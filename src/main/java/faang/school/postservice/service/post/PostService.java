package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {
    PostDto create(CreatePostDto dto);
    void publish(long postId);
    PostDto update(long postId, UpdatePostDto dto);
    void softDelete(long postId);
    void restore(long postId);
    void delete(long postId);
    List<PostDto> getPosts(long authorId, long projectId, boolean deleted, boolean published, 
            int page, int size,
            String sortBy, String sortDirection);
    Post getPostByIdOrThrow(long postId);
}
