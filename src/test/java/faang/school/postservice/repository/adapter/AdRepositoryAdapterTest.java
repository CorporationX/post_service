package faang.school.postservice.repository.adapter;

import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdRepositoryAdapterTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdRepositoryAdapter adRepositoryAdapter;

    @Captor
    private ArgumentCaptor<List<Long>> adIdsCaptor;

    @Test
    @DisplayName("deleteExpiredAdsBatchAsync should return 0 if batch is empty")
    void deleteExpiredAdsBatchAsync_shouldReturnZero_ifBatchIsEmpty()
            throws ExecutionException, InterruptedException {
        List<Long> emptyBatch = Collections.emptyList();
        int currentBatchNum = 1;

        CompletableFuture<Integer> resultFuture =
                adRepositoryAdapter.deleteExpiredAdsBatchAsync(emptyBatch, currentBatchNum);

        assertNotNull(resultFuture);
        assertTrue(resultFuture.isDone());
        assertFalse(resultFuture.isCompletedExceptionally());
        assertEquals(0, resultFuture.get());
        verify(adRepository, never()).deletePostAds(anyList());
    }

    @Test
    @DisplayName("deleteExpiredAdsBatchAsync should delete ads and return batch size on success")
    void deleteExpiredAdsBatchAsync_shouldDeleteAdsAndReturnBatchSize_onSuccess()
            throws ExecutionException, InterruptedException {
        List<Long> batchToRemove = List.of(1L, 2L, 3L);
        int currentBatchNum = 1;

        // Mock the void method
        doNothing().when(adRepository).deletePostAds(anyList());

        CompletableFuture<Integer> resultFuture =
                adRepositoryAdapter.deleteExpiredAdsBatchAsync(batchToRemove, currentBatchNum);

        assertNotNull(resultFuture);
        Integer result = resultFuture.get();

        assertEquals(batchToRemove.size(), result);
        verify(adRepository).deletePostAds(adIdsCaptor.capture());
        assertEquals(batchToRemove, adIdsCaptor.getValue());
    }

    @Test
    @DisplayName("deleteExpiredAdsBatchAsync should return exceptionally completed future on repository error")
    void deleteExpiredAdsBatchAsync_shouldReturnExceptionallyCompletedFuture_onRepositoryError() {
        List<Long> batchToRemove = List.of(1L, 2L, 3L);
        int currentBatchNum = 1;
        RuntimeException dbException = new RuntimeException("Database connection error");

        doThrow(dbException).when(adRepository).deletePostAds(batchToRemove);

        CompletableFuture<Integer> resultFuture =
                adRepositoryAdapter.deleteExpiredAdsBatchAsync(batchToRemove, currentBatchNum);

        assertNotNull(resultFuture);
        assertTrue(resultFuture.isDone());
        assertTrue(resultFuture.isCompletedExceptionally());

        ExecutionException executionException = assertThrows(ExecutionException.class, resultFuture::get);
        Throwable cause = executionException.getCause();
        assertNotNull(cause);
        assertEquals(RuntimeException.class, cause.getClass());
        assertEquals("Error deleting ads in batch " + currentBatchNum, cause.getMessage());

        verify(adRepository).deletePostAds(batchToRemove);
    }

    @Test
    @DisplayName("deleteExpiredAdsBatchAsync should return 0 if thread is interrupted")
    void deleteExpiredAdsBatchAsync_shouldReturnZero_ifThreadIsInterrupted()
            throws ExecutionException, InterruptedException {
        List<Long> batchToRemove = List.of(1L, 2L, 3L);
        int currentBatchNum = 1;
        Thread.currentThread().interrupt();

        CompletableFuture<Integer> resultFutureActual =
                adRepositoryAdapter.deleteExpiredAdsBatchAsync(batchToRemove, currentBatchNum);
        assertEquals(0, resultFutureActual.get());
    }
}
