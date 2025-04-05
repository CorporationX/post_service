package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface PostService {
    List<PostDto> getFeed(PostDto postDto);

    PostDto createDraft(PostDto postDto);

    PostDto publish(Long postId);

    PostDto update(Long id, String content);

    PostDto softDelete(Long id);

    PostDto getById(Long id);

    List<PostDto> getNotDeletedDraftsByUserId(Long userId);

    List<PostDto> getNotDeletedDraftsByProjectId(Long projectId);

    List<PostDto> getNotDeletedPublishedPostsByUserId(Long userId);

    List<PostDto> getNotDeletedPublishedPostsByProjectId(Long projectId);

    void grammarCorrectionPost(Post post);
}
