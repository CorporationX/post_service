package faang.school.postservice.mapper;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    //TODO: пока просто создал, по ходу дела посмотрим что нужно будет маперить
    @Mapping(source = "post.id", target = "postId")
    PostDto toDto(Post post);

}
