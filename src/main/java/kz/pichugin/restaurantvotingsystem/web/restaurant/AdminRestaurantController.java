package kz.pichugin.restaurantvotingsystem.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.RestaurantException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.repository.DishRepository;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.util.RestaurantUtil;
import kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_RESTAURANT_WITH_HISTORY;

@RestController
@RequestMapping(value = AdminRestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Admin Restaurant Controller")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "201", description = "Restaurant created", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
        @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
public class AdminRestaurantController {
    protected static final String REST_URL = "/api/admin/restaurants";
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Operation(summary = "Get restaurant")
    @GetMapping("/{restaurantId}")
    public Restaurant get(@PathVariable int restaurantId) {
        log.info("get restaurant {}", restaurantId);
        return RestaurantUtil.getByIdOrThrow(restaurantRepository, restaurantId);
    }

    @Operation(summary = "Get all restaurants")
    @GetMapping
    public List<Restaurant> getAll() {
        log.info("get all restaurants");
        return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Operation(summary = "Create new restaurant")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = {"restaurants", "dishes"}, allEntries = true)
    public ResponseEntity<Restaurant> create(@NotNull @Valid @RequestBody Restaurant restaurant) {
        log.info("Try to create restaurant {}", restaurant);
        ValidationUtil.checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        log.info("Created: restaurant {}", created);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Update restaurant")
    @PutMapping(value = "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"restaurants", "dishes"}, allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant,
                       @PathVariable int restaurantId) {
        log.info("Try to update restaurantId={}", restaurantId);
        ValidationUtil.assureIdConsistent(restaurant, restaurantId);
        RestaurantUtil.getByIdOrThrow(restaurantRepository, restaurantId);
        restaurantRepository.save(restaurant);
        log.info("Updated: restaurantId={}", restaurantId);
    }

    @Operation(summary = "Delete restaurant")
    @Transactional
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Caching(evict = {
            @CacheEvict(cacheNames = "restaurants", key = "'getAllWithMenuToday'"),
            @CacheEvict(cacheNames = "restaurants", key = "#restaurantId"),
            @CacheEvict(cacheNames = "dishes", key = "'getAllByRestaurant:' + #restaurantId"),
            @CacheEvict(cacheNames = "dishes", key = "'getAllByRestaurantAndDate:' + #restaurantId + ':' + T(java.time.LocalDate).now()")
    })
    public void delete(@PathVariable int restaurantId) {
        log.info("Try to delete restaurantId={}", restaurantId);
        RestaurantUtil.getByIdOrThrow(restaurantRepository, restaurantId);
        boolean isMenuEmpty = dishRepository.getAll(restaurantId).isEmpty();
        if (!isMenuEmpty) {
            throw new RestaurantException(EXCEPTION_RESTAURANT_WITH_HISTORY);
        }
        restaurantRepository.deleteExisted(restaurantId);
        log.info("Deleted: restaurantId={}", restaurantId);
    }
}
