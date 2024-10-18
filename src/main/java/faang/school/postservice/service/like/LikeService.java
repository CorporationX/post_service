package faang.school.postservice.service.like;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.producer.EventProducer;
import faang.school.postservice.service.publisher.PublicationService;
import faang.school.postservice.service.publisher.messagePublishers.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
  private final static Locale LOCALE = Locale.getDefault();
  private final MessageSource messageSource;
  private final LikeRepository likeRepository;
  private final LikeMapper mapper;
  private final PublicationService<LikeEventPublisher, LikeEvent> publishService;
  private final EventProducer<LikeEvent> likeProducer;
  private final LikeValidator likeValidator;

  public LikeDto addPostLike(Long postId, LikeDto dto) {
    if (likeValidator.postLikeValidate(postId, dto.getUserId())) {
      throw new RuntimeException(messageSource.getMessage("exception.post_already_liked", null, LOCALE));
    }

    Like like = mapper.toEntity(dto);
    Like savedLike = likeRepository.save(like);
    log.info("Like:{} is added.", savedLike);

    LikeEvent likeEvent = mapper.toEvent(savedLike);
    likeEvent.setEventType(LikeEvent.EventType.CREATE);
    likeProducer.sendEvent(likeEvent);
    return mapper.toDto(savedLike);
  }

  public void deletePostLike(Long postId, LikeDto dto) {
    long userId = dto.getUserId();

    if (!likeValidator.postLikeValidate(postId, userId)) {
      throw new RuntimeException(messageSource.getMessage("exception.like_not_present", null, LOCALE));
    }

    long likeId = dto.getId();
    likeRepository.deleteById(likeId);
    log.info("Like with id:{} is deleted.", likeId);

    LikeEvent likeEvent = mapper.toEvent(dto);
    likeEvent.setEventType(LikeEvent.EventType.DELETE);
    likeProducer.sendEvent(likeEvent);
  }

  public LikeDto addCommentLike(Long postId, Long commentId, LikeDto dto) {

    if (likeValidator.commentLikeValidate(commentId, dto.getUserId())) {
      throw new RuntimeException(messageSource.getMessage("exception.comment_already_liked", null, LOCALE));
    }

    Like like = mapper.toEntity(dto);
    LikeEvent likeEvent = mapper.toEvent(like);
    likeEvent.setEventType(LikeEvent.EventType.CREATE);
    likeProducer.sendEvent(likeEvent);
    return mapper.toDto(likeRepository.save(like));
  }

  public void deleteCommentLike(Long postId, Long commentId, LikeDto dto) {

    if (!likeValidator.commentLikeValidate(commentId, dto.getUserId())) {
      throw new RuntimeException(messageSource.getMessage("exception.like_not_present", null, LOCALE));
    }

    long likeId = dto.getId();
    likeRepository.deleteById(likeId);
    LikeEvent likeEvent = mapper.toEvent(dto);
    likeEvent.setEventType(LikeEvent.EventType.DELETE);
    likeProducer.sendEvent(likeEvent);
  }

}
