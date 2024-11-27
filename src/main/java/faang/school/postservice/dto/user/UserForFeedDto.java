package faang.school.postservice.dto.user;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class UserForFeedDto {
    @Id
    private long id;

    @Length(max = 64)
    private String username;

    private String profilePicSmallFileId;
}
