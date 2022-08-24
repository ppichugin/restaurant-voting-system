package kz.pichugin.restaurantvotingsystem.web.restaurant;

import kz.pichugin.restaurantvotingsystem.util.RestaurantUtil;
import kz.pichugin.restaurantvotingsystem.web.AbstractControllerTest;
import kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler;
import kz.pichugin.restaurantvotingsystem.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.dish.DishTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = UserTestData.USER_MAIL)
class RestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL = RestaurantController.REST_URL + '/';

    @Test
    void getByIdWithMenuToday() throws Exception {
        citybrew.setDishes(citybrew_menu);
        perform(MockMvcRequestBuilders.get(REST_URL + CITYBREW_ID + "/with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_TO_MATCHER.contentJson(RestaurantUtil.createRestaurantTo(citybrew)));
    }

    @Test
    void getAllWithMenuToday() throws Exception {
        bavarius.setDishes(bavarius_menu);
        citybrew.setDishes(citybrew_menu);
        mokito.setDishes(mokito_menu);
        filadelphia.setDishes(filadelphia_menu);
        roofToHeaven.setDishes(roofToHaven_menu);
        yamato.setDishes(yamato_menu_today);
        perform(MockMvcRequestBuilders.get(REST_URL + "with-menu"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_TO_MATCHER.contentJson(RestaurantUtil.getRestaurantTos(
                        List.of(bavarius, citybrew, mokito, filadelphia, roofToHeaven, yamato))));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND + "/with-menu"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND)));
    }

    @Test
    @WithAnonymousUser
    void getUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + BAVARIUS_ID + "/with-menu"))
                .andExpect(status().isUnauthorized());
    }
}