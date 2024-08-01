package faang.school.postservice.logging;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logging {
    public void log(String message, Long id, String method) {
        switch (method) {
            case "info" -> {
                if (id != null) {
                    log.info(message, id);
                } else {
                    log.info(message);
                }
            }
            case "debug" -> {
                if (id != null) {
                    log.debug(message, id);
                } else log.debug(message);
            }
            case "trace" -> {
                if (id != null) {
                    log.trace(message, id);
                } else log.trace(message);
            }
            case "warn" -> {
                if (id != null) {
                    log.warn(message, id);
                } else {
                    log.warn(message);
                }
            }
            case "error" -> {
                if (id != null) {
                    log.error(message, id);
                } else log.error(message);
            }
            default -> throw new IllegalStateException("Unexpected value: " + method);
        }
    }
}
