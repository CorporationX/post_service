package faang.school.postservice.client;

import faang.school.postservice.dto.payment.PaymentRequest;
import faang.school.postservice.dto.payment.PaymentResponse;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",
        url = "${services.payment-service.host}:${services.payment-service.port}",
        configuration = FeignConfig.class)
public interface PaymentServiceClient {

    @Retryable(
            retryFor = { FeignException.class, RetryableException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @PostMapping("/api/payment")
    ResponseEntity<PaymentResponse> sendPayment(@RequestBody PaymentRequest paymentRequest);
}
