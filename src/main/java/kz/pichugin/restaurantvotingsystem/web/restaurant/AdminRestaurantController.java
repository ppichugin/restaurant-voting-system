package kz.pichugin.restaurantvotingsystem.web.restaurant;

import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
import kz.pichugin.restaurantvotingsystem.error.RestaurantNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.repository.DishRepository;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.util.validation.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
@CacheConfig(cacheNames = "restaurants")
@Tag(name = "Admin Restaurant Controller")
public class AdminRestaurantController {
    protected static final String REST_URL = "/api/admin/restaurants";
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @GetMapping("/{restaurantId}")
    public Restaurant get(@PathVariable int restaurantId) {
        log.info("get restaurant {}", restaurantId);
        return restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new RestaurantNotFoundException(restaurantId));
    }

    @GetMapping
    public List<Restaurant> getAll() {
        log.info("get all restaurants");
        return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(allEntries = true)
    public ResponseEntity<Restaurant> create(@Valid @RequestBody Restaurant restaurant) {
        log.info("create restaurant {}", restaurant.toString());
        ValidationUtil.checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void update(@Valid @RequestBody Restaurant restaurant,
                       @PathVariable int restaurantId) {
        log.info("update restaurantId={}", restaurantId);
        restaurantRepository.findById(restaurantId).orElseThrow(
                () -> new RestaurantNotFoundException(restaurantId));
        ValidationUtil.assureIdConsistent(restaurant, restaurantId);
        restaurantRepository.save(restaurant);
    }

    @Transactional
    @DeleteMapping("/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = {"restaurants"}, allEntries = true)
    public void delete(@PathVariable int restaurantId) {
        log.info("delete restaurantId={}", restaurantId);
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        boolean isMenuEmpty = dishRepository.getAll(restaurantId).isEmpty();
        if (!isMenuEmpty) {
            throw new IllegalRequestDataException(EXCEPTION_RESTAURANT_WITH_HISTORY);
        }
        log.info("restaurantId={} deleted", restaurantId);
        restaurantRepository.deleteExisted(restaurantId);
    }
}
