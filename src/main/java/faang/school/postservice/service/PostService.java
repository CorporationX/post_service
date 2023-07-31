package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }
}
