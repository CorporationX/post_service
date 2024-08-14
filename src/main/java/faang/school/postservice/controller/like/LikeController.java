package faang.school.postservice.controller.like;

import faang.school.postservice.config.redis.like.LikePostPublisher;
import faang.school.postservice.controller.LikeToComment;
import faang.school.postservice.controller.LikeToPost;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Tag(name = "Система лайков", description = "Контроллер для работы с лайками")
@Validated
public class LikeController {

    private final LikeService likeService;
    private final LikePostPublisher likePostPublisher;

    @GetMapping("/post/{postId}")
    @Operation(summary = "Получить лайки поста", description = "Введите идентификатор поста, чтобы получить лайки поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи получены", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public List<UserDto> getLikesUsersByPostId(@PathVariable long postId) {
        return likeService.getLikesUsersByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    @Operation(summary = "Получить список пользователей, лайкнувших коммент",
            description = "Введите идентификатор коммента, чтобы получить список пользователей, лайкнувших коммент")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователи получены", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public List<UserDto> getLikesUsersByCommentId(@PathVariable long commentId) {
        return likeService.getLikesUsersByCommentId(commentId);
    }

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Поставить лайк на пост",
            description = "Введите данные поста, чтобы поставить ему лайк")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Лайк добавлен посту", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public LikeDto addLikeToPost(@Validated(LikeToPost.class) @RequestBody LikeDto likeDto) {
        LikeDto result = likeService.addLikeToPost(likeDto);
        likePostPublisher.createBanEvent(likeDto);
        return result;
    }

    @DeleteMapping("/post/{postId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить лайк с поста",
            description = "Введите идентификатор поста и идентификатор пользователя, чтобы удалить лайк с поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк у поста удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public void deleteLikeFromPost(@Positive @PathVariable("postId") long postId,
                                   @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromPost(postId, userId);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Поставить лайк на комментарий",
            description = "Введите данные комментария, чтобы поставить ему лайк")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Лайк добавлен комментарию", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = LikeDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public LikeDto addLikeToComment(@Validated(LikeToComment.class) @RequestBody LikeDto likeDto) {
        return likeService.addLikeToComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить лайк с комментария",
            description = "Введите идентификатор комментария и идентификатор пользователя, чтобы удалить лайк с комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лайк у комментария удален"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public void deleteLikeFromComment(@Positive @PathVariable("commentId") long commentId,
                                      @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromComment(commentId, userId);
    }
}