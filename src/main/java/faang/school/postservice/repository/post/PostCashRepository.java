package faang.school.postservice.repository.post;

import faang.school.postservice.dto.post.PostCashDto;
import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface PostCashRepository extends CrudRepository<PostCashDto, Long> {
}
