package kz.pichugin.restaurantvotingsystem.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
import kz.pichugin.restaurantvotingsystem.error.RestaurantNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.model.User;
import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.repository.VoteRepository;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import kz.pichugin.restaurantvotingsystem.util.VoteUtil;
import kz.pichugin.restaurantvotingsystem.web.AuthUser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "201", description = "Vote created", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
        @ApiResponse(responseCode = "404", description = "Vote not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
public class VoteController {
    protected static final String REST_URL = "/api/profile/votes";
    private final VoteRepository voteRepository;
    private final RestaurantRepository restaurantRepository;

    @Operation(summary = "Get all votes by date of logged in user")
    @GetMapping("/by-date")
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser,
                      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int userId = authUser.id();
        LocalDate voteDate = (date == null) ? LocalDate.now() : date;
        log.info("get vote for user {} by date {}", userId, voteDate);
        return voteRepository.getByDate(userId, voteDate).map(VoteUtil::getVoteTo)
                .orElseThrow(() -> new IllegalRequestDataException("Vote for date=" + voteDate + " not found"));
    }

    @Operation(summary = "Get all votes of logged in user")
    @GetMapping
    public List<VoteTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get all votes for user {}", authUser.id());
        List<Vote> votes = voteRepository.getAllByUser(authUser.id());
        return getVoteTos(votes);
    }

    @Operation(summary = "Write vote from logged in user")
    @Transactional
    @PostMapping
    public ResponseEntity<VoteTo> create(@AuthenticationPrincipal AuthUser authUser,
                                         @RequestParam int restaurantId) {
        log.info("create vote from userId={} for the restaurantId={}", authUser.id(), restaurantId);
        VoteTo voteTo = saveVote(authUser.getUser(), restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(voteTo.getRestaurantId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(voteTo);
    }

    @Operation(summary = "Change vote from logged in user")
    @Transactional
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        VoteTo voteTo = saveVote(authUser.getUser(), restaurantId);
        if (voteTo.getRestaurantId() == restaurantId) {
            log.info("userId={} tried to vote for restaurantId={} again", authUser.id(), restaurantId);
        } else {
            log.info("userId={} changed the vote for the restaurantId={}", authUser.id(), restaurantId);
        }
    }

    @NotNull
    private VoteTo saveVote(User user, int restaurantId) {
        final Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        final LocalDate today = LocalDate.now();
        final Vote voteToday = voteRepository.getByDate(user.id(), today)
                .orElse(new Vote(today, user, restaurant));
        if (!voteToday.isNew()) {
            assureTimeLimit(LocalTime.now());
        }
        final Vote created = voteRepository.save(voteToday);
        created.setSelectedRestaurant(restaurant);
        return VoteUtil.getVoteTo(created);
    }
}
