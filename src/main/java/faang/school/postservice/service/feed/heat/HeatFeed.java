package faang.school.postservice.service.feed.heat;

public interface HeatFeed {
    void addInfoToRedis(Long userId, Long postIdList);
}
