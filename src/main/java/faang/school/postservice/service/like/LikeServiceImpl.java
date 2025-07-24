package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final UserContext userContext;

    @Override
    public void addLikePost(Long postId, Long userId) {
        log.info("Проверяем, есть ли уже лайк от пользователя {} на посте {}", userId, postId);
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            log.info("Лайк уже существует от пользователя {} на посте {}", userId, postId);
            throw new IllegalArgumentException("Лайк под этим постом уже оставлен пользователем с id: " + userId + " id поста: " + postId);
        }
        log.info("Создаем новый лайк от пользователя {} на посте {}", userId, postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост с id " + postId + " не найден"));
        Like like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        likeRepository.save(like);
        log.info("Лайк успешно добавлен от пользователя {} на пост {}", userId, postId);
    }

    @Override
    public void removeLikePost(Long postId, Long userId) {
        log.info("Проверяем, есть ли лайк от пользователя {} на посте {}", userId, postId);
        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            log.info("Лайк не найден от пользователя {} на посте {}", userId, postId);
            throw new IllegalArgumentException("Лайк под этим постом не найден у пользователя с id: " + userId + " id поста: " + postId);
        }
        log.info("Удаляем лайк от пользователя {} на посте {}", userId, postId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Лайк успешно удален от пользователя {} на пост {}", userId, postId);
    }

    @Override
    public void addLikeComment(Long commentId, Long userId) {
        log.info("Проверяем, есть ли уже лайк от пользователя {} на комментарии {}", userId, commentId);
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            log.info("Лайк уже существует от пользователя {} на комментарии {}", userId, commentId);
            throw new IllegalArgumentException("Лайк под этим комментарием уже оставлен пользователем с id: " + userId + " id комментария: " + commentId);
        }

        log.info("Ищем в базе комментарий с id {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий с id " + commentId + " не найден"));
        log.info("Найден комментарий: {}", comment);

        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();

        log.info("Записываем в базу новый лайк от пользователя {} на комментарии {}", userId, commentId);
        Like likeResult = likeRepository.save(like);

        log.info("Лайк успешно добавлен от пользователя {} на комментарий {}", userId, commentId);
    }

    @Override
    public void removeLikeComment(Long commentId, Long userId) {
        log.info("Проверяем, есть ли лайк от пользователя {} на комментарии {}", userId, commentId);
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new IllegalArgumentException("Лайк под этим комментарием не найден у пользователя с id: " + userId + " id комментария: " + commentId);
        }
        log.info("Удаляем лайк от пользователя {} на комментарии {}", userId, commentId);
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Лайк успешно удален от пользователя {} на комментарий {}", userId, commentId);
    }
}
