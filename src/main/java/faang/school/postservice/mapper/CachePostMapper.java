package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CachePostMapper {
    private final CommentRepository commentRepository;

    @Value("${spring.post.cache.ttl}")
    private int postTtl;
    @Value("${spring.post.cache.max-comment}")
    private int maxCommentToCachePost;

    public List<CachePost> convertPostsToCachePosts(List<Post> posts) {
        return posts.stream().map(post -> {
            List<Comment> comments = commentRepository.findAllByPostId(post.getId());
            return CachePost.builder()
                    .id(post.getId())
                    .countLike(post.getLikes().size())
                    .comments(comments.isEmpty() ?
                            null : new LinkedHashSet<>(comments.stream()
                            .limit(maxCommentToCachePost)
                            .toList().stream()
                            .map(comment -> CommentCache.builder()
                                    .id(comment.getId())
                                    .authorId(comment.getAuthorId())
                                    .content(comment.getContent())
                                    .build()).toList()))
                    .ttl(postTtl)
                    .build();
        }).toList();
    }

    public CachePost converPostToCachePost(Post post, List<CommentCache> commentCaches) {
        return CachePost.builder()
                .id(post.getId())
                .comments(new LinkedHashSet<>(commentCaches))
                .content(post.getContent())
                .countLike(post.getLikes().size())
                .ttl(postTtl)
                .build();
    }
}
