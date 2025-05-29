package faang.school.postservice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ErrorResponse {
    @JsonProperty("errorDate")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime errorDate;

    @JsonProperty("errorMessage")
    private String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorDate = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
}
