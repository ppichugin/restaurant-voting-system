package kz.pichugin.restaurantvotingsystem.web.restaurant;

import kz.pichugin.restaurantvotingsystem.util.RestaurantUtil;
import kz.pichugin.restaurantvotingsystem.web.AbstractControllerTest;
import kz.pichugin.restaurantvotingsystem.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.dish.DishTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = UserTestData.USER_MAIL)
class RestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantController.REST_URL + '/';

    @Test
    void getByIdWithMenuToday() throws Exception {
        citybrew.setMenu(citybrew_menu);
        perform(MockMvcRequestBuilders.get(REST_URL + CITYBREW_ID + "/with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_TO_MATCHER.contentJson(RestaurantUtil.getRestaurantTo(citybrew)));
    }

    @Test
    void getAllWithMenuToday() throws Exception {
        bavarius.setMenu(bavarius_menu);
        citybrew.setMenu(citybrew_menu);
        mokito.setMenu(mokito_menu);
        filadelphia.setMenu(filadelphia_menu);
        roofToHeaven.setMenu(roofToHaven_menu);
        yamato.setMenu(yamato_menu_today);
        perform(MockMvcRequestBuilders.get(REST_URL + "with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_TO_MATCHER.contentJson(RestaurantUtil.getRestaurantTos(
                        List.of(bavarius, citybrew, mokito, filadelphia, roofToHeaven, yamato))));
    }
}