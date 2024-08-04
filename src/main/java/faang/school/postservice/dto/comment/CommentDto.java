package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.transfer.Exists;
import faang.school.postservice.dto.transfer.New;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentDto {

    @Null(groups = {New.class})
    @NotNull(groups = {Exists.class})
    private Long id;

    @NotNull(groups = {New.class, Exists.class}, message = "Content cannot be empty")
    @Size(min = 1, max = 4096, groups = {New.class, Exists.class})
    private String content;

    @NotNull(groups = {New.class}, message = "You need write authorId here")
    @Null(groups = {Exists.class}, message = "Field must be null, when you chose update")
    private Long authorId;

    @NotNull(groups = {New.class}, message = "You need write postId here")
    @Null(groups = {Exists.class}, message = "Field must be null, when you chose update")
    private Long postId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
