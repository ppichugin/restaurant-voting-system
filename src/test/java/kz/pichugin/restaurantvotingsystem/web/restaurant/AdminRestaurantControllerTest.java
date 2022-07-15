package kz.pichugin.restaurantvotingsystem.web.restaurant;

import kz.pichugin.restaurantvotingsystem.model.NamedEntity;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.util.JsonUtil;
import kz.pichugin.restaurantvotingsystem.web.AbstractControllerTest;
import kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler;
import kz.pichugin.restaurantvotingsystem.web.user.UserTestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = UserTestData.ADMIN_MAIL)
class AdminRestaurantControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestaurantController.REST_URL + '/';

    @Autowired
    RestaurantRepository restaurantRepository;

    @Test
    void get() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL + CITYBREW_ID))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_MATCHER.contentJson(citybrew));
    }

    @Test
    void getNotFound() throws Exception {
        perform(MockMvcRequestBuilders
                .get(REST_URL + NOT_FOUND))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAll() throws Exception {
        List<Restaurant> restaurants = new ArrayList<>(List.of(bavarius, citybrew, mokito, filadelphia, roofToHeaven, yamato));
        restaurants.sort(Comparator.comparing(NamedEntity::getName));
        perform(MockMvcRequestBuilders
                .get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(REST_MATCHER.contentJson(
                        restaurants));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders
                .delete(REST_URL + ROOFTOHEAVEN_ID))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteNotFound() throws Exception {
        perform(MockMvcRequestBuilders
                .delete(REST_URL + NOT_FOUND))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithLocation() throws Exception {
        Restaurant newRestaurant = getNew();
        ResultActions action = perform(MockMvcRequestBuilders
                .post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newRestaurant)))
                .andDo(print())
                .andExpect(status().isCreated());
        Restaurant created = REST_MATCHER.readFromJson(action);
        int newId = created.id();
        newRestaurant.setId(newId);
        REST_MATCHER.assertMatch(created, newRestaurant);
        REST_MATCHER.assertMatch(restaurantRepository.getById(newId), newRestaurant);
    }

    @Test
    void update() throws Exception {
        Restaurant updated = getUpdated();
        perform(MockMvcRequestBuilders
                .put(REST_URL + YAMATO_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        REST_MATCHER.assertMatch(restaurantRepository.getById(YAMATO_ID), getUpdated());
    }

    @Test
    void createInvalid() throws Exception {
        Restaurant invalid = new Restaurant(null, null);
        perform(MockMvcRequestBuilders
                .post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateInvalid() throws Exception {
        Restaurant invalid = new Restaurant(BAVARIUS_ID, null);
        perform(MockMvcRequestBuilders.put(REST_URL + BAVARIUS_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void createDuplicate() throws Exception {
        Restaurant invalid = new Restaurant(null, citybrew.getName());
        perform(MockMvcRequestBuilders.post(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(
                        containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_RESTAURANT)));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void updateDuplicate() throws Exception {
        Restaurant invalidDuplicate = new Restaurant(CITYBREW_ID, mokito.getName());
        perform(MockMvcRequestBuilders.put(REST_URL + CITYBREW_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(invalidDuplicate)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(
                        containsString(GlobalExceptionHandler.EXCEPTION_DUPLICATE_RESTAURANT)));
    }
}
