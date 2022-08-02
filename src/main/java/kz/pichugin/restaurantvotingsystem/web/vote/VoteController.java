package kz.pichugin.restaurantvotingsystem.web.vote;

import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
import kz.pichugin.restaurantvotingsystem.error.RestaurantNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.repository.UserRepository;
import kz.pichugin.restaurantvotingsystem.repository.VoteRepository;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import kz.pichugin.restaurantvotingsystem.util.VoteUtil;
import kz.pichugin.restaurantvotingsystem.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.util.VoteUtil.getVoteTos;
import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.assureTimeLimit;

@RestController
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Vote Controller")
public class VoteController {
    protected static final String REST_URL = "/api/profile/votes";
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @GetMapping("/by-date")
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser,
                      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int userId = authUser.id();
        LocalDate voteDate = (date == null) ? LocalDate.now() : date;
        log.info("get vote for user {} by date {}", userId, voteDate);
        return voteRepository.getByDate(userId, voteDate).map(VoteUtil::getVoteTo)
                .orElseThrow(() -> new IllegalRequestDataException("Vote for date=" + voteDate + " not found"));
    }

    @GetMapping
    public List<VoteTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get all votes for user {}", authUser.id());
        List<Vote> votes = voteRepository.getAllByUser(authUser.id());
        return getVoteTos(votes);
    }

    @Transactional
    @PostMapping
    public ResponseEntity<VoteTo> create(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        final int userId = authUser.id();
        Restaurant restaurant = getRestaurant(restaurantId);
        Vote vote = getVote(userId, restaurant);
        if (vote.isNew()) {
            log.info("vote from userId={} for the restaurantId={}", userId, restaurantId);
            Vote created = voteRepository.save(vote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(REST_URL).buildAndExpand().toUri();
            return ResponseEntity.created(uriOfNewResource).body(VoteUtil.getVoteTo(created));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Transactional
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        final int userId = authUser.id();
        Restaurant restaurant = getRestaurant(restaurantId);
        Vote vote = getVote(userId, restaurant);
        assureTimeLimit(LocalTime.now());
        if (vote.getSelectedRestaurant().id() == restaurantId) {
            log.info("Was a try to vote again by userId={} for the restaurantId={}", userId, restaurantId);
        } else {
            log.info("userId={} changed the vote for the restaurantId={}", userId, restaurantId);
            vote.setSelectedRestaurant(restaurant);
        }
    }

    private Vote getVote(int userId, Restaurant restaurant) {
        LocalDate today = LocalDate.now();
        return voteRepository.getByDate(userId, today)
                .orElse(new Vote(today, userRepository.getById(userId), restaurant));
    }

    private Restaurant getRestaurant(int restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
    }
}
