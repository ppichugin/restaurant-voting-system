package kz.pichugin.restaurantvotingsystem.web.vote;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.VoteNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.model.Vote;
import kz.pichugin.restaurantvotingsystem.repository.VoteRepository;
import kz.pichugin.restaurantvotingsystem.to.VoteTo;
import kz.pichugin.restaurantvotingsystem.util.RestaurantUtil;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static kz.pichugin.restaurantvotingsystem.util.VoteUtil.getVoteTos;
import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.assureTimeLimit;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_VOTE_NOT_FOUND;

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

    @PersistenceContext
    private EntityManager em;

    @Operation(summary = "Get all votes by date of logged in user")
    @GetMapping("/by-date")
    public VoteTo get(@NotNull @AuthenticationPrincipal AuthUser authUser,
                      @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        int userId = authUser.id();
        LocalDate voteDate = (date == null) ? LocalDate.now() : date;
        log.info("get vote for user {} by date {}", userId, voteDate);
        return voteRepository.getByUserIdAndDate(userId, voteDate)
                .map(VoteUtil::createVoteTo)
                .orElseThrow(() -> new VoteNotFoundException(EXCEPTION_VOTE_NOT_FOUND + " for date=" + voteDate));
    }

    @Operation(summary = "Get vote by id of logged in user")
    @GetMapping("/{id}")
    public VoteTo getById(@NotNull @AuthenticationPrincipal AuthUser authUser, @PathVariable int id) {
        int userId = authUser.id();
        log.info("get voteId={} for userId={}", id, userId);
        Vote proxy = em.find(Vote.class, id);
        Optional<Vote> byIdAndUserId = proxy != null && proxy.getUser().id() == userId ? Optional.of(proxy) : Optional.empty();
        return VoteUtil.createVoteTo(byIdAndUserId.orElseThrow(
                () -> new VoteNotFoundException(EXCEPTION_VOTE_NOT_FOUND + ": voteId=" + id + " for userId=" + userId)));
    }

    @Operation(summary = "Get all votes of logged in user")
    @GetMapping
    public List<VoteTo> getAll(@NotNull @AuthenticationPrincipal AuthUser authUser) {
        log.info("get all votes for user {}", authUser.id());
        List<Vote> votes = voteRepository.getAllByUser(authUser.id());
        return getVoteTos(votes);
    }

    @Operation(summary = "Write vote from logged in user")
    @Transactional
    @PostMapping
    public ResponseEntity<VoteTo> create(@NotNull @AuthenticationPrincipal AuthUser authUser,
                                         @RequestParam int restaurantId) {
        log.info("Try to create vote from userId={} for restaurantId={}", authUser.id(), restaurantId);
        Restaurant restaurant = RestaurantUtil.getProxyByIdOrThrow(em, restaurantId);
        Vote newVote = new Vote(LocalDate.now(), authUser.getUser(), restaurant);
        voteRepository.saveAndFlush(newVote);
        VoteTo voteTo = VoteUtil.createVoteTo(newVote);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(voteTo.getId()).toUri();
        log.info("userId={} voted for restaurantId={}", authUser.id(), restaurantId);
        return ResponseEntity.created(uriOfNewResource).body(voteTo);
    }

    @Operation(summary = "Change vote from logged in user")
    @Transactional
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@NotNull @AuthenticationPrincipal AuthUser authUser, @RequestParam int restaurantId) {
        log.info("Try to update vote for userId={} to restaurantId={}", authUser.id(), restaurantId);
        assureTimeLimit(LocalTime.now());
        Restaurant restaurantProxy = em.find(Restaurant.class, restaurantId);
        Vote voteForToday = voteRepository.getByUserIdAndDate(authUser.id(), LocalDate.now())
                .orElseThrow(() -> new VoteNotFoundException(EXCEPTION_VOTE_NOT_FOUND));
        if (voteForToday.getSelectedRestaurant().id() == restaurantId) {
            log.info("userId={} tried to vote today for the same restaurantId={} again", authUser.id(), restaurantId);
        } else {
            log.info("userId={} changed the vote to the restaurantId={}", authUser.id(), restaurantId);
            voteForToday.setSelectedRestaurant(restaurantProxy);
            em.merge(voteForToday);
        }
    }
}
