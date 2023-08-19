package faang.school.postservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "likes", expression = "java(new ArrayList<>())")
    @Mapping(target = "comments", expression = "java(new ArrayList<>())")
    @Mapping(target = "albums", expression = "java(new ArrayList<>())")
    @Mapping(target = "published", expression = "java(false)")
    @Mapping(target = "deleted", expression = "java(false)")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS))")
    Post toEntity(PostDto dto);

    @Mapping(target = "adId", source = "ad.id")
    PostDto toDto(Post entity);

    default List<Long> map(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Long> ids = new ArrayList<>();
        try {
            ids = objectMapper.readValue(value, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ids;
    }
    List<PostDto> toDtos(List<Post> entities);
}
