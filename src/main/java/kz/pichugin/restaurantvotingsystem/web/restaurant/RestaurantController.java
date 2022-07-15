package kz.pichugin.restaurantvotingsystem.web.restaurant;

import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.IllegalRequestDataException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;
import kz.pichugin.restaurantvotingsystem.util.RestaurantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@AllArgsConstructor
@Tag(name = "Restaurant Controller")
public class RestaurantController {

    static final String REST_URL = "/api/restaurants";

    private final RestaurantRepository repository;

    @GetMapping("/with-menu")
    @Cacheable("restaurants")
    public List<RestaurantTo> getAllWithMenuToday() {
        log.info("get all restaurants with menu today");
        List<Restaurant> allByDateWithMenu = repository.getAllByDateWithMenu(LocalDate.now());
        return RestaurantUtil.getRestaurantTos(allByDateWithMenu);
    }

    @GetMapping("/{id}/with-menu")
    public RestaurantTo getByIdWithMenuToday(@PathVariable int id) {
        log.info("get restaurant {} with menu today", id);
        return repository.getByIdAndDateWithMenu(id, LocalDate.now()).map(RestaurantUtil::getRestaurantTo).orElseThrow(
                () -> new IllegalRequestDataException("Restaurant with id=" + id + " not found"));
    }
}