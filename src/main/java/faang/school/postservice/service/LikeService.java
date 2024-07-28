package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.KafkaLikePublisher;
import faang.school.postservice.publisher.redis.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;
    private final KafkaLikePublisher kafkaLikePublisher;

    @Transactional
    public LikeDto addLikePost(Long postId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Post post = postService.findPostById(postId);
        Like like = likeMapper.toEntity(likeDto);

        likeValidator.checkIsStandLikeWithIdOnPost(post, like);
        likeValidator.checkIsStandLikeOnComment(like);

        like.setPost(post);
        post.getLikes().add(like);

        Like likeSaved = likeRepository.save(like);
        LikeDto likeDtoSaved = likeMapper.toDto(likeSaved);

        likeEventPublisher.sendEvent(likeMapper.toLikeEvent(likeSaved));
        kafkaLikePublisher.sendMessage(postId, likeDtoSaved);

        return likeDtoSaved;
    }

    @Transactional
    public LikeDto addLikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Comment comment = commentService.getCommentById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        likeValidator.checkIsStandLikeWithIdOnComment(comment, like);
        likeValidator.checkIsStandLikeOnPost(like);

        like.setComment(comment);
        comment.getLikes().add(like);

        return likeMapper.toDto(likeRepository.save(like));
    }

    @Transactional
    public LikeDto deleteLikePost(Long postId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Post post = postService.findPostById(postId);
        Like like = likeMapper.toEntity(likeDto);

        post.setLikes(post.getLikes().stream()
                .filter(like1 -> !like1.getUserId().equals(like.getUserId()))
                .collect(Collectors.toList()));
        like.setPost(null);

        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto deleteLikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Comment comment = commentService.getCommentById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        comment.setLikes(comment.getLikes().stream()
                .filter(like1 -> !like1.getUserId().equals(like.getUserId()))
                .collect(Collectors.toList()));

        like.setComment(null);

        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());

        return likeMapper.toDto(like);
    }
}