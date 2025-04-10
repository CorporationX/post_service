package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostViewEvent {
    @JsonProperty("idPost")
    private Long idPost;
    @JsonProperty("idUser")
    private Long idUser;
    @JsonProperty("idAuthor")
    private Long idAuthor;
    @JsonProperty("date")
    private LocalDateTime date;
}
