package kz.pichugin.restaurantvotingsystem.web.dish;

import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.repository.DishRepository;
import kz.pichugin.restaurantvotingsystem.util.DishUtil;
import kz.pichugin.restaurantvotingsystem.util.JsonUtil;
import kz.pichugin.restaurantvotingsystem.web.AbstractControllerTest;
import kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler;
import kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData;
import kz.pichugin.restaurantvotingsystem.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static kz.pichugin.restaurantvotingsystem.web.dish.DishTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.START_SEQ;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = UserTestData.ADMIN_MAIL)
class AdminDishControllerTest extends AbstractControllerTest {

    static final String REST_URL = AdminDishController.REST_URL + '/';

    @Autowired
    private DishRepository dishRepository;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL + DISH_START_ID, RestaurantTestData.BAVARIUS_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_MATCHER.contentJson(bavariusDish1));
    }

    @Test
    void getAllByRestaurantAndDate() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL + "by-date", RestaurantTestData.BAVARIUS_ID)
                .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_TO_MATCHER.contentJson(DishUtil.getDishTos(bavarius_menu)));
    }

    @Test
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL, RestaurantTestData.YAMATO_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DISH_TO_MATCHER.contentJson(DishUtil.getDishTos(yamato_menu_all)));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders
                .delete(REST_URL + DISH_START_ID, RestaurantTestData.BAVARIUS_ID))
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.get(START_SEQ + 11, RestaurantTestData.BAVARIUS_ID).isPresent());
    }

    @Test
    void deleteNotAllowedPastDays() throws Exception {
        perform(MockMvcRequestBuilders
                .delete(REST_URL + (DISH_START_ID + 24), RestaurantTestData.YAMATO_ID))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithLocation() throws Exception {
        Dish newDish = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, RestaurantTestData.BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish)))
                .andDo(print())
                .andExpect(status().isCreated());
        Dish created = DISH_MATCHER.readFromJson(action);
        int newId = created.id();
        newDish.setId(newId);
        DISH_MATCHER.assertMatch(created, newDish);
        DISH_MATCHER.assertMatch(dishRepository.get(newId, RestaurantTestData.BAVARIUS_ID).orElse(null), newDish);
    }

    @Test
    void update() throws Exception {
        Dish updated = getUpdated();
        perform(MockMvcRequestBuilders
                .put(REST_URL + DISH_START_ID, RestaurantTestData.BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());
        DISH_MATCHER.assertMatch(dishRepository.get(START_SEQ + 11, RestaurantTestData.BAVARIUS_ID).orElse(null), updated);
    }

    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, null, 50.0);
        perform(MockMvcRequestBuilders
                .post(REST_URL, RestaurantTestData.BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DISH_START_ID + 5, null, 35.0);
        perform(MockMvcRequestBuilders
                .put(REST_URL + (DISH_START_ID + 5), RestaurantTestData.BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Dish duplicate = new Dish(null, bavariusDish5.getName(), 2.0);
        perform(MockMvcRequestBuilders
                .post(REST_URL, RestaurantTestData.BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH)));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        Dish invalidDuplicate = new Dish(DISH_START_ID + 18, roofToHavenDish2.getName(), 11.0);
        perform(MockMvcRequestBuilders
                .put(REST_URL + (DISH_START_ID + 18), RestaurantTestData.ROOFTOHEAVEN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalidDuplicate)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH)));
    }
}