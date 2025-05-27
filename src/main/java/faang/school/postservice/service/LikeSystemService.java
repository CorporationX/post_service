package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.likesystem.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeSystemService {

    private final LikeRepository likeRepository;
    private final CommentService commentService;
    private final PostService postService;
    private final UserServiceClient userServiceClient;

    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto addLikePost(LikeDto likeDto) {
        Post post = postService.getPostById(likeDto.postId());
        //Проверка на наличие поста в системе

        userServiceClient.getUser(likeDto.userId(), "12345");//Проверка на наличие юзера в системе, не работает метод

        if (post.getLikes().stream()
                .anyMatch(like -> Objects.equals(like.getUserId(), likeDto.userId()))) {
            throw new IllegalArgumentException("The like is already worth it");
        }//Проверка на "стоит уже лайк на посте или нет"


        post.getComments()
                .forEach(comment -> {
                    if (comment.getLikes().stream()
                            .anyMatch(like -> Objects.equals(like.getUserId(), likeDto.userId()))) {
                        throw new IllegalArgumentException("You can't like a post and a comment at the same time");
                    }
                });
        //Проверка на то, что нет лайка от этого пользователя в комментариях

        Like like = createPostLike(post, likeDto.userId());
        post.getLikes().add(like);
        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto deleteLikePost(Long id) {
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The like with id = " + id + " does not exist"));

        likeRepository.delete(like);
        like.getPost().getLikes().remove(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto addLikeComment(LikeDto likeDto) {
        Comment comment = commentService.getCommentById(likeDto.commentId());
        //Проверка на наличие комментария в системе

        userServiceClient.getUser(likeDto.userId(), "12345");//Проверка на наличие юзера в системе, не работает метод

        if(comment.getLikes().stream()
                .anyMatch(like -> Objects.equals(like.getUserId(), likeDto.userId()))){
            throw new IllegalArgumentException("The like is already worth it");
        }//Стоит ли лайк на комменте или нет

        if (comment.getPost().getLikes().stream()
                .anyMatch(like -> Objects.equals(like.getUserId(), likeDto.userId()))) {
            throw new IllegalArgumentException("Like the post");
        }
        //Проверка на то, что нет лайка от этого пользователя на посте

        Like like = createCommentLike(comment, likeDto.userId());
        comment.getLikes().add(like);
        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto deleteLikeComment(Long id) {
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The like with id = " + id + " does not exist"));

        likeRepository.delete(like);
        like.getComment().getLikes().remove(like);

        return likeMapper.toDto(like);
    }

    private Like createPostLike(Post post, Long userId) {
        return Like.builder()
                .userId(userId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Like createCommentLike(Comment comment, Long userId) {
        return Like.builder()
                .userId(userId)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
