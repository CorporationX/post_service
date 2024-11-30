package faang.school.postservice.redis.cache;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.model.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    PostCache toPostCache(PostDto postDto);

    PostDto toDto(PostCache postCache);

//    // Преобразование CopyOnWriteArraySet<CommentDto> в List<CommentDto>
//    // Используем ArrayList для преобразования в List
//    default List<CommentDto> mapCopyOnWriteArrayListToList(CopyOnWriteArraySet<CommentDto> comments) {
//        return new ArrayList<>(comments); // Создаём новый ArrayList из элементов CopyOnWriteArraySet
//    }
//
//    // Преобразование List<CommentDto> в CopyOnWriteArraySet<CommentDto>
//    // Преобразуем List в CopyOnWriteArraySet
//    default CopyOnWriteArraySet<CommentDto> mapListToCopyOnWriteArraySet(List<CommentDto> comments) {
//        return new CopyOnWriteArraySet<>(comments); // Используем CopyOnWriteArraySet для гарантированной потокобезопасности
//    }
}
