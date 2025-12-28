package faang.school.postservice.mapper.feed;

import faang.school.postservice.cache.model.post.PostCache;
import faang.school.postservice.dto.author.AuthorDto;
import faang.school.postservice.dto.feed.FeedPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeedMapper {

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "publishedAt", source = "post.publishedAt")
    @Mapping(target = "likesCount", source = "post.likesCount")
    @Mapping(target = "commentsCount", source = "post.commentsCount")
    @Mapping(target = "author", source = "authorDto")
    FeedPostDto toFeedPostDto(PostCache post, AuthorDto authorDto);
}