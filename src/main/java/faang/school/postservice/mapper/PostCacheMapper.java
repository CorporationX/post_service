package faang.school.postservice.mapper;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.cache.entity.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    @Mapping(target = "comments", qualifiedByName = "mapListToCopyOnWriteArrayList")
    PostCache toPostCache(PostDto postDto);

    @Mapping(target = "comments", qualifiedByName = "mapCopyOnWriteArrayListToList")
    PostDto toDto(PostCache postCache);

    @Named("mapCopyOnWriteArrayListToList")
    default List<CommentDto> mapCopyOnWriteArrayListToList(CopyOnWriteArraySet<CommentDto> comments) {
        if(comments == null) return null;
        return new ArrayList<>(comments);
    }

    @Named("mapListToCopyOnWriteArrayList")
    default CopyOnWriteArraySet<CommentDto> mapListToCopyOnWriteArrayList(List<CommentDto> comments) {
        if(comments == null) return null;
        return new CopyOnWriteArraySet<>(comments);
    }
}
