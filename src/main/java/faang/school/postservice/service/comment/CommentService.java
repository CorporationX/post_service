package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CommentService {
    private static CommentRepository commentRepository;
    private static UserServiceClient userServiceClient;
    private static PostService postService;
    private static CommentMapper commentMapper;
    public CommentDto addCommentService(Long id, CommentDto commentDto){
        postService.getPost(id);
        return null;
    }

    public CommentDto updateCommentService(Long id, CommentDto commentDto){
        return null;
    }

    public List<CommentDto> getCommentsService(Long id){
        return postService.getPost(id).getComments().stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public CommentDto deleteCommentService(Long id, CommentDto commentDto){
//        validateAuthorExists(commentDto.getAuthorId());
//        List<Comment> comments = postService.getPost(id).getComments();
//        Comment comment = comments.stream().filter(i -> i.getId()==commentDto.getId()).toList();
        return null;
    }
    private void validateAuthorExists(long authorId){
        if (userServiceClient.getUser(authorId) == null){
            log.error("Автора поста нет в базе данных");
            throw new IllegalArgumentException("Автора поста нет в базе данных");
        }
    }
}
