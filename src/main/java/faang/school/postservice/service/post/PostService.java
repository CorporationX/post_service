package faang.school.postservice.service.post;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    void moderatePosts();
    Post getPostById(long id);
    PostDto createPost(PostDto postDto);
    PostDto publishPost(Long postId);
    PostDto updatePost(Long postId, String content, LocalDateTime publicationTime);
    void deletePostById(Long postId);
    PostDto getPost(Long postId);
    List<PostDto> getAllPostsDraftsByUserAuthorId(Long userId);
    List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId);
    List<PostDto> getAllPublishedNotDeletedPostsByUserAuthorId(Long userId);
    List<PostDto> getAllPublishedNotDeletedPostsByProjectAuthorId(Long projectId);
    Post findById(Long postId);
    void publishScheduledPosts();
    boolean existsById(long id);
    Long incrementPostViews(Long postId);
}
