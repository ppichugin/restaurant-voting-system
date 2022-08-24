package kz.pichugin.restaurantvotingsystem.web.restaurant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kz.pichugin.restaurantvotingsystem.error.RestaurantNotFoundException;
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
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content),
        @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content),
        @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
        @ApiResponse(responseCode = "500", description = "Server error", content = @Content)})
public class RestaurantController {
    protected static final String REST_URL = "/api/restaurants";
    private final RestaurantRepository repository;

    @Operation(summary = "Get all restaurants with menu")
    @GetMapping("/with-menu")
    @Cacheable({"restaurants", "dishes"})
    public List<RestaurantTo> getAllWithMenuToday() {
        log.info("get all restaurants with menu for today");
        List<Restaurant> allByDateWithMenu = repository.getAllByDateWithMenu(LocalDate.now());
        return RestaurantUtil.getRestaurantTos(allByDateWithMenu);
    }

    @Operation(summary = "Get restaurant by id with menu")
    @GetMapping("/{id}/with-menu")
    public RestaurantTo getByIdWithMenuToday(@PathVariable int id) {
        log.info("get restaurant {} with menu today", id);
        return repository.getByIdAndDateWithMenu(id, LocalDate.now())
                .map(RestaurantUtil::createRestaurantTo)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }
}