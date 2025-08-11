package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * CommentMapper — интерфейс для маппинга между DTO и сущностью Comment.
 * <p>
 * Отвечает за преобразование между различными представлениями комментариев:
 * <ul>
 *     <li>Преобразование из {@link CommentCreateDto} в {@link Comment} — для создания новых комментариев;</li>
 *     <li>Преобразование из {@link CommentViewDto} в {@link Comment} — для обновления или иных операций;</li>
 *     <li>Преобразование из {@link Comment} в {@link CommentViewDto} — для передачи данных клиенту;</li>
 *     <li>Частичное обновление существующей сущности {@link Comment} из {@link CommentViewDto};</li>
 *     <li>Создание сущности {@link Comment} из {@link CommentCreateDto} с дополнительными параметрами авторства и поста.</li>
 * </ul>
 * <p>
 * Использует MapStruct с конфигурацией Spring и игнорирует поля, не указанные явно в маппинге.
 * <p>
 * Предполагается, что DTO содержат минимально необходимые данные для своих операций,
 * а сущность {@link Comment} — полную модель комментария с ссылками на автора и пост.
 *
 * @author agent
 * @since 10.08.2025
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    /**
     * Преобразует {@link CommentViewDto} в сущность {@link Comment}.
     *
     * @param dto DTO представление комментария для обновления или других целей
     * @return сущность {@link Comment}
     */
    Comment toEntity(CommentViewDto dto);

    /**
     * Преобразует сущность {@link Comment} в DTO {@link CommentViewDto}.
     *
     * @param entity сущность комментария
     * @return DTO представление комментария
     */
    CommentViewDto toViewDto(Comment entity);

    /**
     * Преобразует DTO создания комментария {@link CommentCreateDto} в сущность {@link Comment}.
     * Не устанавливает поля авторства и поста.
     *
     * @param dto DTO создания комментария
     * @return новая сущность {@link Comment}
     */
    Comment toEntityFromCreateDto(CommentCreateDto dto);

    /**
     * Частично обновляет сущность {@link Comment} на основе данных из {@link CommentViewDto}.
     * Игнорирует null значения в DTO.
     *
     * @param dto    DTO с новыми данными
     * @param entity сущность, которую надо обновить
     */
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDto(CommentViewDto dto, @MappingTarget Comment entity);

    /**
     * Преобразует DTO создания комментария {@link CommentCreateDto} в сущность {@link Comment},
     * одновременно устанавливая поля автора (authorId) и поста (post).
     *
     * @param dto      DTO создания комментария
     * @param authorId идентификатор автора комментария
     * @param post     сущность поста, к которому относится комментарий
     * @return новая сущность {@link Comment} с установленными authorId и post
     */
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "post", source = "post")
    Comment toEntityWithAuthorAndPost(CommentCreateDto dto, Long authorId, Post post);
}