package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.RedisMessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final RedisMessagePublisher redisMessagePublisher;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public void checkUserAndBannedForComment() {
        Map<Long, List<Comment>> authorCommentWithoutVerification = commentRepository.findAllByPostWithoutVerification()
                .stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId));

        authorCommentWithoutVerification.forEach((authorId, comments) -> {
            if (comments.size() > 5) {
                redisMessagePublisher.publish(authorId.toString());
            }
        });
    }

    public void checkUserAndBannedForPost() {
        Map<Long, List<Post>> authorCommentWithoutVerification = postRepository.findAllPostWithoutVerification()
                .stream()
                .collect(Collectors.groupingBy(Post::getAuthorId));

        authorCommentWithoutVerification.forEach((authorId, posts) -> {
            if (posts.size() > 5) {
                redisMessagePublisher.publish(authorId.toString());
            }
        });
    }
}