package faang.school.postservice.service;


import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeValidator likeValidator;
    private final LikeEventPublisher likeEventPublisher;


    public void likePost(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        if (likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()).isPresent()) {
            unlikePost(likeDto);
        }

        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);

        LikeEvent likeEvent = new LikeEvent();
        likeEvent.setAuthorId(post.getAuthorId());
        likeEvent.setUserId(likeDto.getUserId());
        likeEvent.setPostId(likeDto.getPostId());
        likeEvent.setReceivedAt(LocalDateTime.now());

        likeEventPublisher.sendEvent(likeEvent);

        likeRepository.save(like);
    }

    public void unlikePost(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        Like like = likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Лайк не найден"));
        likeRepository.delete(like);
    }


    public void likeComment(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        if (likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isPresent()) {
            unlikeComment(likeDto);
        }

        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
    }

    public void unlikeComment(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        Like like = likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Лайк не найден"));
        likeRepository.delete(like);
    }
}
