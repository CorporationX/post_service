package faang.school.postservice.util.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.LongStream;

public class LikeTestUtil {
    public static Like getPostLike(long id, long userId, Post post) {
        return Like.builder()
                .id(id)
                .userId(userId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Like getCommentLike(long id, long userId, Comment comment) {
        return Like.builder()
                .id(id)
                .userId(userId)
                .comment(comment)
                .build();
    }

    public static List<Like> getPostLikes(int number, Post post) {
        return LongStream.rangeClosed(1, number)
                .mapToObj(i -> getPostLike(i, i, post))
                .toList();
    }

    public static List<Like> getCommentLikes(int number, Comment comment) {
        return LongStream.rangeClosed(1, number)
                .mapToObj(i -> getCommentLike(i, i, comment))
                .toList();
    }
}
