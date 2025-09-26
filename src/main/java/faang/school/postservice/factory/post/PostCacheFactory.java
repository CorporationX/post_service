package faang.school.postservice.factory.post;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostCacheFactory {

    private final PostMapper postMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Value("${cache.post.ttl-seconds}")
    private int ttl;


    public PostCache fromPost(Post post) {
        PostCache cache = postMapper.toPostCache(post);
        cache.setTtl((long) ttl);

        List<Comment> last3Comments = commentRepository.findTop3ByPostIdOrderByCreatedAtDesc(post.getId());
        cache.setLastComments(commentMapper.toCommentCacheList(last3Comments));
        return cache;
    }
}
