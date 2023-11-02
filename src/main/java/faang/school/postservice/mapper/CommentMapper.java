package faang.school.postservice.mapper;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "likes", source = "likes", qualifiedByName = "mapLikesToDto")
    CommentDto toDto(Comment comment);

    Comment toEntity(CommentDto dto);

    @Named("mapLikesToDto")
    default List<LikeDto> mapLikesToDto(List<Like> likes) {
        LikeMapper likeMapper = new LikeMapperImpl();
        if (likes == null){
            return null;
        }
        return likes.stream()
                .map(likeMapper::toDto)
                .toList();
    }
}
