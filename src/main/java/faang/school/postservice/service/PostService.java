package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

    //TODO: Здесь был Post, теперь PostDto, нужно теперь в других сервисах посмотреть и поменять логику работы
    public PostDto getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(NoSuchElementException::new);
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

}