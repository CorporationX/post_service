package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRedis implements Serializable {
    @Id
    private Long id;
    private String username;
}
