package faang.school.postservice.dto.like;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TargetType {
    @Schema(description = "Like targets a post")
    POST,

    @Schema(description = "Like targets a comment")
    COMMENT
}