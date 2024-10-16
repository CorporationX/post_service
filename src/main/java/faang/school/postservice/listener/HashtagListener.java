package faang.school.postservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.service.HashtagService;
import org.springframework.stereotype.Service;

@Service
public class HashtagListener extends AbstractListener<PostDto> {

    private final HashtagService hashtagService;

    public HashtagListener(ObjectMapper objectMapper, HashtagService hashtagService) {
        super(objectMapper);
        this.hashtagService = hashtagService;
    }

    @Override
    protected Class<PostDto> getType() {
        return PostDto.class;
    }

    @Override
    protected void process(PostDto postDto) {
        hashtagService.process(postDto);
    }
}
