package faang.school.postservice.service;

/**
 * Defines the contract for services managing advertisement (Ad) entities within the system.
 *
 * <p>This interface outlines the core operations related to the lifecycle of ads,
 * specifically focusing on handling their expiration and subsequent cleanup.
 * Implementations are expected to interact with a persistence layer to modify ad states
 * and remove ad records.
 *
 * <p>Designed to be invoked by scheduled processes or administrative actions, these
 * methods help maintain data integrity and system performance by managing stale ad data.
 */
public interface AdService {

    /**
     * Scans for active advertisements whose designated end-time has passed and
     * transitions their status to indicate expiration.
     *
     * <p>Implementations should:
     * <ul>
     *     <li>Identify ads that are currently considered active (e.g., status {@code ACTIVE}).</li>
     *     <li>Compare their scheduled expiration timestamp against the current system time.</li>
     *     <li>Update the status of ads whose expiration time is in the past to a designated
     *         "expired" status (e.g., status {@code EXPIRED}).</li>
     *     <li>Ensure atomicity for the update operations, typically within a transaction,
     *         to maintain data consistency.</li>
     * </ul>
     * This method is crucial for ensuring that ads are not displayed or processed beyond
     * their intended lifecycle. It is expected to be called periodically.
     *
     * @see faang.school.postservice.model.ad.AdStatus For typical ad statuses like ACTIVE and EXPIRED.
     */
    void updateExpiredAds();

    /**
     * Permanently removes advertisements that have been previously marked as EXPIRED from the system.
     *
     * <p>This operation is critical for data hygiene and resource management. To prevent
     * excessive load on the system, especially when dealing with a large volume of expired ads,
     * implementations are expected to:
     * <ul>
     *     <li>Process deletions in manageable, configurable batches.</li>
     *     <li>Fetch only the necessary information (e.g., ad IDs) for deletion to optimize memory usage.</li>
     *     <li>Potentially execute the deletion tasks asynchronously to avoid blocking the calling thread
     *         for extended periods. If asynchronous, the method may return before all deletions
     *         are complete. Implementations should clearly document this behavior.</li>
     *     <li>Include robust logging for monitoring progress and any errors encountered during
     *         the batch deletion process.</li>
     *     <li>Handle potential interruptions gracefully if the execution environment supports it.</li>
     * </ul>
     * This method is typically scheduled to run at regular intervals to clean up the ads data store.
     *
     * @see faang.school.postservice.model.ad.AdStatus For the EXPIRED status.
     */
    void deleteExpiredAdsInBatches();
}
