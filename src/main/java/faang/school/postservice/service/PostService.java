package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {

    void createDraftPost(PostDto postDto);

    void publishPost(long id);

    void updateContentPost(String newContent, long id);

    void softDeletePost(long id);

    PostDto getPost(long id);

    List<PostDto> getDraftPostsByUserId(long id);

    List<PostDto> getDraftPostsByProjectId(long id);

    List<PostDto> getPublishedPostsByUserId(long id);

    List<PostDto> getPublishedPostsByProjectId(long id);

    List<Long> getAuthorsWithMoreFiveUnverifiedPosts();
}
