package ru.practicum.account;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.practicum.account.exception.NotEnoughBalanceException;
import ru.practicum.account.service.AccountService;

import java.math.BigDecimal;
import java.util.Random;

class AccountServiceTest {

    private final Random random = new Random();

    private final BigDecimal initBalance = BigDecimal.valueOf(random.nextDouble());

    private final AccountService accountService =
            new AccountService(initBalance);

    @Test
    void getBalanceTest() {
        var balance = accountService.getBalance().block();

        Assertions.assertThat(balance)
                .isNotNull()
                .isEqualTo(initBalance);
    }

    @Test
    void addBalanceTest() {
        BigDecimal plusAmount = BigDecimal.valueOf(random.nextDouble());
        var oldBalance = accountService.getBalance().block();
        accountService.addBalance(plusAmount).block();
        var newBalance = accountService.getBalance().block();

        Assertions.assertThat(newBalance)
                .isEqualTo(oldBalance.add(plusAmount));
    }

    @Test
    void subtractTest() throws NotEnoughBalanceException {
        accountService.addBalance(BigDecimal.TEN).block();
        var oldBalance = accountService.getBalance().block();

        accountService.pay(BigDecimal.ONE).block();

        var newBalance = accountService.getBalance().block();

        Assertions.assertThat(newBalance).isEqualTo(oldBalance.subtract(BigDecimal.ONE));
    }


    @Test
    void overBalanceSubtractTest() {
        var balance = accountService.getBalance().block();
        Assert.assertThrows(NotEnoughBalanceException.class, () -> accountService.pay(balance.add(BigDecimal.ONE)).block());
    }
}
