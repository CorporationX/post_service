package faang.school.postservice.dto.post;

import lombok.Data;

import java.util.List;

@Data
public class PostKafkaDto {

    private long id;
    private Long authorId;
    private String content;
    private List<Long> subscribers;
}
