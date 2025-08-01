package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.PostCacheDto;

import java.util.Optional;

public interface PostCacheRepository {
    void putPost(PostCacheDto post);
    Optional<PostCacheDto> getPostById(Long id);
}
