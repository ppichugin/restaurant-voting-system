package kz.pichugin.restaurantvotingsystem.web.dish;

import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
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

@RestController
@RequestMapping(value = AdminDishController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@CacheConfig(cacheNames = "dishes")
@Tag(name = "Admin Dish Controller")
public class AdminDishController {
    protected static final String REST_URL = "/api/admin/restaurants/{restaurantId}/dishes";
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @GetMapping("/{dishId}")
    public Dish get(@PathVariable int dishId,
                    @PathVariable int restaurantId) {
        log.info("get dish {} for restaurant {}", dishId, restaurantId);
        return dishRepository.checkRelation(dishId, restaurantId);
    }

    @GetMapping("/by-date")
    @Cacheable("dishes")
    public List<DishTo> getAllByRestaurantAndDate(@PathVariable int restaurantId,
                                                  @RequestParam @Nullable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("get all dishes for restaurant {} by date {}", restaurantId, date);
        List<Dish> allByDate = dishRepository.getAllByDate(restaurantId, date == null ? LocalDate.now() : date);
        return DishUtil.getDishTos(allByDate);
    }

    @GetMapping
    @Cacheable("dishes")
    public List<Dish> getAllByRestaurant(@PathVariable int restaurantId) {
        log.info("get all dishes for restaurant {}", restaurantId);
        return dishRepository.getAll(restaurantId);
    }

    @Transactional
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    public ResponseEntity<Dish> create(@Valid @RequestBody DishTo dishTo,
                                       @PathVariable int restaurantId) {
        log.info("add dish {} to restaurant {}", dishTo, restaurantId);
        checkNew(dishTo);
        Dish dish = DishUtil.getDish(dishTo);
        dish.setRestaurant(restaurantRepository.getById(restaurantId));
        dish.setAddDate(LocalDate.now());
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

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
        dish.setAddDate(LocalDate.now());
        dishRepository.save(dish);
    }

    @DeleteMapping("/{dishId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void delete(@PathVariable int dishId,
                       @PathVariable int restaurantId) {
        log.info("delete dish {} for restaurant {}", dishId, restaurantId);
        Dish dish = dishRepository.checkRelation(dishId, restaurantId);
        if (!dish.getAddDate().equals(LocalDate.now())) {
            throw new IllegalRequestDataException("Can not delete food for the past days");
        }
        dishRepository.deleteExisted(dishId);
    }
}
