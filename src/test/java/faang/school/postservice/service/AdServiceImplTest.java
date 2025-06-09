package faang.school.postservice.service;

import faang.school.postservice.model.ad.AdStatus;
import faang.school.postservice.repository.adapter.AdRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdServiceImplTest {
    private static final int BATCH_SIZE = 10;

    @Mock
    private AdRepositoryAdapter adRepositoryAdapter;

    @InjectMocks
    private AdServiceImpl adService;

    @Captor
    private ArgumentCaptor<AdStatus> adStatusCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> localDateTimeCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Captor
    private ArgumentCaptor<List<Long>> adIdsBatchCaptor;

    @Captor
    private ArgumentCaptor<Integer> batchNumberCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adService, "deleteBatchSize", BATCH_SIZE);
    }

    @Test
    @DisplayName("updateExpiredAds should update ads and log count when ads are updated")
    void updateExpiredAds_shouldUpdateAdsAndLogCount_whenAdsAreUpdated() {
        when(adRepositoryAdapter.updateExpiredAds(eq(AdStatus.EXPIRED), eq(AdStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(5);

        assertDoesNotThrow(() -> adService.updateExpiredAds());

        verify(adRepositoryAdapter).updateExpiredAds(adStatusCaptor.capture(),
                adStatusCaptor.capture(), localDateTimeCaptor.capture());
        assertEquals(AdStatus.EXPIRED, adStatusCaptor.getAllValues().get(0));
        assertEquals(AdStatus.ACTIVE, adStatusCaptor.getAllValues().get(1));
        assertTrue(ChronoUnit.SECONDS.between(localDateTimeCaptor.getValue(), LocalDateTime.now()) < 1);
    }

    @Test
    @DisplayName("updateExpiredAds should log no updates when no ads are expired")
    void updateExpiredAds_shouldLogNoUpdates_whenNoAdsAreExpired() {
        when(adRepositoryAdapter.updateExpiredAds(eq(AdStatus.EXPIRED), eq(AdStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(0);

        assertDoesNotThrow(() -> adService.updateExpiredAds());

        verify(adRepositoryAdapter)
                .updateExpiredAds(any(AdStatus.class), any(AdStatus.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("updateExpiredAds should throw RuntimeException when repository throws exception")
    void updateExpiredAds_shouldThrowRuntimeException_whenRepositoryThrowsException() {
        when(adRepositoryAdapter.updateExpiredAds(eq(AdStatus.EXPIRED), eq(AdStatus.ACTIVE), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> adService.updateExpiredAds());
        assertEquals("Error updating expired ads", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertEquals("Database error", exception.getCause().getMessage());

        verify(adRepositoryAdapter)
                .updateExpiredAds(any(AdStatus.class), any(AdStatus.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should do nothing if no expired ads found")
    void deleteExpiredAdsInBatches_shouldDoNothing_ifNoExpiredAdsFound() {
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());
        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(emptyPage);

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter).findAdIdsByStatus(eq(AdStatus.EXPIRED), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(BATCH_SIZE, pageableCaptor.getValue().getPageSize());
        verify(adRepositoryAdapter, never()).deleteExpiredAdsBatchAsync(anyList(), anyInt());
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should process one batch successfully")
    void deleteExpiredAdsInBatches_shouldProcessOneBatchSuccessfully() {
        List<Long> adIds = List.of(1L, 2L, 3L);
        Page<Long> pageWithAds = new PageImpl<>(adIds);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());

        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(pageWithAds)
                .thenReturn(emptyPage);

        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(adIds), eq(1)))
                .thenReturn(CompletableFuture.completedFuture(adIds.size()));

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter, times(2))
                .findAdIdsByStatus(eq(AdStatus.EXPIRED), pageableCaptor.capture());
        List<Pageable> pageables = pageableCaptor.getAllValues();
        assertEquals(0, pageables.get(0).getPageNumber());
        assertEquals(1, pageables.get(1).getPageNumber());

        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(adIdsBatchCaptor.capture(), batchNumberCaptor.capture());
        assertEquals(adIds, adIdsBatchCaptor.getValue());
        assertEquals(1, batchNumberCaptor.getValue());
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should process multiple batches successfully")
    void deleteExpiredAdsInBatches_shouldProcessMultipleBatchesSuccessfully() {
        List<Long> batch1Ids =
                LongStream.rangeClosed(1, BATCH_SIZE).boxed().collect(Collectors.toList());
        List<Long> batch2Ids =
                LongStream.rangeClosed(BATCH_SIZE + 1, BATCH_SIZE + 5).boxed().collect(Collectors.toList());

        Page<Long> page1 = new PageImpl<>(batch1Ids);
        Page<Long> page2 = new PageImpl<>(batch2Ids);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());

        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2)
                .thenReturn(emptyPage);

        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1)))
                .thenReturn(CompletableFuture.completedFuture(batch1Ids.size()));
        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2)))
                .thenReturn(CompletableFuture.completedFuture(batch2Ids.size()));

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter, times(3))
                .findAdIdsByStatus(eq(AdStatus.EXPIRED), pageableCaptor.capture());
        List<Pageable> pageables = pageableCaptor.getAllValues();
        assertEquals(0, pageables.get(0).getPageNumber());
        assertEquals(1, pageables.get(1).getPageNumber());
        assertEquals(2, pageables.get(2).getPageNumber());
        assertEquals(Sort.by("id").ascending(), pageables.get(0).getSort());


        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2));
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should throw RuntimeException when findAdIdsByStatus fails")
    void deleteExpiredAdsInBatches_shouldThrowRuntimeException_whenFindAdIdsByStatusFails() {
        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenThrow(new RuntimeException("DB find error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> adService.deleteExpiredAdsInBatches());

        assertEquals("error while deleting expired ads", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("DB find error", exception.getCause().getMessage());

        verify(adRepositoryAdapter, never()).deleteExpiredAdsBatchAsync(anyList(), anyInt());
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should handle async deletion failure for a batch")
    void deleteExpiredAdsInBatches_shouldHandleAsyncDeletionFailure() {
        List<Long> adIds = List.of(1L, 2L);
        Page<Long> pageWithAds = new PageImpl<>(adIds);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());
        CompletableFuture<Integer> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Async DB Error"));

        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(pageWithAds)
                .thenReturn(emptyPage);
        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(adIds), eq(1)))
                .thenReturn(failedFuture);

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());
        verify(adRepositoryAdapter, times(2))
                .findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(adIds), eq(1));
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches should stop if thread is interrupted")
    void deleteExpiredAdsInBatches_shouldStop_ifThreadIsInterrupted() {
        Thread.currentThread().interrupt();

        adService.deleteExpiredAdsInBatches();

        verify(adRepositoryAdapter, never()).findAdIdsByStatus(any(), any());
        verify(adRepositoryAdapter, never()).deleteExpiredAdsBatchAsync(anyList(), anyInt());

        assertTrue(Thread.interrupted(), "Thread interrupted status should be cleared");
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches processes multiple batches and handles one failing batch")
    void deleteExpiredAdsInBatches_processesMultipleAndHandlesOneFailure() {
        List<Long> batch1Ids = List.of(1L, 2L);
        List<Long> batch2Ids = List.of(3L, 4L);
        Page<Long> page1 = new PageImpl<>(batch1Ids);
        Page<Long> page2 = new PageImpl<>(batch2Ids);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());

        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2)
                .thenReturn(emptyPage);

        CompletableFuture<Integer> successfulFuture =
                CompletableFuture.completedFuture(batch1Ids.size());
        CompletableFuture<Integer> failedFuture =
                CompletableFuture.failedFuture(new RuntimeException("Batch 2 DB Error"));

        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1))).thenReturn(successfulFuture);
        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2))).thenReturn(failedFuture);

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter, times(3))
                .findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2));
    }

    @Test
    @DisplayName("deleteExpiredAdsInBatches with no ads found after first batch")
    void deleteExpiredAdsInBatches_noAdsAfterFirstBatch() {
        List<Long> adIdsBatch1 = LongStream.range(0, BATCH_SIZE).boxed().collect(Collectors.toList());
        Page<Long> page1 = new PageImpl<>(adIdsBatch1, PageRequest.of(0, BATCH_SIZE), BATCH_SIZE);
        Page<Long> emptyPage = Page.empty(PageRequest.of(1, BATCH_SIZE));


        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), pageableCaptor.capture()))
                .thenReturn(page1)
                .thenReturn(emptyPage);

        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(adIdsBatch1), eq(1)))
                .thenReturn(CompletableFuture.completedFuture(adIdsBatch1.size()));

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter, times(2))
                .findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(adIdsBatch1), eq(1));

        List<Pageable> capturedPageables = pageableCaptor.getAllValues();
        assertEquals(0, capturedPageables.get(0).getPageNumber());
        assertEquals(1, capturedPageables.get(1).getPageNumber());
    }


    @Test
    @DisplayName("processBatchResults sums results from successful futures even if one future in allOf failed")
    void processBatchResults_sumsSuccessfulEvenIfAllOfFails() {
        List<Long> batch1Ids = List.of(1L, 2L);
        List<Long> batch2Ids = List.of(3L, 4L);

        Page<Long> page1 = new PageImpl<>(batch1Ids);
        Page<Long> page2 = new PageImpl<>(batch2Ids);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());

        when(adRepositoryAdapter.findAdIdsByStatus(eq(AdStatus.EXPIRED), any(Pageable.class)))
                .thenReturn(page1)
                .thenReturn(page2)
                .thenReturn(emptyPage);

        CompletableFuture<Integer> successfulFuture = CompletableFuture.completedFuture(batch1Ids.size());
        CompletableFuture<Integer> failedFuture = CompletableFuture.failedFuture(new RuntimeException("Batch 2 Error"));

        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1))).thenReturn(successfulFuture);
        when(adRepositoryAdapter.deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2))).thenReturn(failedFuture);

        assertDoesNotThrow(() -> adService.deleteExpiredAdsInBatches());

        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch1Ids), eq(1));
        verify(adRepositoryAdapter).deleteExpiredAdsBatchAsync(eq(batch2Ids), eq(2));
    }
}