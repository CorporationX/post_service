package faang.school.postservice.controller;

import faang.school.postservice.annotation.ValidHashtag;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Посты", description = "Контроллер для работы с постами")
public class PostController {
    private final PostService postService;

    @PostMapping
    @Operation(summary = "Создать пост", description = "Введите данные поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост создан", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public PostDto createPost(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping
    @Operation(summary = "Обновить пост", description = "Введите данные поста, чтобы его обновить")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост обновлен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public PostDto updatePost(@RequestBody @Valid PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/publish")
    @Operation(summary = "Опубликовать пост", description = "Введите данные поста, который нужно опубликовать")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост отправлен на публикацию", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public PostDto publishPost(@RequestBody @Valid PostDto postDto) {
        return postService.publishPost(postDto);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Удалить пост", description = "Введите идентификатор поста, который нужно удалить")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост удален", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public PostDto deletePost(@PathVariable Long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Найти пост по идентификатору", description = "Введите идентификатор поста")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация поста получена", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные поста"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public PostDto getPostByPostId(@PathVariable Long postId) {
        return postService.getPostDtoById(postId);
    }

    @GetMapping("/drafts/user/{userId}")
    @Operation(summary = "Получить неопубликованные посты юзера",
            description = "Введите идентификатор юзера, чтобы посмотреть его неопубликованные посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список неопубликованных постов юзера получен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public List<PostDto> getAllDraftPostsByUserId(@PathVariable Long userId) {
        return postService.getAllDraftPostsByUserId(userId);
    }

    @GetMapping("/drafts/project/{projectId}")
    @Operation(summary = "Получить неопубликованные посты проекта",
            description = "Введите идентификатор проекта, чтобы посмотреть его неопубликованные посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Списк неопубликованных постов проекта получен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public List<PostDto> getAllDraftPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllDraftPostsByProjectId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    @Operation(summary = "Получить опубликованные посты юзера",
            description = "Введите идентификатор юзера, чтобы посмотреть его опубликованные посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Списк опубликованных постов юзера получен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public List<PostDto> getAllPublishPostsByUserId(@PathVariable Long userId) {
        return postService.getAllPublishPostsByUserId(userId);
    }

    @GetMapping("/published/project/{projectId}")
    @Operation(summary = "Получить опубликованные посты проекта",
            description = "Введите идентификатор проекта, чтобы посмотреть его опубликованные посты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Списк опубликованных постов проекта получен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public List<PostDto> getAllPublishPostsByProjectId(@PathVariable Long projectId) {
        return postService.getAllPublishPostsByProjectId(projectId);
    }

    @GetMapping("/hashtag")
    public List<PostDto> findPostsByHashtag(@RequestParam @ValidHashtag String hashtagName,
                                            @RequestParam int page,
                                            @RequestParam int size) {
        return postService.findPostsByHashtag(hashtagName, page, size);
    }

    @GetMapping("/hashtag/cache")
    public List<PostDto> findPostsByHashtagForCache(@RequestParam @ValidHashtag String hashtagName,
                                                    @RequestParam int page,
                                                    @RequestParam int size) {
        return postService.findPostsByHashtagForCache(hashtagName, page, size);
    }

    @GetMapping("/list/ids")
    public List<PostDto> findPostsByIds(@RequestParam List<Long> postIds) {
        return postService.getPostsDtoByIds(postIds);
    }
}