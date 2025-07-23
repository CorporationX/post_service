package faang.school.postservice.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostViewEvent {
    @NotNull
    private long postId;
    @NotNull
    private long userId;
    @NotNull
    private LocalDateTime viewedAt;
}