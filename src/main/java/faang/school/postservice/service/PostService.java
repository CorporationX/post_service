package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;

public interface PostService {
    PostDto findById(long id);
    void save(PostDto postDto);
    void markPostAsDeleted(long id);
    void createPostDraft(PostDto postDto);
}
