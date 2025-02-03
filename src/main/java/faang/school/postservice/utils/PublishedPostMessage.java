package faang.school.postservice.utils;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublishedPostMessage {
    private Post post;
    private UserDto userDto;
}