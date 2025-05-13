package ru.practicum.onlinestore.web;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.onlinestore.configuration.WebConfiguration;
import ru.practicum.onlinestore.controller.ItemController;
import ru.practicum.onlinestore.dto.response.ItemDTO;
import ru.practicum.onlinestore.dto.response.ItemSearchResponse;
import ru.practicum.onlinestore.dto.response.Paging;
import ru.practicum.onlinestore.service.ItemService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(WebConfiguration.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    public void getItem() throws Exception {
        String title = "title";
        var response = new ItemSearchResponse(
                List.of(List.of(new ItemDTO(0L, title, "", new BigDecimal(0), new BigDecimal(0), 0L))),
                new Paging(false, 0, 0)
        );

        Mockito.when(itemService.paginateSearch(any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(xpath("//body/table/tr[2]/td/table/tr[2]/td[1]/a").string("title"));
    }
}
