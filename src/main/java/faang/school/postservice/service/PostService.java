package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;

import java.util.List;

public interface PostService {

    PostDto createDraft(PostDto postDto);

    PostDto createPost(Long id);

    PostDto updatePost(PostDto postDto);

    void softDeletePost(Long id);

    PostDto getPostById(Long id);

    List<PostDto> getAllBlackPostsByAuthorId(Long authorId);

    List<PostDto> getAllBlackProjectsByAuthorId(Long projectId);

    List<PostDto> getAllPublicPostsByAuthorId(Long authorId);

    List<PostDto> getAllPublicProjectsByAuthorId(Long projectId);
}
