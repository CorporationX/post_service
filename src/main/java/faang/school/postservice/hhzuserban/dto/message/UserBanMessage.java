package faang.school.postservice.hhzuserban.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class UserBanMessage implements Serializable {
    private long userId;
}
