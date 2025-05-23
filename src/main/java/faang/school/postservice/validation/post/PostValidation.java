package faang.school.postservice.validation.post;

import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;

import java.util.List;

public class PostValidation {

    private static PostRepository postRepository;

    public static void existenceCheckThePost(long postId) {
        List<Post> posts = (List<Post>) postRepository.findAll();
        for(Post post : posts) {
            if(post.getId() != postId) {
                throw new PostNotFoundException(String.format("Post by %d id Not Found ", postId));
            }
        }
    }
}
