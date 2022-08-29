package kz.pichugin.restaurantvotingsystem.web.vote;

import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.repository.VoteRepository;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import kz.pichugin.restaurantvotingsystem.util.TimeUtil;
import kz.pichugin.restaurantvotingsystem.util.VoteUtil;
import kz.pichugin.restaurantvotingsystem.web.AbstractControllerTest;
import kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static kz.pichugin.restaurantvotingsystem.util.TimeUtil.getLimit;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_TIME_LIMIT_VOTE;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.NOT_FOUND;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.vote.VoteTestData.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUserDetails(value = USER_MAIL)
class VoteControllerTest extends AbstractControllerTest {
    private static final String REST_URL = VoteController.REST_URL + '/';

    @Autowired
    private VoteRepository voteRepository;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.getVoteTos(user1Votes)));
    }

    @Test
    void getByDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", LocalDate.now().minusDays(2).toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.createVoteTo(vote1)));
    }

    @Test
    void getByDateNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", LocalDate.now().minusDays(3).toString()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_VOTE_NOT_FOUND)));
    }

    @Test
    void getById() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + vote1.id()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.createVoteTo(vote1)));
    }

    @Test
    void getByIdNotFound() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + adminVote1.id()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(GlobalExceptionHandler.EXCEPTION_VOTE_NOT_FOUND)));
    }

    @Test
    @WithUserDetails(value = USER2_MAIL)
    void getWithNullableDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.createVoteTo(vote4)));
    }

    @Test
    @WithAnonymousUser
    void getAccessDenied() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "by-date"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {
        VoteTo newVote = getNewVoteTo();
        ResultActions actions = perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(FILADELPHIA_ID)))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        VoteTo created = VOTE_TO_MATCHER.readFromJson(actions);
        int newId = created.id();
        newVote.setId(newId);
        VOTE_TO_MATCHER.assertMatch(created, newVote);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createForSameRestaurantTwice() throws Exception {
        VoteTo existingVote = VoteUtil.createVoteTo(vote3);
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(MOKITO_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        VoteTo created = voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now())
                .map(VoteUtil::createVoteTo).orElse(null);
        VOTE_TO_MATCHER.assertMatch(created, existingVote);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createDifferentRestaurantSameDay() throws Exception {
        VoteTo existingVote = VoteUtil.createVoteTo(vote3);
        VoteTo newVote = VoteUtil.createVoteTo(new Vote(vote3.getId(), vote3.getUser(), citybrew, LocalDate.now()));
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(CITYBREW_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        VoteTo foundForToday = voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now())
                .map(VoteUtil::createVoteTo).orElse(null);
        assert foundForToday != null;
        assertNotEquals(foundForToday.getRestaurantId(), newVote.getRestaurantId());
        VOTE_TO_MATCHER.assertMatch(foundForToday, existingVote);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createRestaurantNotFound() throws Exception {
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(NOT_FOUND)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(EXCEPTION_RESTAURANT_NOT_FOUND)));
    }

    @Test
    void updateBeforeDeadline() throws Exception {
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.patch(REST_URL)
                .param("restaurantId", String.valueOf(YAMATO_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());
        VoteTo actual = voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now())
                .map(VoteUtil::createVoteTo).orElse(null);
        assert actual != null;
        assertEquals(actual.getRestaurantId(), YAMATO_ID);
    }

    @Test
    void updateAfterDeadline() throws Exception {
        TimeUtil.setLimit(LocalTime.now().minus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.patch(REST_URL)
                .param("restaurantId", String.valueOf(CITYBREW_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(
                        containsString(EXCEPTION_TIME_LIMIT_VOTE + TimeUtil.toString(getLimit()))));
        VoteTo actual = voteRepository.getByUserIdAndDate(USER_ID, LocalDate.now())
                .map(VoteUtil::createVoteTo).orElse(null);
        assert actual != null;
        assertEquals(actual.getRestaurantId(), vote3.getSelectedRestaurant().getId());
    }
}