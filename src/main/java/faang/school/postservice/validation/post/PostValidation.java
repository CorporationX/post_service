package faang.school.postservice.validation.post;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PostValidation {

    public static void validateNotNullAuthor(Post post) {
        if (Objects.isNull(post.getAuthorId())) {
            throw new DataValidationException("Author of the post can not be null");
        }
    }

    public static void validatePostExists(boolean isExist) {
        if (!isExist) {
            throw new DataValidationException("Post with such id does not exist");
        }
    }

    public static void validatePostDoesNotExist(boolean isExist) {
        if (isExist) {
            throw new DataValidationException("Post with such id already exists");
        }
    }

    public static void validateNotNullContent(Post post) {
        if (Objects.isNull(post.getContent())) {
            throw new DataValidationException("Content of the post can not be null");
        }
    }

    public static void validateNotAlreadyPublishedPost(Post post) {
        if (post.isPublished()) {
            throw new DataValidationException("Post was already published");
        }
    }

    public static void validateNotAlreadyDeletedPost(Post post) {
        if (post.isDeleted()) {
            throw new DataValidationException("Post was already deleted");
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
