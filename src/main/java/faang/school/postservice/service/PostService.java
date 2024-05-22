package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostValidator postValidator;
    private final PostMapper postMapper;
    private final PostRepository postRepository;

    @Transactional
    public PostDto createPost(PostDto postDto) {

        return
    }

    @Transactional
    public PostDto publishPost(long id) {
        return
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        return
    }

    @Transactional
    public PostDto deletePost(long id) {
        return
    }

    public PostDto getPostById(long postId) {
        return
    }

    public List<PostDto> getDraftsByAuthorId(long id) {
        return
    }

    public List<PostDto> getDraftsByProjectId(long id) {
        return
    }

    public List<PostDto> getPostsByAuthorId(long id) {
        return
    }

    public List<PostDto> getPostsByProjectId(long id) {
        return
    }

    protected Post existsPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with ID " + postId + " not found"));
    }
}