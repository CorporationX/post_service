package faang.school.postservice.service;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostService {

    PostDto createPost(PostDto postDto);
    PostDto publishPost(Long id);
    PostDto updatePost(Long id, PostDto postDto);
    void deletePost(Long id);
    PostDto getPost(Long id);
    List<PostDto> getUserDrafts(Long authorId);
    List<PostDto> getProjectDrafts(Long projectId);
    List<PostDto> getUserPublishedPosts(Long authorId);
    List<PostDto> getProjectPublishedPosts(Long projectId);
    Page<PostDto> getAllPostsByHashtagId(String content, Pageable pageable);
    Post getPostByIdInternal(Long id);
    Post updatePostInternal(Post post);
    List<CompletableFuture<Void>> publishScheduledPosts();
    void correctSpellingInUnpublishedPosts();
    List<List<Post>> findAndSplitUnverifiedPosts();
    CompletableFuture<Void> verifyPostsForSwearWords(List<Post> unverifiedPostsBatch);
}
