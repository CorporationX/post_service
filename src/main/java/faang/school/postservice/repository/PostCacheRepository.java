package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostCacheDto;

import java.util.Optional;

public interface PostCacheRepository {
    void set(PostCacheDto post);

    Optional<PostCacheDto> get(long postId);
}
