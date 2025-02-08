package faang.school.postservice.service.impl;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostRepositoryAdapter postRepositoryAdapter;
    private final PostMapper postMapper;

    private static final String ERR_CHANGE_AUTHOR_MSG = "Нельзя изменить автора!";

    @Transactional
    @Override
    public PostDto create(PostSaveDto postSaveDto) {
        Post post = postMapper.toEntity(postSaveDto);
        post.setScheduledAt(LocalDateTime.now().plusDays(3));
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional(readOnly = true)
    @Override
    public PostDto getPost(long id) {
        Post post = postRepositoryAdapter.findById(id);
        return postMapper.toDto(post);
    }

    @Transactional
    @Override
    public PostDto update(long id, PostSaveDto postSaveDto) {
        Post post = postRepositoryAdapter.findById(id);
        if (post.getAuthorId() != null) {
            if (!Objects.equals(postSaveDto.getAuthorId(), post.getAuthorId())) {
                throw new DataValidationException(ERR_CHANGE_AUTHOR_MSG);
            }
        } else {
            if (!Objects.equals(postSaveDto.getProjectId(), post.getProjectId())) {
                throw new DataValidationException(ERR_CHANGE_AUTHOR_MSG);
            }
        }
        postMapper.update(post, postSaveDto);
        return postMapper.toDto(postRepository.save(post));
    }

    @Transactional
    @Override
    public void publish(long id) {
        Post post = postRepositoryAdapter.findById(id);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void delete(long id) {
        Post post = postRepositoryAdapter.findById(id);
        post.setDeleted(true);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostDto> getPostsByAuthorId(long id, boolean published) {
        List<Post> posts = postRepository.findByAuthorId(id, published);
        return postMapper.toDto(posts);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PostDto> getPostsByProjectId(long id, boolean published) {
        List<Post> posts = postRepository.findByProjectId(id, published);
        return postMapper.toDto(posts);
    }

    @Transactional
    @Override
    public int publishingPostsOnSchedule() {
        return postRepository.publishingPostsOnSchedule();
    }
}
