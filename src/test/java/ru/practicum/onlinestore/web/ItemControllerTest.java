package ru.practicum.onlinestore.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.practicum.onlinestore.configuration.WebConfiguration;
import ru.practicum.onlinestore.controller.ItemController;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ItemSearchResponse;
import ru.practicum.onlinestore.dto.response.Paging;
import ru.practicum.onlinestore.service.ItemService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(ItemController.class)
@Import(WebConfiguration.class)
class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getItem() {
        String title = "title";
        var response = new ItemSearchResponse(
                List.of(List.of(new ItemDTO(0L, title, "", new BigDecimal(0), new BigDecimal(0), ""))),
                new Paging(false, 0, 0)
        );

        Mockito.when(itemService.paginateSearch(any())).thenReturn(Mono.just(response));

        webTestClient.get().uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//body/table/tr[2]/td/table/tr[2]/td[1]/a", "title");
    }
}
