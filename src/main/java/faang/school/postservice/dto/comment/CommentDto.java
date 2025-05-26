package faang.school.postservice.dto.comment;

import faang.school.postservice.model.CommentDtoStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public abstract class CommentDto {
    private CommentDtoStatus status;
}
