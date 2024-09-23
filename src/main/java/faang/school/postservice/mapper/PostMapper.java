package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostKafkaDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    PostDto toDto(Post post);

    Post toEntity(PostDto postDto);

    @Mapping(target = "subscribers", source = "subscribers")
    PostKafkaDto toPostKafkaDto(PostDto post, List<Long> subscribers);

}
