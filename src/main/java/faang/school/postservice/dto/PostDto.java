package faang.school.postservice.dto;

import lombok.Data;

import java.util.List;


@Data
public class PostDto {
    private long id;
    private List<Long> likes;
}
