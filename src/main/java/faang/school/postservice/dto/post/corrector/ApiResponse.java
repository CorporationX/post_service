package faang.school.postservice.dto.post.corrector;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponse<T extends Response>(
        boolean status,

        T response,

        @JsonProperty("error_code")
        int errorCode,

        String description) {
}
