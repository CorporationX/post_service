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
    @Mapping(target = "albums", ignore = true) // Внутри поста есть альбом, внутри альбома JSON с id юзеров,
        // которые могут видеть альбом. В ДТО id юзеров лежат как List<Long>, он сам не может это смапить при такое,
        // я тоже не понимаю пока, как это сделать. Пока игнор поля, чтобы можно было стартануть приложение TODO
    PostDto toDto(Post entity);

    List<PostDto> toDtos(List<Post> entities);
}
