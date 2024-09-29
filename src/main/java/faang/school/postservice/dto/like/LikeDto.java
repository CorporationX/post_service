package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonInclude;

public record LikeDto(Long userId,
                      @JsonInclude(JsonInclude.Include.NON_NULL)
                      Long postId,
                      @JsonInclude(JsonInclude.Include.NON_NULL)
                      Long commentId) {

}