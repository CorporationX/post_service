package faang.school.postservice.model.event;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeEvent extends Event {
    @Id
    private Long id;
    private Long userId;

}
