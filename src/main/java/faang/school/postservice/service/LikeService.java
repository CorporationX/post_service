package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final ResponsePostMapper postMapper;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;

    public LikeDto likePost(LikeDto likeDto) {
        validUser(likeDto);
        long postId = likeDto.getPostId();
        ResponsePostDto responsePostDto = postService.getById(postId);
        likeDto.setPost(responsePostDto);
        Like like = likeRepository.save(likeMapper.toLike(likeDto));
        return likeMapper.toLikeDto(like);
    }

    public void deleteLikePost(Long postId, Long userId) {
        userServiceClient.getUser(userId);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public LikeDto likeComment(LikeDto likeDto) {
        validUser(likeDto);
        long commentId = likeDto.getCommentId();
        CommentDto commentDto = commentService.getCommentById(commentId);
        likeDto.setCommentDto(commentDto);
        Like like = likeRepository.save(likeMapper.toLike(likeDto));
        return likeMapper.toLikeDto(like);
    }

    private void validUser(LikeDto likeDto) {
        long userId = likeDto.getUserId();
        userServiceClient.getUser(userId);
    }
}
