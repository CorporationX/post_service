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
    public Like addToPost(Like entity) {
        checkUserExist(entity.getUserId());

        Optional<Like> like = likeRepository.findByPostIdAndUserId(entity.getPost().getId(), entity.getUserId());
        if (like.isPresent()) {
            log.error("PostId = {} and UserId = {}. {}", entity.getPost().getId(), entity.getUserId(), SECOND_LIKE);
            throw new RuntimeException(SECOND_LIKE);
        }

        Optional<Post> post = postRepository.findById(entity.getPost().getId());
        if (post.isEmpty()) {
            log.error("Post = {}. {}", entity.getPost().getId(), POST_NOT_EXIST);
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
    public Like addToComment(Like entity) {
        checkUserExist(entity.getUserId());

        Optional<Like> like = likeRepository.findByCommentIdAndUserId(entity.getComment().getId(), entity.getUserId());
        if (like.isPresent()) {
            log.error("CommentId = {} and UserId = {}. {}", entity.getComment().getId(), entity.getUserId(), SECOND_LIKE);
            throw new RuntimeException(SECOND_LIKE);
        }

        Optional<Comment> comment = commentRepository.findById(entity.getComment().getId());
        if (comment.isEmpty()) {
            log.error("Comment = {}. {}", entity.getComment().getId(), COMMENT_NOT_EXIST);
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

    @Transactional
    public void deletePostLike(Long likeId, Long postId) {
        checkLikeExist(likeId);
        if (!postRepository.existsById(postId)) {
            log.error("PostId = {}. {}", postId, POST_NOT_EXIST);
            throw new RuntimeException(POST_NOT_EXIST);
        }

        Like like = likeRepository.findById(likeId).get();
        Post post = postRepository.findById(postId).get();
        post.getLikes().remove(like);
        postRepository.save(post);

        likeRepository.delete(like);
    }

    @Transactional
    public void deleteCommentLike(Long likeId, Long commentId) {
        checkLikeExist(likeId);
        if (!commentRepository.existsById(commentId)) {
            log.error("CommentId = {}. {}", commentId, COMMENT_NOT_EXIST);
            throw new RuntimeException(COMMENT_NOT_EXIST);
        }

        Like like = likeRepository.findById(likeId).get();
        Comment comment = commentRepository.findById(commentId).get();
        comment.getLikes().remove(like);
        commentRepository.save(comment);

        likeRepository.delete(like);
    }

    private void checkLikeExist(Long likeId) {
        if (!likeRepository.existsById(likeId)) {
            log.error("LikeId = {}. {}", likeId, LIKE_NOT_EXIST);
            throw new RuntimeException(LIKE_NOT_EXIST);
        }
    }
}
