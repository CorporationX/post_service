package faang.school.postservice.mapper;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PostViewEventMapper {

    @Autowired
    protected UserContext userContext;

    @Mapping(target = "postId", source = "id")
    @Mapping(target = "userAuthorId", source = "authorId")
    @Mapping(target = "projectAuthorId", source = "projectId")
    @Mapping(target = "viewerId", expression = "java(userContext.getUserId())")
    public abstract PostViewEvent toEvent(Post post);
}
