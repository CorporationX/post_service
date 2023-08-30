package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeEvent implements Serializable {
    private Long idPost;
    private Long idAuthor;
    private Long idUser;
    private LocalDateTime dateTime;
    @Override
    public String toString() {
        return "LikeEvent{" +
                "idPost=" + idPost +
                ", idAuthor=" + idAuthor +
                ", idUser=" + idUser +
                ", dateTime=" + dateTime +
                '}';
    }
}