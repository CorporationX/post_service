package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {

    List<PostDto> getPostsByHashtag(String hashtag);

    PostDto createDraftPost(PostDto postDto);

    PostDto publishPost(PostDto postDto);

    void updateContentPost(String newContent, long id);

    PostDto updatePost(PostDto postDto);

    PostDto softDeletePost(Long postId);

    PostDto getPost(Long id);

    List<PostDto> getDraftPostsByUserId(long id);

    List<PostDto> getDraftPostsByProjectId(long id);

    List<PostDto> getPublishedPostsByUserId(long id);

    List<PostDto> getPublishedPostsByProjectId(long id);

    List<PostDto> getAllDraftsByAuthorId(Long userId);

    List<PostDto> getAllDraftsByProjectId(Long projectId);

    List<PostDto> getAllPublishedPostsByAuthorId(Long userId);

    List<PostDto> getAllPublishedPostsByProjectId(Long projectId);
}
