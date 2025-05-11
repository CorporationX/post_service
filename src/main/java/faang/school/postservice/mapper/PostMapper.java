package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostMapper {

    @Mapping(target = "likes", expression = "java(post.getLikes() != null ? (long)post.getLikes().size() : 0)")
    @Mapping(target = "scheduleAt", ignore = true)
    PostDto toDto(Post post);
}
