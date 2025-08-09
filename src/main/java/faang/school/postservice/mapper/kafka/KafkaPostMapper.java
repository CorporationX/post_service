package faang.school.postservice.mapper.kafka;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.postservice.dto.kafka.KafkaPostMessage;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface KafkaPostMapper {
    @Mapping(target = "totalLikes", source = "likes", qualifiedByName = "likeListToCount")
    @Mapping(target = "totalComments", source = "comments", qualifiedByName = "commentListToCount")
    KafkaPostMessage toKafkaPostMessage(Post post);

    List<KafkaPostMessage> toKafkaPostMessages(List<Post> posts);

    @Named("likeListToCount")
    default Long likeListToCount(List<Like> likes) {
        return likes == null ? 0L : (long) likes.size();
    }

    @Named("commentListToCount")
    default Long commentListToCount(List<Comment> comments) {
        return comments == null ? 0L : (long) comments.size();
    }
}
