package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления постами
 * Предоставляет endpoints для создания, обновления, получения списка
 * и получения поста по индентификатору.
 *
 * @author Linempy
 * @since 26.07.2025
 */
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    /**
     * Создает новый пост.
     *
     * @param createDto DTO с данными для создания поста {@link PostCreateDto}
     * @return DTO созданного поста {@link PostViewDto}
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<PostViewDto> create(@RequestBody PostCreateDto createDto) {
        PostViewDto result = service.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Публикует существующий пост.
     *
     * @param postId ID поста для публикации
     * @return HTTP 204 No Content при успешной публикации
     */
    @PutMapping("/{postId}/publications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> publication(@PathVariable Long postId) {
        service.publication(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет существующий пост.
     *
     * @param postId ID поста для обновления
     * @param dto    DTO с обновленными данными поста {@link PostUpdateDto}
     * @return DTO обновленного поста {@link PostViewDto}
     */
    @PutMapping("/{postId}")
    public ResponseEntity<PostViewDto> update(@PathVariable Long postId, @RequestBody PostUpdateDto dto) {
        PostViewDto result = service.update(postId, dto);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Помечает пост как удаленный (soft delete).
     *
     * @param postId ID поста для удаления
     */
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> softDelete(@PathVariable Long postId) {
        service.softDelete(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает пост по его ID.
     *
     * @param postId ID поста
     * @return DTO запрошенного поста {@link PostViewDto}
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostViewDto> getById(@PathVariable Long postId) {
        PostViewDto result = service.getById(postId);
        return ResponseEntity.ok().body(result);
    }

    /**
     * Получает отфильтрованные посты в соответствии с параметрами filterDto ({@link PostFilterDto}).
     * В цепочке фильтрации участвуют только не null параметры filterDto.
     *
     * @param filterDto Dto с параметрами фильтрации постов
     * @return список DTO черновиков {@link PostViewDto}
     */
    @PostMapping("/filter")
    public ResponseEntity<List<PostViewDto>> findByFilter(@RequestBody PostFilterDto filterDto) {
        List<PostViewDto> result = service.findByFilter(filterDto);
        return ResponseEntity.ok().body(result);
    }
}