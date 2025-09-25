package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostViewEvent {

    private Long postId;

    private Long viewerId;

    private Long authorId;

    private Long projectId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime viewedAt;

    private String ipAddress;

    private String userAgent;

    private String source;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventTimestamp;

    private String eventVersion = "1.0";

    private String sessionId;

    private Long viewDurationMs;
}