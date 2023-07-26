package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostServiceValidator validator;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional
    public PostDto addPost(PostDto dto) {
        validator.validateToAdd(dto);
        Post post = postMapper.toEntity(dto);
        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto publishPost(Long id) {
        Post post = validator.validateToPublish(id);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long id, String content) {
        Post post = validator.validateToUpdate(id, content);
        post.setContent(content);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return postMapper.toDto(post);
    }
}
