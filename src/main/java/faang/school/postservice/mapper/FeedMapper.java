package faang.school.postservice.mapper;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.model.Feed;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedMapper {

    @Mapping(source = "post.id", target = "postId")
    FeedPostDto feedToFeedDto(Feed feed);

    List<FeedPostDto> feedToFeedDto(List<Feed> feed);

}
