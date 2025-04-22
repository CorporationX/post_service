package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import org.springframework.web.multipart.MultipartFile;
import faang.school.postservice.model.Post;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface PostService {

    PostDto createDraft(PostDto postDto);

    PostDto publishPost(Long postId);

    PostDto updatePost(Long postId, PostDto postDto);

    PostDto softDelete(Long postId);

    PostDto getPostById(Long postId);

    List<PostDto> getAllDraftsByAuthorId(Long authorId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPublishedPostsByAuthorId(Long authorId);

    List<PostDto> getAllPublishedPostsByProjectId(Long projectId);

    List<ResourceDto> uploadImageToPost(Long postId, List<MultipartFile> files);
  
    Post getPostEntryById(@Min(1) long id);

    Post findPostById(Long id);

    void removeTagsFromPost(Long postId, List<Long> tagsId);

    void banUsersWithTooManyOffendedPosts();

    void moderatePosts();
}
