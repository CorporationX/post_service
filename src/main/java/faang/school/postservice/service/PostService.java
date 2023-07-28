package faang.school.postservice.service;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;

    public ResponsePostDto createDraft(CreatePostDto dto) {
        validateCreate(dto);
        return null;
    }

    private void validateCreate(CreatePostDto dto) {

    }
}
