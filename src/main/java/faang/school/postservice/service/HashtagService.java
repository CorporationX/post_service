package faang.school.postservice.service;

import faang.school.postservice.model.dto.PostDto;
import org.springframework.transaction.annotation.Transactional;

public interface HashtagService {
    void process(PostDto postDto);
}
