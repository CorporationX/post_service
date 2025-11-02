package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.handler.ErrorResponse;
import faang.school.postservice.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @Operation(
            summary = "Список пользователей, лайкнувших пост",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found", content = @Content)
            },
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            description = "ID текущего пользователя",
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "integer", format = "int64"),
                            example = "1"
                    )
            }
    )
    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersLikersByPostId(@PathVariable long postId) {
        return likeService.getUsersLikesByPostId(postId);
    }

    @Operation(
            summary = "Список пользователей, лайкнувших комментарий",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "Comment not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            },
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            description = "ID текущего пользователя",
                            in = ParameterIn.HEADER,
                            schema = @Schema(type = "integer", format = "int64"),
                            example = "1"
                    )
            }
    )
    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikerByCommentId(@PathVariable long commentId) {
        return likeService.getUsersLikesByCommentId(commentId);
    }
}