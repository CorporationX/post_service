package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.FeedPublicationDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.redis.cache.entity.PostCache;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = {CommentFeedMapper.class, PostFeedMapper.class})
public interface FeedPublicationMapper {

    FeedPublicationDto toDto(PostFeedDto post, List<CommentFeedDto> comments);

    @Mapping(source = "comments", target = "comments")
    @Mapping(source = "postCache", target = "post")
    FeedPublicationDto toDto(PostCache postCache);
}
