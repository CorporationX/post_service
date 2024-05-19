package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataLikeValidation;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public LikeDto addLikePost(Long postId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Post post = getPostById(postId);
        Like like = likeMapper.toEntity(likeDto);

        if (post.getLikes().stream().anyMatch(like1 -> like1.getUserId().equals(like.getUserId()))) {
            throw new DataLikeValidation("Лайк от пользователя с id " + like.getUserId() + " уже поставлен на пост с id" + postId);
        }

        if (like.getComment() == null) {
            like.setPost(post);
        } else {
            throw new DataLikeValidation("Этот лайк уже стоит на комментарии.");
        }

        post.getLikes().add(like);

        return likeMapper.toDto(likeRepository.save(like));
    }

    public LikeDto addLikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Comment comment = getCommentById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        if (comment.getLikes().stream().anyMatch(like1 -> like1.getUserId().equals(like.getUserId()))) {
            throw new DataLikeValidation("Лайк от пользователя с id " + like.getUserId() + " уже поставлен на комментарий с id" + commentId);
        }

        if (like.getPost() == null) {
            like.setComment(comment);
        } else {
            throw new DataLikeValidation("Этот лайк уже стоит на посте.");
        }
        comment.getLikes().add(like);

        return likeMapper.toDto(likeRepository.save(like));
    }

    public LikeDto deleteLikePost(Long postId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Post post = getPostById(postId);
        Like like = likeMapper.toEntity(likeDto);

        post.setLikes(post.getLikes().stream()
                .filter(like1 -> !like1.getUserId().equals(like.getUserId()))
                .collect(Collectors.toList()));
        like.setPost(null);

        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());

        return likeMapper.toDto(like);
    }

    public LikeDto deleteLikeComment(Long commentId, LikeDto likeDto) {
        likeValidator.checkExistAuthor(likeDto);

        Comment comment = getCommentById(commentId);
        Like like = likeMapper.toEntity(likeDto);

        comment.setLikes(comment.getLikes().stream()
                .filter(like1 -> !like1.getUserId().equals(like.getUserId()))
                .collect(Collectors.toList()));

        like.setComment(null);

        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());

        return likeMapper.toDto(like);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new DataLikeValidation("Поста с id " + postId + " нет в базе данных."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new DataLikeValidation("Комментария с id " + commentId + " нет в базе данных."));
    }
}