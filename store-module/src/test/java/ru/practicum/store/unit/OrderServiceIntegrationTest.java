package ru.practicum.store.unit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.practicum.store.api.AccountApi;
import ru.practicum.store.configuration.PostgresContainerBase;
import ru.practicum.store.configuration.RedisContainerBase;
import ru.practicum.store.dto.Account;
import ru.practicum.store.model.Item;
import ru.practicum.store.repository.ItemRepository;
import ru.practicum.store.repository.OrderRepository;
import ru.practicum.store.service.ItemService;
import ru.practicum.store.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@ImportTestcontainers({PostgresContainerBase.class, RedisContainerBase.class})
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private AccountApi accountApi;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void getBalanceTest() {
        var accountAmount = BigDecimal.valueOf(31385L);
        var account = new Account();
        account.amount(accountAmount);
        var balanceResult = Mono.just(account);

        Mockito.when(accountApi.getAmount()).thenReturn(balanceResult);

        var actualAccount = orderService.getBalance().block();

        Assertions.assertThat(actualAccount).isEqualTo(accountAmount);
    }

    @Test
    void payTest() {
        Item item = new Item();
        item.setTitle("title");
        item.setDescription("description");
        item.setPrice(BigDecimal.ONE);
        item.setCount(BigDecimal.TEN);
        item.setImageId("image");

        item = itemRepository.save(item).block();

        var account = new Account();
        account.setAmount(BigDecimal.TEN.add(BigDecimal.ONE));

        var newAccount = new Account();
        newAccount.setAmount(account.getAmount().subtract(BigDecimal.TEN));

        Mockito.when(accountApi.getAmount()).thenReturn(Mono.just(account));
        Mockito.when(accountApi.pay(any())).thenReturn(Mono.just(newAccount));

        var monoList = Mono.just(
                List.of(Objects.requireNonNull(itemService.getById(item.getId()).block()))
        );

        var orderId = orderService.createOrder(monoList).block();

        Assertions.assertThat(orderId).isNotNull();
        Assertions.assertThat(orderRepository.findById(orderId)).isNotNull();
    }
}
