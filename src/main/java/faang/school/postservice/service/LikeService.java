package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.NotFoundElementException;
import faang.school.postservice.exception.ValidationServiceExceptions;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public LikeDto createLikeToPost(@NotNull LikeDto likeDto) {
        if (likeDto.getLikeId() != null && likeRepository.existsById(likeDto.getLikeId())) {
            throwExceptionLikeService("it is impossible to create an existing like");
        }
        var post = validationAndPostReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        var entityLike = likeMapper.toEntity(likeDto);
        entityLike.setPost(post);
        var createLike = likeRepository.save(entityLike);
        post.getLikes().add(createLike);
        postRepository.save(post);
        return likeMapper.toDto(createLike);
    }

    @Transactional
    public LikeDto removeLikeToPost(@NotNull LikeDto likeDto) {
        if (likeDto.getLikeId() == null || !likeRepository.existsById(likeDto.getLikeId())) {
            throwExceptionLikeService("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: " + likeDto.getLikeId());
        }
        var post = validationAndPostReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        post.getLikes().remove(likeRepository.findById(likeDto.getLikeId()).get());
        likeRepository.delete(likeMapper.toEntity(likeDto));
        postRepository.save(post);
        return likeDto;
    }

    @Transactional
    public LikeDto createLikeToComment(@NotNull LikeDto likeDto) {
        if (likeDto.getLikeId() != null && likeRepository.existsById(likeDto.getLikeId())) {
            throwExceptionLikeService("it is impossible to create an existing like");
        }
        var comment = validationAndCommentsReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        var entityLike = likeMapper.toEntity(likeDto);
        entityLike.setComment(comment);
        var createLike = likeRepository.save(entityLike);
        comment.getLikes().add(createLike);
        commentRepository.save(comment);
        return likeMapper.toDto(createLike);
    }

    @Transactional
    public LikeDto removeLikeToComment(@NotNull LikeDto likeDto) {
        if (likeDto.getLikeId() == null || !likeRepository.existsById(likeDto.getLikeId())) {
            throwExceptionLikeService("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: " + likeDto.getLikeId());
        }
        var comment = validationAndCommentsReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        comment.getLikes().remove(likeRepository.findById(likeDto.getLikeId()).get());
        likeRepository.delete(likeMapper.toEntity(likeDto));
        commentRepository.save(comment);
        return likeDto;
    }

    private Post validationAndPostReceived(@NotNull LikeDto likeDto) {
        if (likeDto.getPostId() != null) {
            if (!postRepository.existsById(likeDto.getPostId())) {
                throwExceptionLikeService("no such postId exists postId: " + likeDto.getPostId());
            }
        } else {
            throwExceptionLikeService("arrived likeDto with postId equal to null");
        }
        return postRepository.findById(likeDto.getPostId()).orElseThrow(() ->
                new NotFoundElementException("Not found post by id: " + likeDto.getPostId()));
    }

    private Comment validationAndCommentsReceived(@NotNull LikeDto likeDto) {
        if (likeDto.getCommentId() != null) {
            if (!commentRepository.existsById(likeDto.getCommentId())) {
                throwExceptionLikeService("no such postId exists commentId: " + likeDto.getCommentId());
            }
        } else {
            throwExceptionLikeService("arrived likeDto with postId and commentId equal to null");
        }
        return commentRepository.findById(likeDto.getCommentId()).orElseThrow(() ->
                new NotFoundElementException("Not found comment by id: " + likeDto.getCommentId()));
    }

    private void throwExceptionLikeService(String messageError) {
        String startMessageError = "In LikeService class ";
        log.error(startMessageError + messageError);
        throw new ValidationServiceExceptions(startMessageError + messageError);
    }
}
