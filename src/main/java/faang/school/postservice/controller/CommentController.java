package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/v1/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Создание комментария")
    public CommentReadDto create(@Valid @RequestBody CommentCreateDto createDto) {
        return commentService.create(createDto);
    }

    @PutMapping
    @Operation(summary = "Обновление комментария")
    public CommentReadDto update(@Valid @RequestBody CommentUpdateDto updateDto) {
        return commentService.update(updateDto);
    }

    @GetMapping
    @Operation(summary = "Получение комментариев по id поста")
    public List<CommentReadDto> getCommentsByPostId(
            @Parameter(description = "Id поста, комментарии которого надо получить", required = true)
            @RequestParam(required = false)
            long postId
    ) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Удаление комментария")
    public void remove(
            @PathVariable
            @Parameter(description = "Id комментария, который надо удалить", required = true)
            long commentId
    ) {
        commentService.remove(commentId);
    }


    @PostMapping("/{commentId}")
    @Operation(summary = "Добавление изображения")
    public CommentReadDto addImage(@PathVariable long commentId, @RequestBody MultipartFile file) {
        return commentService.uploadImage(commentId, file);
    }

    @DeleteMapping("/attachments/{imageId}")
    @Operation(summary = "Удаление изображения")
    public void removeImage(@PathVariable long imageId) {
        commentService.removeImage(imageId);
    }

    @GetMapping("/attachments/{imageId}")
    @Operation(summary = "Получение изображения")
    public InputStream downloadImage(@PathVariable long imageId) {
        return commentService.downloadImage(imageId);
    }
}
