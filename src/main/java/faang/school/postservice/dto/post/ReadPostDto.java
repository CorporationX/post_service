package faang.school.postservice.dto.post;

import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.Data;

@Data
public class ReadPostDto {

    private long id;
    private String content;
    private UserDto author;
    private ProjectDto project;
}
