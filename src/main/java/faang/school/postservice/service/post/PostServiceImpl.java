package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.scheduler.SpellCheckResult;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostDto createPost(PostDto postDto) {
        isPostAuthorExist(postDto);

        postDto.setCreatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setDeleted(false);

        postRepository.save(post);
        return postDto;
    }

    @Override
    public PostDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isPublished()) {
            throw new PostException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Override
    public PostDto deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isDeleted()) {
            throw new PostException("post already deleted");
        }

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);

        PostDto postDto = postMapper.toDto(post);
        postDto.setDeletedAt(LocalDateTime.now());

        return postDto;
    }

    @Override
    public PostDto getPost(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<PostDto> getAllNonPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByAuthorId(id));
    }

    @Override
    public List<PostDto> getAllNonPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByProjectId(id));
    }

    @Override
    public List<PostDto> getAllPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByAuthorId(id));
    }

    @Override
    public List<PostDto> getAllPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByProjectId(id));
    }

    @Override
    @Retryable(retryFor = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void postTextCorrection() {
        List<PostDto> unpublishedPost = postMapper.toListDto(postRepository.findUnpublishedPost());

        RestTemplate restTemplate = new RestTemplate();

        for (PostDto postDto : unpublishedPost) {
            String content = postDto.getContent();
            log.info("Words sent for review " + content);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("text", content);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://speller.yandex.net/services/spellservice.json/checkTexts",
                    params,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String jsonResponse = response.getBody();
                log.info("Reply from API " + jsonResponse);

                try {
                    List<List<SpellCheckResult>> results = objectMapper.readValue(jsonResponse,
                            new TypeReference<List<List<SpellCheckResult>>>() {});

                    Map<String, String> correctionsMap = new HashMap<>();

                    for (List<SpellCheckResult> result : results) {
                        for (SpellCheckResult spellCheckResult : result) {
                            if (spellCheckResult.getCode() != 0) {
                                correctionsMap.put(spellCheckResult.getWord(), spellCheckResult.getS().get(0));
                            }
                        }
                    }

                    for(Map.Entry<String, String> entry : correctionsMap.entrySet()) {
                        content = content.replace(entry.getKey(), entry.getValue());
                    }

                    postDto.setContent(content);
                    postRepository.save(postMapper.toEntity(postDto));

                } catch (JsonProcessingException e) {
                    log.error("JSON processing error: " + e);
                    throw new RuntimeException(e);
                }
            } else {
                log.debug("Error when requesting to API: " + response.getStatusCode());
            }
        }
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    private void validateProjectExist(Long id) {
        projectServiceClient.getProject(id);
    }

    private List<PostDto> filterPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> filterNonPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void isPostAuthorExist(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }
}
