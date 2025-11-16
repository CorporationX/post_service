package faang.school.postservice.mapper;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LikeMapper {

    static LikeDto toDtoWithPost(Like like) {
        return LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .postId(like.getPost().getId())
                .commentId(null)
                .build();
    }

    static LikeDto toDtoWithComment(Like like) {
        return LikeDto.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .commentId(like.getComment().getId())
                .postId(null)
                .build();
    }

}
