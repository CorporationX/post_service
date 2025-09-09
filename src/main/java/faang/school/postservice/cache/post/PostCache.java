package faang.school.postservice.cache.post;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostCache {
    void set(PostDto dto);

    PostDto get(long id);

    List<PostDto> getAll(List<Long> ids);
}
