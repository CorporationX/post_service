package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentValidation commentValidation;
    private final CommentMapper commentMapper;


    public CommentDto addNewComment(Long id, CommentDto commentDto) {
        commentValidation.validateCommentAuthor(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = postRepository.findById(commentDto.getAuthorId()).orElseThrow(null);
        Post post = getPostById(post, id);
        comment.setPost(post);
        commentRepository.save(comment);
        return commentDto;
    }

    public CommentDto changeComment(Long id, CommentDto commentDto) {
        commentValidation.validateCommentAuthor(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentDto;
    }

    public CommentDto deleteComment(Long id, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        commentRepository.deleteById(id);
        return commentDto;
    }

    public List<CommentDto> getAllComments(Long id) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(id);
        return commentMapper.toDtoList(allByPostId);
    }

    public Post getPostById(List<Post> posts, Long id) {
        return posts.stream()
                .filter(p -> p.getAuthorId().equals(id))
                .findFirst()
                .orElseThrow(null);
    }
}
