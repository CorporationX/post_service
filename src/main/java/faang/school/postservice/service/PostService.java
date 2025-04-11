package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import faang.school.postservice.model.Post;

public interface PostService {
    @Transactional
    PostDto createDraft(PostDto postDto);

    @Transactional
    PostDto getPost(long postId);

    @Transactional
    PostDto publishPost(long postId);

    @Transactional
    PostDto updatePost(long postId, String content);

    @Transactional
    void softDeletePost(long postId);

    @Transactional
    List<PostDto> getAllDraftsByAuthorId(long authorId);

    @Transactional
    List<PostDto> getAllDraftsByProjectId(long projectId);

    @Transactional
    List<PostDto> getAllPostsByAuthorId(long authorId);

    @Transactional
    List<PostDto> getAllPostsByProjectId(long projectId);
    Post findPostById(long postId);
}
