package faang.school.postservice.heater;

public interface Heater {
    void addInfoToRedis(Long userId, Long postIdList);
}
