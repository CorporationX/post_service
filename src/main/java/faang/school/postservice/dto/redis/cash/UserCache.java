package faang.school.postservice.dto.redis.cash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {

    private String id;
    private String username;
    private List<Long> followedUserIds;
    private List<Long> followersUserIds;
    @Version
    private long version;
}
