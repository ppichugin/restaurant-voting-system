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

import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_DISH_NOT_FOUND;
import static kz.pichugin.restaurantvotingsystem.web.dish.DishTestData.bavariusDish1;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.START_SEQ;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        perform(MockMvcRequestBuilders.get(REST_URL + DishTestData.DISH_START_ID, BAVARIUS_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.DISH_MATCHER.contentJson(bavariusDish1));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + NOT_FOUND, BAVARIUS_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(EXCEPTION_DISH_NOT_FOUND)));
    }

    @Test
    void getAllByRestaurantAndDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by-date", BAVARIUS_ID)
                .param("date", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.DISH_TO_MATCHER.contentJson(DishUtil.getDishTos(DishTestData.bavarius_menu)));
    }

    @Test
    void getAllByRestaurant() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL, YAMATO_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(DishTestData.DISH_TO_MATCHER.contentJson(DishUtil.getDishTos(DishTestData.yamato_menu_all)));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + DishTestData.DISH_START_ID, BAVARIUS_ID))
                .andExpect(status().isNoContent());
        assertFalse(dishRepository.get(START_SEQ + 11, BAVARIUS_ID).isPresent());
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + bavariusDish1.id(), CITYBREW_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(EXCEPTION_DISH_NOT_FOUND)));
        assertTrue(dishRepository.get(bavariusDish1.id(), BAVARIUS_ID).isPresent());
    }

    @Test
    void deleteRestaurantNotFound() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + bavariusDish1.id(), NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND)));
        assertTrue(dishRepository.get(bavariusDish1.id(), BAVARIUS_ID).isPresent());
    }

    @Test
    void deleteNotAllowedPastDays() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + (DishTestData.DISH_START_ID + 24), YAMATO_ID))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_PAST_DAYS_DISH)));
    }

    @Test
    void create() throws Exception {
        Dish newDish = DishTestData.getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL, BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish)))
                .andDo(print())
                .andExpect(status().isCreated());
        Dish created = DishTestData.DISH_MATCHER.readFromJson(action);
        int newId = created.id();
        newDish.setId(newId);
        DishTestData.DISH_MATCHER.assertMatch(created, newDish);
        DishTestData.DISH_MATCHER.assertMatch(dishRepository.get(newId, BAVARIUS_ID).orElse(null), newDish);
    }

    @Test
    void createRestaurantNotFound() throws Exception {
        Dish newDish = DishTestData.getNew();
        perform(MockMvcRequestBuilders.post(REST_URL, NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newDish)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND)));
    }

    @Test
    void createInvalid() throws Exception {
        Dish invalid = new Dish(null, 50);
        perform(MockMvcRequestBuilders.post(REST_URL, BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createNegativePrice() throws Exception {
        Dish invalid = new Dish("test dish", -50);
        perform(MockMvcRequestBuilders.post(REST_URL, BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("must be greater than or equal to 0")));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Dish duplicate = new Dish(DishTestData.bavariusDish5.getName(), 2);
        perform(MockMvcRequestBuilders.post(REST_URL, BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(duplicate)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH)));
    }

    @Test
    void update() throws Exception {
        Dish updated = DishTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + DishTestData.DISH_START_ID, BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andExpect(status().isNoContent());
        DishTestData.DISH_MATCHER.assertMatch(dishRepository.get(DishTestData.DISH_START_ID, BAVARIUS_ID).orElse(null), updated);
    }

    @Test
    void updateInvalid() throws Exception {
        Dish invalid = new Dish(DishTestData.DISH_START_ID + 5, null, 35);
        perform(MockMvcRequestBuilders.put(REST_URL + (DishTestData.DISH_START_ID + 5), BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNotFound() throws Exception {
        Dish invalid = DishTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + (invalid.id()), YAMATO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(EXCEPTION_DISH_NOT_FOUND)));
    }

    @Test
    void updateRestaurantNotFound() throws Exception {
        Dish dish = DishTestData.getUpdated();
        perform(MockMvcRequestBuilders.put(REST_URL + (dish.id()), NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(dish)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND)));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        Dish invalidDuplicate = new Dish(DishTestData.DISH_START_ID + 18, DishTestData.roofToHavenDish2.getName(), 11);
        perform(MockMvcRequestBuilders.put(REST_URL + (DishTestData.DISH_START_ID + 18), RestaurantTestData.ROOFTOHEAVEN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalidDuplicate)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_DISH)));
    }
}