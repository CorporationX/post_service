package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostUiDto;
import faang.school.postservice.dto.user.UserCashDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostUiDtoMapper {
    @Mapping(target = "likesNumber", expression = "java(post.getLikes() != null ? (long)post.getLikes().size() : 0)")
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "content", source = "post.content")
    @Mapping(target = "author", source = "userCashDto")
    PostUiDto toDto(Post post, UserCashDto userCashDto);
}
