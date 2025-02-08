package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostDto;
import faang.school.postservice.dto.posts.PostSaveDto;

import java.util.List;

public interface PostService {
    /**
     * Creates a new post.
     *
     * @param postSaveDto the dto containing the details of the post to be created.
     * @return the created post as a dto.
     */
    PostDto create(PostSaveDto postSaveDto);

    /**
     * Retrieves a post by its ID.
     *
     * @param id the ID of the post to retrieve.
     * @return the post as a dto.
     */
    PostDto getPost(long id);

    /**
     * Updates an existing post.
     *
     * @param id          the ID of the post to update.
     * @param postSaveDto the dto containing the updated details of the post.
     * @return the updated post as a dto.
     */
    PostDto update(long id, PostSaveDto postSaveDto);

    /**
     * Publishes a post.
     *
     * @param id the ID of the post to publish.
     */
    void publish(long id);

    /**
     * Deletes a post.
     *
     * @param id the ID of the post to delete.
     */
    void delete(long id);

    /**
     * Retrieves posts by author ID.
     *
     * @param id        the ID of the author.
     * @param published whether to retrieve only published posts.
     * @return a list of posts by the specified author.
     */
    List<PostDto> getPostsByAuthorId(long id, boolean published);

    /**
     * Retrieves posts by project ID.
     *
     * @param id        the ID of the project.
     * @param published whether to retrieve only published posts.
     * @return a list of posts by the specified project.
     */
    List<PostDto> getPostsByProjectId(long id, boolean published);

    /**
     * Publishes scheduled posts.
     *
     * @return the number of posts published on schedule.
     */
    int publishingPostsOnSchedule();
}
