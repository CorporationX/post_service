package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.exception.handler.ErrorResponse;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * REST-контроллер для управления постами пользователей и проектов.
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
@Tag(name = "Posts", description = "Operations with posts (drafts and publications)")
public class PostController {
    private final PostService postService;


    /**
     * Создание черновика
     */
    @Operation(
            summary = "Create a draft post",
            description = "Creates a draft based on the provided request data. Validates referenced user/project.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Draft created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User or project not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public PostResponseDto create(@Valid @RequestBody CreatePostRequestDto dto) {
        return postService.createDraft(dto);
    }

    /**
     * Публикация поста
     */
    @Operation(
            summary = "Publish a post",
            description = "Moves a post from draft to published state.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post published",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409",
                            description = "State conflict: post deleted or already published",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping("/{id}/publish")
    public PostResponseDto publish(
            @Parameter(description = "Post identifier", example = "123")
            @PathVariable @Positive long id) {
        return postService.publish(id);
    }


    /**
     * Обновление контента поста
     */
    @Operation(
            summary = "Update a post",
            description = "Updates post data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error or illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "State conflict",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PutMapping("/{id}")
    public PostResponseDto update(
            @Parameter(description = "Post identifier", example = "123")
            @PathVariable @Positive long id,
            @Valid @RequestBody UpdatePostRequestDto dto) {
        return postService.update(id, dto);
    }


    /**
     * Мягкое удаление поста
     */
    @Operation(
            summary = "Soft delete a post",
            description = "Marks a post as deleted without physical removal.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Post marked as deleted", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "State conflict",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/{id}")
    public void softDelete(
            @Parameter(description = "Post identifier", example = "123")
            @PathVariable @Positive long id) {
        postService.softDelete(id);
    }


    /**
     * Получение поста по id
     */
    @Operation(
            summary = "Get post by ID",
            description = "Returns a post by its identifier.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Post not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/{id}")
    public PostResponseDto getById(
            @Parameter(description = "Post identifier", example = "123")
            @PathVariable @Positive long id) {
        return postService.getById(id);
    }


    /**
     * Все черновики пользователя
     */
    @Operation(
            summary = "User's draft posts",
            description = "Returns all draft posts for a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Draft list",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/users/{userId}/drafts")
    public List<PostResponseDto> draftsByUser(
            @Parameter(description = "User ID", example = "42")
            @PathVariable @Positive long userId) {
        return postService.getDraftsByUser(userId);
    }


    /**
     * Все черновики проекта
     */
    @Operation(
            summary = "Project's draft posts",
            description = "Returns all draft posts for a specific project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Draft list",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/projects/{projectId}/drafts")
    public List<PostResponseDto> draftsByProject(
            @Parameter(description = "Project ID", example = "1001")
            @PathVariable @Positive long projectId) {
        return postService.getDraftsByProject(projectId);
    }


    /**
     * Все опубликованные посты пользователя
     */
    @Operation(
            summary = "User's published posts",
            description = "Returns all published posts for a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Published posts list",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/users/{userId}/published")
    public List<PostResponseDto> publishedByUser(
            @Parameter(description = "User ID", example = "42")
            @PathVariable @Positive long userId) {
        return postService.getPublishedByUser(userId);
    }


    /**
     * Все опубликованные посты проекта
     */
    @Operation(
            summary = "Project's published posts",
            description = "Returns all published posts for a specific project.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Published posts list",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PostResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Illegal argument",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping("/projects/{projectId}/published")
    public List<PostResponseDto> publishedByProject(
            @Parameter(description = "Project ID", example = "1001")
            @PathVariable @Positive long projectId) {
        return postService.getPublishedByProject(projectId);
    }
}