package faang.school.postservice.repository.cache.post;

import faang.school.postservice.dto.cache.post.PostCacheDto;
import faang.school.postservice.dto.comment.CommentDto;

import java.util.Optional;

public interface PostCacheRepository {

    void save(PostCacheDto postCacheDto);

    boolean incrementLikesCount(Long postId);

    Optional<PostCacheDto> findById(Long postId);

    boolean updatePostsComments(Long postId, CommentDto commentDto);
}
