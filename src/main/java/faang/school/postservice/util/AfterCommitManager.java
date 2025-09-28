package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

/**
 * Выполняет задачи асинхронно после коммита транзакции.
 *
 * <p>
 * Если транзакция активна - задача выполняется после её коммита.
 * Если транзакция не активна - задача выполняется немедленно.
 * Все ошибки автоматически логируются и не прерывают основной поток выполнения.
 * </p>
 *
 * @author Linempy
 * @since 15.09.2025
 */
@Slf4j
@Component
public class AfterCommitManager {

    private final Executor asyncExecutor;

    public AfterCommitManager(@Qualifier("afterCommitExecutor") Executor asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    public void executeAfterCommit(Runnable task) {
        executeAfterCommit(task, true);
    }

    public void executeAfterCommit(Runnable task, boolean logErrors) {
        executeAfterCommit(task, e -> {
            if (logErrors) {
                log.error("Ошибка при выполнении afterCommit задачи: {}", e.getMessage(), e);
            }
        });
    }

    public void executeAfterCommit(Runnable task, Consumer<Exception> errorHandler) {
        Runnable safeTask = wrapWithErrorHandling(task, errorHandler);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            registerTransactionSynchronization(safeTask);
        } else {
            executeImmediately(safeTask);
        }
    }

    private Runnable wrapWithErrorHandling(Runnable task, Consumer<Exception> errorHandler) {
        return () -> {
            try {
                task.run();
                log.debug("AfterCommit задача успешно выполнена");
            } catch (Exception e) {
                errorHandler.accept(e);
            }
        };
    }

    private void registerTransactionSynchronization(Runnable task) {
        log.debug("Регистрация afterCommit синхронизации");
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        log.debug("Транзакция закоммичена, запуск afterCommit задачи");
                        asyncExecutor.execute(task);
                    }
                }
        );
    }

    private void executeImmediately(Runnable task) {
        log.debug("Транзакция не активна, немедленный запуск задачи");
        asyncExecutor.execute(task);
    }
}