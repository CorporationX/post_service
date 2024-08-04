package faang.school.postservice.repository;

import faang.school.postservice.model.Post;

import java.util.List;

public interface CustomPostRepository {

    int[] updateVerifiedInfo(List<Post> posts);
}
