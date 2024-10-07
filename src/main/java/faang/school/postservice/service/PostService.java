package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostMapper postMapper;

  public List<PostResponseDto> getPostsByAuthorWithLikes(long authorId) {
    List<Post> posts = postRepository.findByAuthorIdWithLikes(authorId);
    return posts.stream()
        .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
        .toList();
  }

  public List<PostResponseDto> getPostsByProjectWithLikes(long projectId) {
    List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
    return posts.stream()
        .map(post -> postMapper.toResponseDto(post, post.getLikes().size()))
        .toList();
  }
}
