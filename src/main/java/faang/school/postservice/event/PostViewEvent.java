package faang.school.postservice.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostViewEvent implements Serializable {

    private Long postId;
    private Long authorId;
    private Long userId;
    @JsonFormat(pattern = "dd-MM-yyyy'T'HH:mm")
    private LocalDateTime viewedAt;


}
