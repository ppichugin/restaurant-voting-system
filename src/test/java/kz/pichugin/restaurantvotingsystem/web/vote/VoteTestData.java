package kz.pichugin.restaurantvotingsystem.web.vote;

import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import kz.pichugin.restaurantvotingsystem.web.MatcherFactory;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.restaurant.RestaurantTestData.*;
import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.*;

@UtilityClass
public class VoteTestData {
    private static int startWith = 39;
    public static MatcherFactory.Matcher<VoteTo> VOTE_TO_MATCHER = MatcherFactory.usingEqualsComparator(VoteTo.class);
    public static final Vote vote1 = new Vote(START_SEQ + incr(), user1, yamato, LocalDate.now().minusDays(2));
    public static final Vote vote2 = new Vote(START_SEQ + incr(), user1, mokito, LocalDate.now().minusDays(1));
    public static final Vote vote3 = new Vote(START_SEQ + incr(), user1, mokito, LocalDate.now());
    public static final Vote adminVote1 = new Vote(START_SEQ + incr(), admin, bavarius, LocalDate.now().minusDays(2));
    public static final Vote adminVote2 = new Vote(START_SEQ + incr(), admin, bavarius, LocalDate.now().minusDays(1));
    public static final Vote vote4 = new Vote(START_SEQ + incr(), user2, bavarius, LocalDate.now());
    public static final Vote vote5 = new Vote(START_SEQ + incr(), user3, roofToHeaven, LocalDate.now().minusDays(2));
    public static final Vote vote6 = new Vote(START_SEQ + incr(), user4, citybrew, LocalDate.now());
    public static final List<Vote> user1Votes = List.of(vote3, vote2, vote1);

    public static VoteTo getNewVoteTo() {
        return new VoteTo(LocalDate.now(), FILADELPHIA_ID);
    }

    public static VoteTo getUpdatedBeforeLimit() {
        LocalDate voteDate = LocalDate.from(LocalDate.now().atTime(10, 30));
        return new VoteTo(voteDate, CITYBREW_ID);
    }

    private static int incr() {
        return startWith++;
    }
}
