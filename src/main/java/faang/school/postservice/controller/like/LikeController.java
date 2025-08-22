package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для управления лайками под постами и комментариями.
 * <p>
 * Позволяет пользователям ставить и убирать лайки с постов и комментариев.
 * Методы контроллера соответствуют CRUD операциям над лайками:
 * добавление лайка (POST) и удаление лайка (DELETE).
 * Все операции работают с текущим аутентифицированным пользователем.
 * </p>
 * <p>
 * Пути:
 * <ul>
 *     <li>POST /likes/posts/{postId} — поставить лайк посту</li>
 *     <li>DELETE /likes/posts/{postId} — убрать лайк с поста</li>
 *     <li>POST /likes/comments/{commentId} — поставить лайк комментарию</li>
 *     <li>DELETE /likes/comments/{commentId} — убрать лайк с комментария</li>
 * </ul>
 *
 * @author agent
 * @since 12.08.2025
 */
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService service;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> addLikeToPost(@PathVariable long postId) {
        service.addLikeToPost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> removeLikeFromPost(@PathVariable long postId) {
        service.removeLikeFromPost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> addLikeToComment(@PathVariable long commentId) {
        service.addLikeToComment(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> removeLikeFromComment(@PathVariable long commentId) {
        service.removeLikeFromComment(commentId);
        return ResponseEntity.noContent().build();
    }
}