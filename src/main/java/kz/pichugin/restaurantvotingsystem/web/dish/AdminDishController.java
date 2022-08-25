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

import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.assureIdConsistent;
import static kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil.checkNew;
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
    protected static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Operation(summary = "Get dish by id of restaurant")
    @GetMapping("/{dishId}")
//    @Cacheable
    public Dish get(@PathVariable int dishId,
                    @PathVariable int restaurantId) {
        log.info("get dish {} for restaurant {}", dishId, restaurantId);
        return dishRepository.checkRelation(dishId, restaurantId);
    }

    @Operation(summary = "Get all dishes of the restaurant by date")
    @GetMapping("/by-date")
//    @Cacheable({"dishes", "restaurants"})
    public List<DishTo> getAllByRestaurantAndDate(@PathVariable int restaurantId,
                                                  @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get all dishes for restaurant {} by date {}", restaurantId, date);
        List<Dish> allByDate = dishRepository.getAllByDate(restaurantId, date == null ? LocalDate.now() : date);
        return DishUtil.getDishTos(allByDate);
    }

    @Operation(summary = "Get all dishes of the restaurant")
    @GetMapping
//    @Cacheable
    public List<Dish> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("get all dishes for restaurant {}", restaurantId);
        return dishRepository.getAll(restaurantId);
    }

    @Operation(summary = "Create dish at the restaurant")
    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    public ResponseEntity<Dish> create(@Valid @RequestBody DishTo dishTo,
                                       @PathVariable int restaurantId) {
        log.info("add dish {} to restaurant {}", dishTo, restaurantId);
        checkNew(dishTo);
        Dish dish = DishUtil.getDish(dishTo);
        dish.setRestaurant(restaurantRepository.getById(restaurantId));
        dish.setServingDate(LocalDate.now());
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Operation(summary = "Update dish at restaurant")
    @Transactional
    @PutMapping(value = "/{dishId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void update(@Valid @RequestBody DishTo dishTo,
                       @PathVariable int dishId,
                       @PathVariable int restaurantId) {
        log.info("update dish {} for restaurant {}", dishId, restaurantId);
        assureIdConsistent(dishTo, dishId);
        dishRepository.checkRelation(dishId, restaurantId);
        Dish dish = DishUtil.getDish(dishTo);
        dish.setRestaurant(restaurantRepository.getById(restaurantId));
        dish.setServingDate(LocalDate.now());
        dishRepository.save(dish);
    }

    @Operation(summary = "Delete dish at restaurant")
    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int dishId,
                       @PathVariable int restaurantId) {
        log.info("delete dish {} for restaurant {}", dishId, restaurantId);
        Dish dish = dishRepository.checkRelation(dishId, restaurantId);
        if (!dish.getServingDate().equals(LocalDate.now())) {
            throw new DishException(EXCEPTION_PAST_DAYS_DISH);
        }
        dishRepository.deleteExisted(dishId);
    }
}
