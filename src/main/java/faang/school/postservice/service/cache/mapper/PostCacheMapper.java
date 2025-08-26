package faang.school.postservice.service.cache.mapper;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.cache.model.PostCacheDto;

import java.util.List;

public final class PostCacheMapper {
    private PostCacheMapper() {}

    public static PostCacheDto fromEntity(Post post) {
        if (post == null) {
            return null;
        }
        List<String> resourceKeys = post.getResources() == null
                ? List.of()
                : post.getResources().stream()
                .map(Resource::getKey)
                .toList();

        return new PostCacheDto(
                post.getId(),
                post.getAuthorId(),
                post.getProjectId(),
                post.getContent(),
                resourceKeys,
                post.getPublishedAt()
        );
    }
}
