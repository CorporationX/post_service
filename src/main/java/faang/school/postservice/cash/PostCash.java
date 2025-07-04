package faang.school.postservice.cash;

import faang.school.postservice.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
//@RedisHash("PostCash")
public class PostCash {
    @Id
    Long id;
    PostDto postDto;
}
