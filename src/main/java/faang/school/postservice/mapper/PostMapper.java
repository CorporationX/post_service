package faang.school.postservice.mapper;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CommentCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = { CommentMapper.class }
)
public abstract class PostMapper {

    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentMapper commentMapper;

    public abstract Post toPost(CreatePostDto createPostDto);

    public abstract void update(UpdatePostDto updatePostDto, @MappingTarget Post entity);

    public abstract PostDto toPostDto(Post post);

    public abstract List<PostDto> toPostDtoList(List<Post> posts);

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "subscribers", ignore = true)
    public abstract PostPublishedEvent toPostPublishedEvent(Post post);

    @Mapping(target = "lastComments", ignore = true)
    public abstract PostCache toPostCache(Post post);

    @AfterMapping
    protected void fillLastComments(Post post, @MappingTarget PostCache postCache) {
        List<Comment> last3 = commentRepository
                .findTop3ByPostIdOrderByCreatedAtDesc(post.getId());
        postCache.setLastComments(
                last3.stream().map(commentMapper::toCommentCache).toList()
        );
    }

    @AfterMapping
    protected void fillSubscribers(Post post, @MappingTarget PostPublishedEvent event) {
        List<Long> subscribers = postRepository.findAllFollowers(post.getAuthorId(), post.getProjectId());
        event.setSubscribers(
                subscribers
        );
    }
}
