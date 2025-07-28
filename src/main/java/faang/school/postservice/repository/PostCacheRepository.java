package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostOutputDto;

public interface PostCacheRepository {
    void set(PostOutputDto post);

    PostOutputDto get(long postId);
}
