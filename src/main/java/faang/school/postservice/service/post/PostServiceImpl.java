package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.events.KafkaPostEvent;
import faang.school.postservice.dto.kafka.events.KafkaPostViewEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.properties.topics.KafkaTopics;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final KafkaProducer kafkaProducer;
    private final KafkaTopics kafkaTopics;

    @Override
    public PostDto createPost(PostDto postDto) {
        isPostAuthorExist(postDto);

        postDto.setCreatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setDeleted(false);
        post.setComments(List.of(new Comment()));

        postRepository.save(post);
        return postDto;
    }

    @Transactional
    @Override
    public PostDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isPublished()) {
            throw new PostException("Post is already published");
        }

//        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);

        UserDto userDto = userServiceClient.getUser(post.getAuthorId());

        KafkaPostEvent event = KafkaPostEvent.builder()
                .author(userDto)
                .postId(post.getId())
                .countComments((long) post.getComments().size())
                .countLikes((long) post.getLikes().size())
                .createdAt(LocalDateTime.now())
                .build();
        kafkaProducer.send(kafkaTopics.getPublish_post().getName(), event);
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
        kafkaProducer.send(kafkaTopics.getPost_view().getName(), KafkaPostViewEvent.builder().postId(id).build());
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
