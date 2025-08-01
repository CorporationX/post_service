package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.PostCacheDto;

public interface PostCacheRepository {
    void putPost(PostCacheDto user);
    PostCacheDto getPostById(Long id);
}
