package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDraftDto;

public interface PostService {
    PostDraftDto findById(long id);
    void save(PostDraftDto postDraftDto);
    void markPostAsDeleted(long id);
    void createPostDraft(PostDraftDto postDraftDto);
    void publishPost(Long postId, PostDraftDto postDraftDto);
}
