package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * LikeController — контроллер для получения списка UserDto лайков к комментариям и постам.
 *
 * @author bozya
 * @since 12.08.2025
 */
@Slf4j
@RequestMapping("/likes")
@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    /**
     * Получает список всех пользователей, поставивших лайк указанному посту.
     *
     * @apiNote Возвращает список UserDto объектов, содержащих информацию о пользователях
     * @param postId идентификатор поста, для которого запрашиваются лайки
     * @return ResponseEntity со списком UserDto и статусом 200 OK
     * @throws EntityNotFoundException если пост с указанным ID не найден
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<UserViewDto>> getAllLikesFromUsersToPost(@PathVariable Long postId) {
        List<UserViewDto> users = service.getUsersWhoLikedPost(postId);

        return ResponseEntity.ok(users);
    }

    /**
     * Получает список всех пользователей, поставивших лайк указанному комментарию.
     *
     * @apiNote Возвращает список UserDto объектов, содержащих информацию о пользователях,
     * которые поставили лайк комментарию. Для корректной работы требуется указать
     * идентификатор поста, к которому принадлежит комментарий.
     *
     * @param commentId идентификатор комментария, для которого запрашиваются лайки
     * @return ResponseEntity со списком UserDto и статусом 200 OK
     */
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<UserViewDto>> getAllLikesFromUsersToComment(@PathVariable Long commentId) {
        List<UserViewDto> users = service.getUsersWhoLikedComment(commentId);

        return ResponseEntity.ok(users);
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Void> likeToPost(@PathVariable Long postId) {
        service.addLikeToPost(postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<Void> likeToComment(@PathVariable Long commentId) {
        service.addLikeToComment(commentId);
        return ResponseEntity.ok().build();
    }


}