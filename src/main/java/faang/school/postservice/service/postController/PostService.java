package faang.school.postservice.service.postController;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.PostException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final AlbumRepository albumRepository;
    private final AdRepository adRepository;
    private final ResourceRepository resourceRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final List<PostFilter> postFilters;

    public PostDto create(PostDto postDto) {
        validator.validateAuthor(postDto);
        Post postEntity = mapper.toEntity(postDto);

        validateAuthorExists(postEntity);
        fillEntityWithData(postDto, postEntity);

        return mapper.toDto(postRepository.save(postEntity));
    }

    public PostDto publish(Long postId) {
        Post postEntity = getPostEntity(postId);
        validator.validatePublished(postEntity);
        postEntity.setPublished(true);
        postEntity.setPublishedAt(LocalDateTime.now());
        return mapper.toDto(postRepository.save(postEntity));
    }

    public PostDto update(PostDto postDto) {
        Post entity = getPostEntity(postDto.getId());
        validator.checkImmutableData(postDto, entity);
        updateEntity(postDto, entity);
        return mapper.toDto(postRepository.save(entity));
    }

    public PostDto delete(Long postId) {
        Post entity = getPostEntity(postId);
        entity.setDeleted(true);
        return mapper.toDto(postRepository.save(entity));
    }

    public PostDto getPost(Long postId) {
        return mapper.toDto(getPostEntity(postId));
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();

        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
    }

    private void updateEntity(PostDto dto, Post entity) {
        entity.setContent(dto.getContent());
        entity.setAd(getAd(dto.getAdId()));
        entity.setUpdatedAt(LocalDateTime.now());
    }

    private void validateAuthorExists(Post postEntity) {
        Long authorId = postEntity.getAuthorId();
        Long projectId = postEntity.getProjectId();

        if (authorId != null) {
            userServiceClient.getUser(authorId);
            return;
        }
        projectServiceClient.getProject(projectId);
    }

    private void fillEntityWithData(PostDto postDto, Post postEntity) {
        postEntity.setLikes(StreamSupport.stream(
                        likeRepository.findAllById(postDto.getLikeIds()).spliterator(), false)
                .toList());
        postEntity.setComments(StreamSupport.stream(
                        commentRepository.findAllById(postDto.getCommentIds()).spliterator(), false)
                .toList());
        postEntity.setAlbums(StreamSupport.stream(
                        albumRepository.findAllById(postDto.getAlbumIds()).spliterator(), false)
                .toList());
        postEntity.setAd(getAd(postDto.getAdId()));
        postEntity.setResources(resourceRepository.findAllById(postDto.getResourceIds()).stream()
                .toList());
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setUpdatedAt(postEntity.getCreatedAt());
    }

    private Ad getAd(Long adId) {
        return adRepository.findById(adId).orElseThrow(() -> new PostException("Такого объявления не существует."));
    }

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new PostException("Такого сообщения не существует."));
    }
}
