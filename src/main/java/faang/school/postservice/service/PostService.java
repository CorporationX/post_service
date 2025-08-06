package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {
    PostDto findById(long id);
    void save(PostDraftDto postDraftDto);
    void markPostAsDeleted(long id);
    void createPostDraft(PostDraftDto postDraftDto);
    void publishPost(Long postId, PostDraftDto postDraftDto);
    void updatePost(PostDto postDto, Long postId);
    List<PostDto> getAllPostsByAuthorId (long userId);
    List<PostDto> getAllPostsByProjectId (long projectId);
    List<PostDto> getAllPublishedPostsByAuthorId (long userId);
    List<PostDto> getAllPublishedPostsByProjectId (long projectId);
}
