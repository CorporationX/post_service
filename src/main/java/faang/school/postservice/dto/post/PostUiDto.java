package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PostUiDto {
        Long id;
        String content;
        UserDto author;
        Long projectId;
        Long likesNumber;
}
