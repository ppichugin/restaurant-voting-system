package kz.pichugin.restaurantvotingsystem.web.dish;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.DishException;
import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.repository.DishRepository;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.to.DishTo;
import kz.pichugin.restaurantvotingsystem.util.DishUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.assureIdConsistent;
import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.checkNew;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_DISH_NOT_FOUND;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_PAST_DAYS_DISH;

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "dishes")
@Tag(name = "Admin Dish Controller")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "201", description = "Dish created", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
        @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
public class AdminDishController {
    private static final BiFunction<Integer, Integer, DishException> DISH_EXCEPTION_NOT_FOUND_FUNCTION =
            (dishId, restaurantId) -> new DishException(EXCEPTION_DISH_NOT_FOUND + ": DishId=" + dishId + " at RestaurantId=" + restaurantId);
    protected static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Operation(summary = "Get dish by id of restaurant")
    @GetMapping("/{dishId}")
    public DishTo get(@PathVariable int dishId,
                      @PathVariable int restaurantId) {
        log.info("get dish {} for restaurant {}", dishId, restaurantId);
        Optional<Dish> dish = dishRepository.get(dishId, restaurantId);
        return DishUtil.createDishTo(dish.orElseThrow(
                () -> DISH_EXCEPTION_NOT_FOUND_FUNCTION.apply(dishId, restaurantId)));
    }

    @Operation(summary = "Get all dishes of the restaurant by date")
    @GetMapping("/by-date")
    @Cacheable(key = "'getAllByRestaurantAndDate:' + #restaurantId + ':' + #date",
            condition = "#date == null OR #date == T(java.time.LocalDate).now()", unless = "#result.empty")
    public List<DishTo> getAllByRestaurantAndDate(@PathVariable int restaurantId,
                                                  @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get all dishes for restaurant {} by date {}", restaurantId, date);
        List<Dish> allByDate = dishRepository.getAllByDate(restaurantId, date == null ? LocalDate.now() : date);
        return DishUtil.getDishTos(allByDate);
    }

    @Operation(summary = "Get all dishes of the restaurant")
    @GetMapping
    @Cacheable(key = "'getAllByRestaurant:' + #restaurantId", unless = "#result==null")
    public List<DishTo> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("get all dishes for restaurant {}", restaurantId);
        List<Dish> all = dishRepository.getAll(restaurantId);
        return DishUtil.getDishTos(all);
    }

    @Operation(summary = "Create dish at the restaurant")
    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(cacheNames = {"restaurants", "dishes"}, allEntries = true)
    public ResponseEntity<DishTo> create(@Valid @RequestBody DishTo dishTo,
                                         @PathVariable int restaurantId) {
        log.info("Try to add dish {} to restaurant {}", dishTo, restaurantId);
        checkNew(dishTo);
        Dish dish = DishUtil.getDish(dishTo);
        dish.setRestaurant(restaurantRepository.getById(restaurantId));
        dish.setServingDate(LocalDate.now());
        Dish created = dishRepository.save(dish);
        log.info("Added: dish {} for restaurantId={}", created, restaurantId);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(DishUtil.createDishTo(created));
    }

    @Operation(summary = "Update dish at restaurant")
    @Transactional
    @PutMapping(value = "/{dishId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"restaurants", "dishes"}, allEntries = true)
    public void update(@Valid @RequestBody DishTo dishTo,
                       @PathVariable int dishId,
                       @PathVariable int restaurantId) {
        log.info("Try to update dish {} for restaurant {}", dishId, restaurantId);
        assureIdConsistent(dishTo, dishId);
        if (dishRepository.existsByIdAndRestaurantId(dishId, restaurantId)) {
            Dish dish = DishUtil.getDish(dishTo);
            dish.setRestaurant(restaurantRepository.getById(restaurantId));
            dish.setServingDate(LocalDate.now());
            dishRepository.save(dish);
            log.info("Updated: dishId={} for restaurantId={}", dishId, restaurantId);
        } else {
            throw DISH_EXCEPTION_NOT_FOUND_FUNCTION.apply(dishId, restaurantId);
        }
    }

    @Operation(summary = "Delete dish at restaurant")
    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "dishes", allEntries = true)
    @Caching(evict = {
            @CacheEvict(cacheNames = "dishes", key = "'getAllByRestaurant:' + #restaurantId"),
            @CacheEvict(cacheNames = "dishes", key = "'getAllByRestaurantAndDate:' + #restaurantId + ':' + T(java.time.LocalDate).now()"),
            @CacheEvict(cacheNames = "restaurants", key = "'getAllWithMenuToday'"),
            @CacheEvict(cacheNames = "restaurants", key = "#restaurantId")
    })
    public void delete(@PathVariable int dishId,
                       @PathVariable int restaurantId) {
        log.info("Try to delete dish {} for restaurant {}", dishId, restaurantId);
        if (dishRepository.existsByIdAndRestaurantId(dishId, restaurantId)) {
            Dish dish = dishRepository.getById(dishId);
            if (!dish.getServingDate().equals(LocalDate.now())) {
                throw new DishException(EXCEPTION_PAST_DAYS_DISH);
            }
            dishRepository.deleteExisted(dishId);
            log.info("Deleted: dishId={} for restaurantId={}", dishId, restaurantId);
        } else {
            throw DISH_EXCEPTION_NOT_FOUND_FUNCTION.apply(dishId, restaurantId);
        }
    }
}
