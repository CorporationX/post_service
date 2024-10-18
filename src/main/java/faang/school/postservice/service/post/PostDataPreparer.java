package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.AdRepository;
import faang.school.postservice.repository.AlbumRepository;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class PostDataPreparer {
  private final LikeRepository likeRepository;
  private final CommentRepository commentRepository;
  private final AlbumRepository albumRepository;
  private final ResourceRepository resourceRepository;
  private final AdRepository adRepository;
  private final UserServiceClient userServiceClient;

  public Post prepareForCreate(PostDto dto, Post postEntity) {
    postEntity.setLikes(StreamSupport.stream(
                    likeRepository.findAllById(dto.getLikeIds()).spliterator(), false)
            .toList());
    postEntity.setComments(StreamSupport.stream(
                    commentRepository.findAllById(dto.getCommentIds()).spliterator(), false)
            .toList());
    postEntity.setAlbums(StreamSupport.stream(
                    albumRepository.findAllById(dto.getAlbumIds()).spliterator(), false)
            .toList());
    postEntity.setAd(getAd(dto.getAdId()));
    postEntity.setResources(resourceRepository.findAllById(dto.getResourceIds()).stream()
            .toList());
    postEntity.setCreatedAt(LocalDateTime.now());
    postEntity.setUpdatedAt(postEntity.getCreatedAt());

    return postEntity;
  }

  public Post prepareForUpdate(PostDto dto, Post entity) {
    entity.setContent(dto.getContent());
    entity.setAd(getAd(dto.getAdId()));
    entity.setUpdatedAt(LocalDateTime.now());

    return entity;
  }

  public Post prepareForPublish(Post entity) {
    entity.setPublished(true);
    entity.setPublishedAt(LocalDateTime.now());
    return entity;
  }

  private Ad getAd(Long adId) {
    if (adId == null) {
      return null;
    }
    return adRepository.findById(adId).orElseThrow(() -> new EntityNotFoundException("Такого объявления не существует."));
  }

  public PostEvent createPostEvent(PostEvent.EventType eventType, Post post) {
    List<Long> subscriberIds = userServiceClient.getUser(post.getAuthorId()).getFollowers();

    return PostEvent.builder()
            .eventType(eventType)
            .postId(post.getId())
            .authorId(post.getAuthorId())
            .subscriberIds(subscriberIds)
            .build();

  }
}
