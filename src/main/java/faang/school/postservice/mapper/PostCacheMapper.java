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
import java.util.NavigableSet;
import java.util.TreeSet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCacheMapper {

    @Mapping(target = "comments", qualifiedByName = "mapListToNavigableSet")
    PostCache toPostCache(PostDto postDto);

    @Mapping(target = "comments", qualifiedByName = "mapNavigableSetToList")
    PostDto toDto(PostCache postCache);

    @Named("mapNavigableSetToList")
    default List<CommentDto> mapNavigableSetToList(NavigableSet<CommentDto> comments) {
        if(comments == null) return null;
        return new ArrayList<>(comments);
    }

    @Named("mapListToNavigableSet")
    default NavigableSet<CommentDto> mapListToNavigableSet(List<CommentDto> comments) {
        if(comments == null) return null;
        return new TreeSet<>(comments);
    }
}
