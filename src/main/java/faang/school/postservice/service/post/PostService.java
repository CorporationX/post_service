package faang.school.postservice.service.post;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

import java.util.List;

public interface PostService {
    PostDto create(CreatePostDto createPostDto);

    PostDto publish(@NonNull Long postId);

    PostDto update(Long postId, UpdatePostDto updatePostDto);

    void delete(@NotNull Long postId);

    PostDto getById(@NotNull Long postId);

    List<PostDto> getDraftsByUser(@NotNull Long userId);

    List<PostDto> getDraftsByProject(@NotNull Long projectId);

    List<PostDto> getPublishedByUser(@NotNull Long userId);

    List<PostDto> getPublishedByProject(@NotNull Long projectId);

    Post getPostById(Long postId);

    boolean existsById(Long postId);

    CommentDto createComment(Long postId, Long authorId, SaveCommentDto saveCommentDto);

    List<CommentDto> getCommentsByPostId(Long postId);

    void updatePostContent(Post post, String correctedContent);

    List<Post> getUnpublishedPosts();
}
