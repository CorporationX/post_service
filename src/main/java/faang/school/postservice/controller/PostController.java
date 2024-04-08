package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PostController", description = "Посылаем запросы в PostController")
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Создание черновика поста",
            description = "Получает сообщение от автора/проекта и создаёт черновик",
            parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "id пользователя", required = true)}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пост создан"),
            @ApiResponse(responseCode = "500", description = "Нет доступа к базе данных пользователей или проектов")
    })
    @PostMapping("/draft")
    public PostDto createDraft(@Valid @RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @Operation(
            summary = "Публикация поста по id"
    )
    @PutMapping("/publish/{postId}")
    public PostDto publishPost(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.publish(postId);
    }

    @Operation(
            summary = "Обновление поста",
            description = "Получает PostDto и обновляет post"
    )
    @PutMapping
    public PostDto updatePost(@Valid @RequestBody PostDto postDto) {
        return postService.update(postDto);
    }

    @Operation(
            summary = "Удаление поста по id",
            description = "Помечает пост как удаленный"
    )
    @DeleteMapping("/{postId}")
    public PostDto removePostSoftly(@PathVariable @Positive(message = "Id must be greater than zero") long postId) {
        return postService.deletePost(postId);
    }

    @Operation(
            summary = "Получение поста по id"
    )
    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPostById(id);
    }

    @Operation(
            summary = "Получение черновиков по id пользователя"
    )
    @GetMapping("/author/{id}/drafts")
    public List<PostDto> getDraftsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByAuthorId(id);
    }

    @Operation(
            summary = "Получение черновиков по id проекта"
    )
    @GetMapping("/project/{id}/drafts")
    public List<PostDto> getDraftsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getDraftsByProjectId(id);
    }

    @Operation(
            summary = "Получение опубликованных постов по id пользователя"
    )
    @GetMapping("/author/{id}")
    public List<PostDto> getPostsByAuthorId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPublishedPostsByAuthorId(id);
    }

    @Operation(
            summary = "Получение опубликованных постов по id проекта"
    )
    @GetMapping("/project/{id}")
    public List<PostDto> getPostsByProjectId(@PathVariable @Positive(message = "Id must be greater than zero") long id) {
        return postService.getPublishedPostsByProjectId(id);
    }
}