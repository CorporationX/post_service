package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface PostService {

    List<PostDto> getPostsByHashtag(String hashtag);

    //temp method
    PostDto activate(PostDto postDto);

    //temp method
    PostDto updatePost(PostDto postDto);
}
