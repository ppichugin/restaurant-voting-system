package kz.pichugin.restaurantvotingsystem.web.vote;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static kz.pichugin.restaurantvotingsystem.util.TimeUtil.getLimit;
import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.vote.VoteTestData.*;
import static org.hamcrest.Matchers.containsString;
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
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.getVoteTo(vote1)));
    }

    @Test
    @WithUserDetails(value = USER2_MAIL)
    void getWithNullableDate() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", ""))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(VoteUtil.getVoteTo(vote4)));
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
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(FILADELPHIA_ID)))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        VoteTo created = voteRepository.getByDate(ADMIN_ID, LocalDate.now())
                .map(VoteUtil::getVoteTo).orElse(null);
        VOTE_TO_MATCHER.assertMatch(created, newVote);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void createNotNew() throws Exception {
        VoteTo existingVote = VoteUtil.getVoteTo(vote3);
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurantId", String.valueOf(MOKITO_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());
        VoteTo created = voteRepository.getByDate(USER_ID, LocalDate.now())
                .map(VoteUtil::getVoteTo).orElse(null);
        VOTE_TO_MATCHER.assertMatch(created, existingVote);
    }

    @Test
    void updateBeforeLimit() throws Exception {
        VoteTo expected = getUpdatedBeforeLimit();
        TimeUtil.setLimit(LocalTime.now().plus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurantId", String.valueOf(CITYBREW_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());
        VoteTo actual = voteRepository.getByDate(USER_ID, LocalDate.now())
                .map(VoteUtil::getVoteTo).orElse(null);
        VOTE_TO_MATCHER.assertMatch(actual, expected);
    }

    @Test
    void updateAfterLimit() throws Exception {
        TimeUtil.setLimit(LocalTime.now().minus(1, ChronoUnit.MINUTES));
        perform(MockMvcRequestBuilders.put(REST_URL)
                .param("restaurantId", String.valueOf(CITYBREW_ID)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(
                        containsString(GlobalExceptionHandler.EXCEPTION_VOTE + TimeUtil.toString(getLimit()))));
    }
}