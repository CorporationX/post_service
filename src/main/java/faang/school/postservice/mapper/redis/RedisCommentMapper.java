package faang.school.postservice.mapper.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RedisCommentMapper {

    @Mapping(target = "amountOfLikes", source = "likes", qualifiedByName = "countAmountOfLikes")
    RedisCommentDto toDto(Comment comment);


    @Named("countAmountOfLikes")
    default long countAmountOfLikes(List<Like> likes) {
        return likes == null ? 0 : likes.size();
    }

    @Named("mapCommentsToRedisCommentDto")
    default List<RedisCommentDto> mapCommentsToRedisCommentDto(List<Comment> comments) {
        return comments == null ? new ArrayList<>(3) : comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(this::toDto)
                .limit(3)
                .collect(Collectors.toList());
    }
}
