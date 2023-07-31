package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);
    @Mapping(source = "hashtags", target = "hashtags", qualifiedByName = "hashtagToString")
    PostDto toDto(Post entity);

    List<PostDto> toListDto(List<Post> posts);

    @Named("hashtagToString")
    default List<String> hashtagToString(List<Hashtag> list){
        return list.stream()
                .map(Hashtag::getHashtag)
                .toList();
    }
}
