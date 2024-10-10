package faang.school.postservice.util.album;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.album.Album;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static faang.school.postservice.model.album.AlbumVisibility.ALL_USERS;

public class BuilderForAlbumsTests {
    private static final Random random = new Random();

    public static Album buildAlbum(long albumId, String title, String description, long authorId, List<Post> posts) {
        return Album.builder()
                .id(albumId)
                .title(title)
                .description(description)
                .authorId(authorId)
                .posts(posts)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(String title, String description) {
        return Album.builder()
                .title(title)
                .description(description)
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long albumId, long authorId) {
        return Album.builder()
                .id(albumId)
                .authorId(authorId)
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long albumId, String title, long authorId, List<Post> posts) {
        return Album.builder()
                .id(albumId)
                .title(title)
                .authorId(authorId)
                .posts(posts)
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long albumId, long authorId, long fromMinusDays, long toMinusDays) {
        return Album.builder()
                .id(albumId)
                .authorId(authorId)
                .createdAt(LocalDateTime.now().minusDays(getRandomLong(fromMinusDays, toMinusDays)))
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long albumId, String title, long fromMinusDays, long toMinusDays) {
        return Album.builder()
                .id(albumId)
                .title(title)
                .createdAt(LocalDateTime.now().minusDays(getRandomLong(fromMinusDays, toMinusDays)))
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long albumId, String title, String description, long authorId) {
        return Album.builder()
                .id(albumId)
                .title(title)
                .description(description)
                .authorId(authorId)
                .visibility(ALL_USERS)
                .build();
    }

    public static Album buildAlbum(long authorId) {
        return Album.builder()
                .authorId(authorId)
                .visibility(ALL_USERS)
                .build();
    }

    public static Post buildPost(long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }

    public static long getRandomLong(long from, long to) {
        return random.nextLong(from, to);
    }

}
