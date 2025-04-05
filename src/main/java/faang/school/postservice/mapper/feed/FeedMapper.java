package faang.school.postservice.mapper.feed;

import faang.school.postservice.dto.feed.FeedItemDto;
import faang.school.postservice.dto.feed.FeedItemResponseDto;
import faang.school.postservice.dto.post.PostResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {
    @Mapping(target = "postResponseDto", source = "postResponseDto")
    FeedItemResponseDto toFeedResponseDto(FeedItemDto feedItemDto, PostResponseDto postResponseDto);
}
