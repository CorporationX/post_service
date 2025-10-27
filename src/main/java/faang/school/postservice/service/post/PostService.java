package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface PostService {
    PostDto createPost(long authorId, @Valid CreatePostDto createPostDto) throws Exception;

    boolean publishPost(long requesterId, long postId);

    PostDto updatePost(long postId, long requesterId, @Valid UpdatePostDto updatePostDto);

    boolean deletePost(long requesterId, long postId);

    PostDto getPostById(long postId);

    List<PostDto> getAllUnpublishedPostsByAuthor(long authorId);

    List<PostDto> getAllUnpublishedPostsByProject(long projectId);

    List<PostDto> getAllPublishedPostsByAuthor(long authorId);

    List<PostDto> getAllPublishedPostsByProject(long projectId);
}
