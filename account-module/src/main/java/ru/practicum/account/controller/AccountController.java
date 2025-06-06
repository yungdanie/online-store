package ru.practicum.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.practicum.account.api.AccountApi;
import ru.practicum.account.dto.Account;
import ru.practicum.account.dto.Payment;
import ru.practicum.account.service.AccountService;

@RequestMapping("/api/v1/account")
@RestController
public class AccountController implements AccountApi {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Mono<ResponseEntity<Account>> getAmount(ServerWebExchange exchange) {
        return accountService.getBalance().map(amount -> ResponseEntity.ok(new Account(amount)));
    }

    @Override
    public Mono<ResponseEntity<Account>> pay(Mono<Payment> payment, ServerWebExchange exchange) {
        return accountService.pay(payment).map(amount -> ResponseEntity.ok(new Account(amount)));
    }
}
