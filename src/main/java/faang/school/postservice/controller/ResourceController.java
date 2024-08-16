package faang.school.postservice.controller;

import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
@Tag(name = "Ресурсы", description = "Контроллер для работы с базой S3 через сущность Resource")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping(value = "/post/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Сохранить изображение", description = "Прикрепить изображение к посту")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображение сохранено в базу", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public ResourceDto addImage(
            @PathVariable Long postId, @RequestBody @NonNull MultipartFile file) {
        return resourceService.addImage(postId, file);
    }

    @PostMapping(value = "/post/list/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Сохранить изображения посту", description = "Введите идентификатор поста и список изображений, которые нужно сохранить в базу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Изображения сохранены в базу", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public List<ResourceDto> addImages(
            @PathVariable Long postId, @RequestBody @NonNull List<MultipartFile> files) {
        return resourceService.addImages(postId, files);
    }

    @DeleteMapping("/{resourceId}")
    @Operation(summary = "Удалить resource из базы", description = "Введите идентификатор resource, который нужно удалить")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resource удален", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ResourceDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public ResourceDto deleteResource(@PathVariable Long resourceId) {
        return resourceService.deleteResource(resourceId);
    }
}
