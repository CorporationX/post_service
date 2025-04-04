package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.feed.PostCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostCacheMapper {

    @Mapping(source = "comments", target = "comments")
    PostCache toPostCache(PostDto postDto);

    @Mapping(source = "comments", target = "comments")
    PostDto toDto(PostCache postCache);

    default List<CommentDto> mapCopyOnWriteArraySetToList(CopyOnWriteArraySet<CommentDto> comments) {
        return new ArrayList<>(comments);
    }

    default CopyOnWriteArraySet<CommentDto> mapListToCopyOnWriteArraySet(List<CommentDto> comments) {
        return new CopyOnWriteArraySet<>(comments);
    }
}