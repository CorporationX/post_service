package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления лайками под постами и комментариями.
 * <p>
 * Позволяет пользователям ставить и убирать лайки с постов и комментариев.
 * Позволяет получать список пользователей поставившим лайк под постом/коментарием.
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
 *     <li>GET /likes/posts/{postId}/likes — получить список пользователей поставивших лайк посту</li>
 *     <li>GET /likes/comments/{commentId}/likes — получить список пользователей поставивших лайк коментарию</li>
 * </ul>
 *
 * @author agent
 * @since 12.08.2025
 */
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> addLikeToPost(@PathVariable long postId) {
        likeService.addLikeToPost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> removeLikeFromPost(@PathVariable long postId) {
        likeService.removeLikeFromPost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> addLikeToComment(@PathVariable long commentId) {
        likeService.addLikeToComment(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> removeLikeFromComment(@PathVariable long commentId) {
        likeService.removeLikeFromComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<UserDto>> getListUsersWhoLikesThisPost(@PathVariable long postId) {
        return ResponseEntity.ok(likeService.getListUsersWhoLikesThisPost(postId));
    }

    @GetMapping("/comments/{commentId}/likes")
    public ResponseEntity<List<UserDto>> getListUsersWhoLikesThisComment(@PathVariable long commentId) {
        return ResponseEntity.ok(likeService.getListUsersWhoLikesThisComment(commentId));
    }
}