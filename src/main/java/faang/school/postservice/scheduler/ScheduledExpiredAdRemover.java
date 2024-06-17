package faang.school.postservice.scheduler;

//import faang.school.postservice.service.ad.AdService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ScheduledExpiredAdRemover {
//    private final AdService adService;
//    private final int batchSize;
//
//    @Autowired
//    public ScheduledExpiredAdRemover(AdService adService, @Value("${ad-remover.scheduler.batch-size}") int batchSize) {
//        this.adService = adService;
//        this.batchSize = batchSize;
//    }
//
//    @Scheduled(cron = "${ad-remover.scheduler.cron}")
//    public void removeExpiredAdsScheduled() {
//        adService.removeExpiredAds(batchSize);
//    }
//
//}
