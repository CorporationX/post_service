package faang.school.postservice.service.cache;

public interface CacheService {

    void saveAuthor(Long authorId, Long modelId);

    void savePost(Long postId, Long authorId, Long projectId);
}