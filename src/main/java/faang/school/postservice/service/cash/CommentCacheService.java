package faang.school.postservice.service.cash;

import faang.school.postservice.model.Comment;
import faang.school.postservice.redis.entities.CommentCache;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentCacheService {
    private final CommentService commentService;

    @Value("${spring.data.cache.comments.latest-count}")
    private int latestCommentsCount;

    public LinkedHashSet<CommentCache> fetchLatestComments(Long postId) {
        try {
            return commentService.getLatestCommentsByPostId(postId, latestCommentsCount)
                    .stream()
                    .map(this::createCommentCache)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            log.error("Error fetching latest comments for post {}", postId, e);
            return new LinkedHashSet<>();
        }
    }

    private CommentCache createCommentCache(Comment comment) {
        return CommentCache.builder()
                .id(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}