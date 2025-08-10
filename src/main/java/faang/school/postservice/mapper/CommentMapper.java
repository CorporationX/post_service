package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * CommentMapper — интерфейс для маппинга между DTO и сущностью Comment.
 * <p>
 * Обязанности интерфейса включают преобразование:
 * <ul>
 *     <li>из CommentCreateDto в Comment — для создания новых комментариев;</li>
 *     <li>из CommentViewDto в Comment — для обновления или иных целей;</li>
 *     <li>из Comment в CommentViewDto — для возвращения данных клиенту;</li>
 *     <li>частичное обновление сущности Comment из CommentViewDto;</li>
 * </ul>
 * <p>
 * Использует MapStruct с конфигурацией Spring и игнорированием непомеченных полей.
 * <p>
 * Предполагает, что DTO содержат минимально необходимые поля для операций,
 * а сущность Comment — полную модель данных.
 *
 * @author agent
 * @since 10.08.2025
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment toEntity(CommentViewDto dto);

    CommentViewDto toViewDto(Comment entity);

    Comment toEntity(CommentCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDto(CommentViewDto dto, @MappingTarget Comment entity);
}