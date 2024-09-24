package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    final LikeRepository likeRepository;
    final UserServiceClient userServiceClient;
    final PostRepository postRepository;

    public final String USER_NOT_EXIST = "User not exist";
    public final String POST_NOT_EXIST = "Post not exist";
    public final String COMMENT_NOT_EXIST = "Comment not exist";
    public final String LIKE_NOT_EXIST = "Like not exist";
    public final String SECOND_LIKE = "Second like is denied";
    private final CommentRepository commentRepository;


    @Transactional
    public Like addToPost(Long postId, Like entity) {
        checkUserExist(entity.getUserId());

        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, entity.getUserId());
        if (like.isPresent()) {
            log.error("PostId = {} and UserId = {}. {}", postId, entity.getUserId(), SECOND_LIKE);
            throw new RuntimeException(SECOND_LIKE);
        }

        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            log.error("Post = {}. {}", postId, POST_NOT_EXIST);
            throw new RuntimeException(POST_NOT_EXIST);
        }

        entity.setPost(post.get());
        Like newLike = likeRepository.save(entity);
        List<Like> likeList = post.get().getLikes();
        likeList.add(newLike);
        post.get().setLikes(likeList);
        postRepository.save(post.get());

        return newLike;
    }

    @Transactional
    public Like addToComment(Long commentId, Like entity) {
        checkUserExist(entity.getUserId());

        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, entity.getUserId());
        if (like.isPresent()) {
            log.error("CommentId = {} and UserId = {}. {}", commentId, entity.getUserId(), SECOND_LIKE);
            throw new RuntimeException(SECOND_LIKE);
        }

        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            log.error("Comment = {}. {}", commentId, COMMENT_NOT_EXIST);
            throw new RuntimeException(COMMENT_NOT_EXIST);
        }

        entity.setComment(comment.get());
        Like newLike = likeRepository.save(entity);
        List<Like> likeList = comment.get().getLikes();
        likeList.add(newLike);
        comment.get().setLikes(likeList);
        commentRepository.save(comment.get());

        return newLike;
    }

    private void checkUserExist(Long userId) {
        // Поскольку контроллера для UserService пока нет, создаем заглушку
        //UserDto userDto = userServiceClient.getUser(entity.getUserId().longValue());
        UserDto userDto = new UserDto(1L, "Alex", "alex@gmail.com");
        if (userDto.getId() == null) {
            log.error("Id = {}. {}", userId, USER_NOT_EXIST);
            throw new RuntimeException(USER_NOT_EXIST);
        }
    }

    private void checkPostExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            log.error("PostId = {}. {}", postId, POST_NOT_EXIST);
            throw new RuntimeException(POST_NOT_EXIST);
        }
    }

    @Transactional
    public void deletePostLike(Long postId, Long userId) {
        checkUserExist(userId);
        checkPostExist(postId);

        likeRepository.findByCommentIdAndUserId(postId, userId).ifPresentOrElse(like -> {
                    Post post = postRepository.findById(postId).get();
                    post.getLikes().remove(like);
                    postRepository.save(post);
                    likeRepository.deleteByCommentIdAndUserId(postId, userId);
                },
                () -> {
                    log.error("For postId = {} and userId = {}. {}", postId, userId, LIKE_NOT_EXIST);
                    throw new RuntimeException(LIKE_NOT_EXIST);
                });
    }

    @Transactional
    public void deleteCommentLike(Long commentId, Long userId) {
        checkUserExist(userId);
        if (!commentRepository.existsById(commentId)) {
            log.error("CommentId = {}. {}", commentId, COMMENT_NOT_EXIST);
            throw new RuntimeException(COMMENT_NOT_EXIST);
        }

        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresentOrElse(like -> {
                    Comment comment = commentRepository.findById(commentId).get();
                    comment.getLikes().remove(like);
                    commentRepository.save(comment);
                    likeRepository.deleteByCommentIdAndUserId(commentId, userId);
                },
                () -> {
                    log.error("For commentId = {} and userId = {}. {}", commentId, userId, LIKE_NOT_EXIST);
                    throw new RuntimeException(LIKE_NOT_EXIST);
                });
    }

    @Transactional
    public int getLikesByPost(Long postId) {
        checkPostExist(postId);

        Optional<Post> post = postRepository.findById(postId);
        return post.map(value -> value.getLikes().size()).orElse(0);
    }
}
