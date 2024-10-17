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
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Like addToPost(Long postId, Like tempLike) {
        checkUserExist(tempLike.getUserId());

        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new NoSuchElementException(Util.POST_NOT_EXIST);
        }

        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, tempLike.getUserId());
        if (like.isPresent()) {
            throw new RuntimeException(Util.SECOND_LIKE);
        }

        Post post = postOptional.get();
        tempLike.setPost(post);
        Like newLike = likeRepository.save(tempLike);
        List<Like> likeList = post.getLikes();
        likeList.add(newLike);
        post.setLikes(likeList);

        return newLike;
    }

    @Transactional
    public Like addToComment(Long commentId, Like entity) {
        checkUserExist(entity.getUserId());

        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        if (commentOptional.isEmpty()) {
            throw new NoSuchElementException(Util.COMMENT_NOT_EXIST);
        }

        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, entity.getUserId());
        if (like.isPresent()) {
            throw new RuntimeException(Util.SECOND_LIKE);
        }
        Comment comment = commentOptional.get();
        entity.setComment(comment);
        Like newLike = likeRepository.save(entity);
        List<Like> likeList = comment.getLikes();
        likeList.add(newLike);
        comment.setLikes(likeList);

        return newLike;
    }


    @Transactional
    public void removeFromPost(Long postId, Long userId) {
        checkUserExist(userId);
        checkPostExist(postId);

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(like -> {
                    Post post = postRepository.findById(postId).get();
                    post.getLikes().remove(like);
                    postRepository.save(post);
                    likeRepository.deleteByPostIdAndUserId(postId, userId);
                },
                () -> {
                    throw new NoSuchElementException(Util.LIKE_NOT_EXIST);
                });
    }

    @Transactional
    public void removeFromComment(Long commentId, Long userId) {
        checkUserExist(userId);
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException(Util.COMMENT_NOT_EXIST);
        }

        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresentOrElse(like -> {
                    Comment comment = commentRepository.findById(commentId).get();
                    comment.getLikes().remove(like);
                    commentRepository.save(comment);
                    likeRepository.deleteByCommentIdAndUserId(commentId, userId);
                },
                () -> {
                    throw new NoSuchElementException(Util.LIKE_NOT_EXIST);
                });
    }

    @Transactional
    public int getLikesByPost(Long postId) {
        checkPostExist(postId);

        Optional<Post> post = postRepository.findById(postId);
        return post.map(value -> value.getLikes().size()).orElse(0);
    }

    private void checkUserExist(Long userId) {
        //TODO: необходимо реализовать контроллер для UserService
        //UserDto userDto = userServiceClient.getUser(userId);

        // Поскольку контроллера для UserService пока нет, создаем заглушку
        UserDto userDto = new UserDto(1L, "Alex", "alex@gmail.com", List.of(), List.of());

        if (userDto.getId() == null) {
            throw new NoSuchElementException(Util.USER_NOT_EXIST);
        }
    }

    private void checkPostExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NoSuchElementException(Util.POST_NOT_EXIST);
        }
    }
}
