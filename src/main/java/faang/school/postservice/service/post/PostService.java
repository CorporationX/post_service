package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.project.ProjectService;
import faang.school.postservice.service.user.UserService;
import faang.school.postservice.service.utils.PostServiceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostServiceUtils postServiceUtils;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final ProjectService projectService;

    @Transactional
    public PostDto create(CreatePostDto createPostDto) {
        postServiceUtils.isAuthorOrProjectAdded(createPostDto);
        return postMapper.toPostDto(
                postRepository.save(
                        postMapper.toEntity(createPostDto)));
    }

    @Transactional
    public PostDto publishPost(long postId) {
        Post post = postServiceUtils.isPostExists(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toPostDto(postRepository.save(post));
    }

    @Transactional
    public PostDto update(long postId, String content) {
        Post post = postServiceUtils.isPostExists(postId);
        post.setContent(content);
        return postMapper.toPostDto(postRepository.save(post));
    }

    @Transactional
    public void delete(long postId) {
        Post post = postServiceUtils.isPostExists(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostDto getById(long postId) {
        postServiceUtils.isPostExists(postId);
        return postMapper.toPostDto(postServiceUtils.isPostExists(postId));
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNonDeletedScratchesByAuthorId(long authorId) {
        userService.checkUserExist(authorId);
        return postMapper.toListPostDto(postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNonDeletedScratchesByProjectId(long projectId) {
        projectService.checkProjectExist(projectId);
        return postMapper.toListPostDto(postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNonDeletedPublishedByAuthorId(Long authorId) {
        userService.checkUserExist(authorId);
        return postMapper.toListPostDto(postRepository.findByAuthorId(authorId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList());
    }

    @Transactional(readOnly = true)
    public List<PostDto> getNonDeletedPublishedByProjectId(Long projectId) {
        projectService.checkProjectExist(projectId);
        return postMapper.toListPostDto(postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList());
    }
}
