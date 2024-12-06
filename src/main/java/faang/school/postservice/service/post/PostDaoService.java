package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;

import java.util.List;

public interface PostDaoService {

    List<Post> findAll();

    Post getById(Long id);

    Post create(Post post);

    Post publish(Long id);

    Post update(Post post);

    Post deleteById(Long id);

    List<Post> filterNonPublishedPostsByTime(List<Post> posts);

    List<Post> getAllNonPublishedByAuthorId(Long id);
}
