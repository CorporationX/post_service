package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeViewDto;
import faang.school.postservice.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с лайками постов и комментариев.
 * <p>
 * Основные методы:
 * <ul>
 *   <li>{@link #likePost(Long)} - добавляет лайк к указанному посту</li>
 *   <li>{@link #unlikePost(Long)} - удаляет лайк с указанного поста</li>
 *   <li>{@link #likeComment(Long)} - добавляет лайк к указанному комментарию</li>
 *   <li>{@link #unlikeComment(Long)} - удаляет лайк с указанного комментария</li>
 * </ul>
 * <p>
 * Все методы требуют аутентификации пользователя.
 *
 * @author gulnaz21
 */
@Slf4j
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Like Controller",
        description = "Контроллер для работы с лайками постов и комментариев. " +
                "Все методы требуют аутентификации пользователя."
)
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;

    @Operation(
            summary = "Добавляет лайк к посту.",
            description = "Параметры: postId - идентификатор поста, к которому добавляется лайк. " +
                    "Возвращает LikeViewDto, содержащий информацию о добавленном лайке."
    )
    @PostMapping("/posts/{postId}")
    public LikeViewDto likePost(@PathVariable
                                @NotNull Long postId) {
        long userId = userContext.getUserId();
        return likeService.likePost(postId, userId);
    }

    @Operation(
            summary = "Удаляет лайк с поста.",
            description = "Параметры: postId - идентификатор поста, с которого нужно удалить лайк. " +
                    "Возвращает ResponseEntity, с пустым телом и статусом 204."
    )
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> unlikePost(@PathVariable
                                           @NotNull Long postId) {
        long userId = userContext.getUserId();
        likeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Добавляет лайк к комментарию.",
            description = "Параметры: commentId - идентификатор комментария, к которому добавляется лайк. " +
                    "Возвращает LikeViewDto, содержащий информацию о добавленном лайке."
    )
    @PostMapping("/comments/{commentId}")
    public LikeViewDto likeComment(@PathVariable
                                   @NotNull Long commentId) {
        long userId = userContext.getUserId();
        return likeService.likeComment(commentId, userId);
    }

    @Operation(
            summary = "Удаляет лайк с комментария.",
            description = "Параметры: commentId - идентификатор комментария, с которого удаляется лайк. " +
                    "Возвращает ResponseEntity с пустым телом и статусом 204."
    )
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> unlikeComment(@PathVariable
                                              @NotNull Long commentId) {
        long userId = userContext.getUserId();
        likeService.unlikeComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
