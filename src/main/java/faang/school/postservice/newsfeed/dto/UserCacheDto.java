package faang.school.postservice.newsfeed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCacheDto {
    private Long userId;
    private String username;
    private Integer experience;
    private String pictureFileId;
}
