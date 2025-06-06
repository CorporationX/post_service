package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Comment controller", description = "Контроллер для управления комментариями")
public interface CommentController {

    @Operation(
            summary = "Создания комментария",
            description = "Сохраняет комментарий в базе",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponse(responseCode = "200", description = "Комментарий создан")
    ResponseEntity<CommentDtoResponse> createComment(CommentCreateDto commentDto);

    @Operation(
            summary = "Обновления комментария",
            description = "Обновляет комментарий в базе",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponse(responseCode = "200", description = "Комментарий обновлен")
    ResponseEntity<CommentDtoResponse> updateComment(CommentUpdateDto commentDto);

    @Operation(
            summary = "Получить все комментарии поста",
            description = "Возвращает список комментариев поста из базы"
    )
    ResponseEntity<List<CommentDtoResponse>> getAllComments(long postId);

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет из базы",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponse(responseCode = "200", description = "Комментарий удален")
    ResponseEntity<Long> deleteComment(long commentId);

    @Operation(
            summary = "Прикрепить картинку к комментарию",
            description = "Добавляет картинку в базу"
    )
    @ApiResponse(responseCode = "200", description = "Картинка загружена")
    ResponseEntity<String> uploadFile(long commentId, MultipartFile file);

    @Operation(
            summary = "Удалить картинку из комментария",
            description = "Удалить картинку из базу",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            in = ParameterIn.HEADER,
                            description = "ID Пользователя",
                            required = true)
            }
    )
    @ApiResponse(responseCode = "200", description = "Картинка удалена")
    ResponseEntity<String> deleteFile(long commentId);

    @Operation(
            summary = "Получить маленькую картинку",
            description = "Выводит маленькую на экран"
    )
    @ApiResponse(responseCode = "200", description = "Картинка загружена")
    ResponseEntity<InputStreamResource> getSmallImage(long commentId);

    @Operation(
            summary = "Получить большую картинку",
            description = "Выводит большую картинку на экран"
    )
    @ApiResponse(responseCode = "200", description = "Картинка загружена")
    ResponseEntity<InputStreamResource> getLargeImage(long commentId);
}