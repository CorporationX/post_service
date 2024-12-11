package faang.school.postservice.service.moderation.sightengine.check.impl;

import faang.school.postservice.service.moderation.sightengine.check.ModerationCheck;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseCheck implements ModerationCheck {

    private final double value;
    private final double target;

    @Override
    public boolean check() {
        return value <= target;
    }
}
