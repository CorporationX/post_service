package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.feed.PostFeedItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostFeedMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "likesCount", source = "post.likesCount")
    @Mapping(target = "publishedAt", source = "post.publishedAt")
    @Mapping(target = "createdAt", source = "post.createdAt")
    PostFeedItemDto toFeedItemDto(PostV2Dto post, UserDto author);
}
