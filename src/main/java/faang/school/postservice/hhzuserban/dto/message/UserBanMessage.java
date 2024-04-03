package faang.school.postservice.hhzuserban.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserBanMessage implements Serializable {
    private long userId;
}
