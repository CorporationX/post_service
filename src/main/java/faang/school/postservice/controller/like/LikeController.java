package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Tag(name = "Система лайков", description = "Контроллер для работы с лайками")
public class LikeController {

    private final LikeService likeService;

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
}
