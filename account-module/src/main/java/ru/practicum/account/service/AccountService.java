package ru.practicum.account.service;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.account.dto.Payment;
import ru.practicum.account.exception.NotEnoughBalanceException;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AccountService {

    private final AtomicReference<BigDecimal> atomicBalance;

    public AccountService(@Value("${initBalance}") final BigDecimal balance) {
        this.atomicBalance = new AtomicReference<>(balance);
    }

    public Mono<BigDecimal> getBalance() {
        return Mono.fromCallable(atomicBalance::get);
    }

    public Mono<Void> addBalance(@NotNull final BigDecimal amount) {
        return Mono.fromRunnable(() -> atomicBalance.updateAndGet(atomic -> atomic.add(amount)));
    }

    public Mono<BigDecimal> pay(Mono<Payment> changeAmount) {
        return changeAmount.map(Payment::getAmount).flatMap(this::pay);
    }

    public Mono<BigDecimal> pay(@NotNull @Positive final BigDecimal changeAmount) {
        return Mono.fromCallable(
                        () -> {
                            var empty = Mono.empty().then();

                            if (changeAmount.signum() > 0) {
                                empty = subtractBalance(changeAmount);
                            }

                            return empty;
                        }
                )
                .flatMap(mono -> mono)
                .then(Mono.defer(this::getBalance));
    }

    protected Mono<Void> subtractBalance(@NotNull @Positive final BigDecimal amount) {
        return Mono.fromRunnable(() -> {
            BigDecimal current;
            BigDecimal newValue;

            do {
                current = atomicBalance.get();
                newValue = current.subtract(amount);

                if (newValue.compareTo(BigDecimal.ZERO) < 0) {
                    throw new NotEnoughBalanceException();
                }

            } while (!atomicBalance.compareAndSet(current, newValue));
        });
    }
}
