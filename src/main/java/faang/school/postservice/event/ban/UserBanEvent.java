package faang.school.postservice.event.ban;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBanEvent {

    private final Long userId;
}
