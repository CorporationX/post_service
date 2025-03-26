package faang.school.postservice.mapper;

import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface HashtagsPostMapper {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    PostResponseDto toPostResponseDto(Post post);

    default String formatDate(LocalDateTime publishedAt) {
        if (publishedAt != null) {
            return publishedAt.format(dateFormatter);
        }
        return null;
    }
}

