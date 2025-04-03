package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostShortContentDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PostShortContentMapper {
    @Mapping(target = "shortContent", source = "content", qualifiedByName = "mapContentToShortContent")
    PostShortContentDto toDto(Post post);

    @Named("mapContentToShortContent")
    default String mapContentToShortContent(String content) {
        if(content.length()>50) {
           return content.substring(0,49);
        } else {return content;}

    }
}
