package faang.school.postservice.dto.post;

import faang.school.postservice.model.VerificationPostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Boolean published;
    private Boolean deleted;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private VerificationPostStatus verificationStatus;
    private LocalDateTime verifiedDate;
    private List<Long> resourceIds;
    private Integer likes;
}
