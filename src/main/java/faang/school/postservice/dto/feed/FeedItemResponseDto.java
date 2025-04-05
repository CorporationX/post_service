package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.Builder;

@Builder
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public record FeedItemResponseDto(

        @JsonSerialize(as = PostResponseDto.class)
        @JsonDeserialize(as = PostResponseDto.class)
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        PostResponseDto postResponseDto
) {
}
