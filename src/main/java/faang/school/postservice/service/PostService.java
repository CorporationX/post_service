package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostDto create(PostDto postDto) {
        return null;
    }

    public PostDto publish(PostDto postDto) {
        return null;
    }

    public PostDto update(PostDto postDto) {
        return null;
    }

    public PostDto deleteById(Long postId) {
        return null;
    }

    public Post findById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post with this Id does not exist"));
    }

    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long userId) {
        return null;
    }

    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        return null;
    }

    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(Long userId) {
        return null;
    }

    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(Long projectId) {
        return null;
    }
}
