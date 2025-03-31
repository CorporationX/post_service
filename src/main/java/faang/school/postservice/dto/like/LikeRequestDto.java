package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LikeRequestDto(

        @JsonProperty("userId")
        long userId
) {
}
