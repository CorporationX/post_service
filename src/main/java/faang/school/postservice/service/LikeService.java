package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public ResponseEntity<?> toggleLikePost(LikePostRequest request) {
        Post post;
        UserDto userDto;

        try {
            post = postRepository.findById(request.postId())
                    .orElseThrow(() -> new EntityNotFoundException("Пост с id " + request.postId() + " не был найден"));
            userDto = getUserDto(request.userId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        List<Like> likes = new ArrayList<>(post.getLikes() == null ? Collections.emptyList() : post.getLikes());
        boolean likeAlreadyExists = likes.stream().anyMatch(like -> like.getUserId() == userDto.id());

        if (likeAlreadyExists) {
            post.setLikes(likes.stream().filter(like -> like.getUserId() != userDto.id()).toList());
            likeRepository.deleteByPostIdAndUserId(post.getId(), userDto.id());
        } else {
            Like newLike = Like.builder()
                    .userId(userDto.id())
                    .post(post)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(newLike);
            likes.add(newLike);
            post.setLikes(likes);
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> toggleLikeComment(LikeCommentRequest request) {
        Comment comment;
        UserDto userDto;

        try {
            comment = commentRepository.findById(request.commentId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Комментарий с id " + request.commentId() + " не был найден"));
            userDto = getUserDto(request.userId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        List<Like> likes = new ArrayList<>(comment.getLikes() == null ? Collections.emptyList() : comment.getLikes());
        boolean likeAlreadyExists = likes.stream().anyMatch(like -> like.getUserId() == userDto.id());

        if (likeAlreadyExists) {
            comment.setLikes(likes.stream().filter(like -> like.getUserId() != userDto.id()).toList());
            likeRepository.deleteByCommentIdAndUserId(comment.getId(), userDto.id());
        } else {
            Like newLike = Like.builder()
                    .userId(userDto.id())
                    .comment(comment)
                    .createdAt(LocalDateTime.now())
                    .build();
            likeRepository.save(newLike);
            likes.add(newLike);
            comment.setLikes(likes);
        }

        return ResponseEntity.ok().build();
    }

    private UserDto getUserDto(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        if (userDto == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не был найден");
        }
        return userDto;
    }
}
