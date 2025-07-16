package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    @Override
    public PostDto findById(long id) {
        return null;
    }

    @Override
    public void save(PostDto postDto) {

    }

    @Override
    public void markPostAsDeleted(long id) {

    }

    @Override
    public void createPostDraft(PostDto postDto) {

    }
}
