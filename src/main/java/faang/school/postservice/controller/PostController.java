package faang.school.postservice.controller;

import faang.school.postservice.controller.common.ApiExceptionDto;
import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/draft")
    @Operation(method = "POST",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Черновик поста,",
                    required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Черновик успешно создан"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
            })
    public ResponseEntity<Void> createDraft(@RequestBody @Validated PostDraftDto postDraftDto) {
        postService.createPostDraft(postDraftDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/publishPost")
    @Operation(method = "PUT", parameters = {
            @Parameter(name = "postId", required = true, description = "id поста")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Пост,"
                    + " публикуемый пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пост успешно опубликован"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                    content = @Content(schema = @Schema(implementation = ApiExceptionDto.class))),
                    @ApiResponse(responseCode = "422", description = "Произошла бизнес-ошибка",
                            content = @Content(schema = @Schema(implementation = ApiExceptionDto.class)))
            })
    public ResponseEntity<Void> publishPost(@RequestBody(required = false) @Validated PostDraftDto postDraftDto,
                                            @RequestParam(required = false) Long postId) {
        postService.publishPost(postId, postDraftDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/markedPostAsDeleted/{postId}")
    @Operation(method = "PUT", parameters = {
            @Parameter(name = "postId", required = true, description = "id поста")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Пост,"
                    + " удаляемый пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пост успешно удален пользователем"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 403\n  "
                                                    + "\"message\":\"Текущему пользователю операция недоступна\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            }),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<PostDto> markPostAsDeleted(@PathVariable @NotNull(message = "PostId не может быть равен null")
                                                         Long postId) {
        PostDto postDto = postService.markPostAsDeleted(postId);
        return ResponseEntity.ok(postDto);
    }

    @PutMapping("/update/{postId}")
    @Operation(method = "PUT", parameters = {
            @Parameter(name = "postId", required = true, description = "id поста")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Пост,"
                    + " обновляемый пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пост успешно обновлен пользователем"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "403", description = "Отказано в доступе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 403\n  "
                                                    + "\"message\":\"Текущему пользователю операция недоступна\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            }),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<PostDto> update(@RequestBody @Validated PostDto postDto, @PathVariable @NotNull(message = "PostId не может быть равен null") Long postId) {
        PostDto result = postService.updatePost(postDto, postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{postId}")
    @Operation(method = "GET", parameters = {
            @Parameter(name = "postId", required = true, description = "id поста")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Пост,"
                    + " запрашиваемый пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пост найден"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<PostDto> getPost(@PathVariable @NotNull(message = "PostId не может быть равен null")
                                               Long postId) {
        PostDto post = postService.findById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/byAuthor/{userId}")
    @Operation(method = "GET", parameters = {
            @Parameter(name = "userId", required = true, description = "id автора")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Посты,"
                    + " запрашиваемые пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Посты найдены"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<List<PostDto>> getPostsByAuthor(@PathVariable @NotNull(
            message = "UserId не может быть равен null") Long userId) {
        List<PostDto> allPostsByAuthorId = postService.getAllPostsByAuthorId(userId);
        return ResponseEntity.ok(allPostsByAuthorId);
    }

    @GetMapping("/byProject/{projectId}")
    @Operation(method = "GET", parameters = {
            @Parameter(name = "projectId", required = true, description = "id проекта")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Посты,"
                    + " запрашиваемые пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Посты найдены"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<List<PostDto>> getPostsByProject(@PathVariable @NotNull(
            message = "ProjectId не может быть равен null"
    ) Long projectId) {
        List<PostDto> allPostsByProjectId = postService.getAllPostsByProjectId(projectId);
        return ResponseEntity.ok(allPostsByProjectId);
    }

    @GetMapping("/publishedPostsByAuthor/{userId}")
    @Operation(method = "GET", parameters = {
            @Parameter(name = "userId", required = true, description = "id автора")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Опубликованные посты,"
                    + " запрашиваемые пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Посты найдены"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<List<PostDto>> getPublishedPostsByAuthor(@PathVariable @NotNull(
            message = "UserId не может быть равен null"
    ) Long userId) {
        List<PostDto> allPublishedPostsByAuthorId = postService.getAllPublishedPostsByAuthorId(userId);
        return ResponseEntity.ok(allPublishedPostsByAuthorId);
    }

    @GetMapping("/publishedPostsByProject/{projectId}")
    @Operation(method = "GET", parameters = {
            @Parameter(name = "projectId", required = true, description = "id проекта")
    },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Опубликованные посты,"
                    + " запрашиваемые пользователем", required = true),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Посты найдены"),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка"),
                    @ApiResponse(responseCode = "404", description = "Не удалось найти сущность в базе",
                            content = {
                                    @Content(examples = {
                                            @ExampleObject(name = "ErrorResponse", value = "{\n  \"status\": 404\n  "
                                                    + "\"message\":\"Не удалось найти сущность в базе\"\n  "
                                                    + "\"exceptionMessage\": \"Текст ошибки\"\n  "
                                                    + "\"timestamp\": 1752087981\n}", description = "Объект ошибки")
                                    })
                            })
            })
    public ResponseEntity<List<PostDto>> getPublishedPostsByProject(@PathVariable @NotNull(
            message = "ProjectId не может быть равен null"
    ) Long projectId) {
        List<PostDto> allPublishedPostsByProjectId = postService.getAllPublishedPostsByProjectId(projectId);
        return ResponseEntity.ok(allPublishedPostsByProjectId);
    }
}
