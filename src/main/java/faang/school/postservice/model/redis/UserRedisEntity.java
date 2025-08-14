package faang.school.postservice.model.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRedisEntity implements Serializable {
    private String userId;
    private String userName;
    private String email;
}
