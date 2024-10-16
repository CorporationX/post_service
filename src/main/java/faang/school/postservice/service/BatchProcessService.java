package faang.school.postservice.service;

import faang.school.postservice.model.Post;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BatchProcessService {

    CompletableFuture<Void> processBatch(List<Post> postsBatch);
}