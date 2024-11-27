package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.protobuf.generate.PostPublishedEventProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostPublishedEventMapper {
    default PostPublishedEventProto.PostPublishedEvent toProto(PostPublishedEvent event) {
        if (event == null) {
            return null;
        }

        PostPublishedEventProto.PostPublishedEvent.Builder postPublishedEvent
                = PostPublishedEventProto.PostPublishedEvent.newBuilder();

        postPublishedEvent.setAuthorId(event.getAuthorId());
        postPublishedEvent.setPostId(event.getPostId());

        if (event.getSubscribersIds() != null) {
            postPublishedEvent.addAllSubscribersIds(event.getSubscribersIds());
        }


        return postPublishedEvent.build();
    }

    @Mapping(target = "subscribersIds", source = "subscribersIdsList")
    PostPublishedEvent toEvent(PostPublishedEventProto.PostPublishedEvent postPublishedEvent);

}
