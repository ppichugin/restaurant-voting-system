package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.error.RestaurantNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.repository.RestaurantRepository;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kz.pichugin.restaurantvotingsystem.util.DishUtil.getDishTos;
import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_RESTAURANT_NOT_FOUND;

@UtilityClass
public final class RestaurantUtil {

    public static final Function<Integer, RestaurantNotFoundException> RESTAURANT_NOT_FOUND_FUNCTION =
            (id) -> new RestaurantNotFoundException(EXCEPTION_RESTAURANT_NOT_FOUND + id);

    @Contract("_ -> new")
    @NotNull
    public static RestaurantTo createRestaurantTo(@NotNull Restaurant restaurant) {
        return new RestaurantTo(restaurant.getId(), restaurant.getName(), getDishTos(restaurant.getDishes()));
    }

    public static List<RestaurantTo> getRestaurantTos(@NotNull Collection<Restaurant> restaurants) {
        return restaurants.stream()
                .map(RestaurantUtil::createRestaurantTo)
                .collect(Collectors.toList());
    }

    public static Restaurant getByIdOrThrow(@NotNull RestaurantRepository restaurantRepository, int id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> RESTAURANT_NOT_FOUND_FUNCTION.apply(id));
    }

    public static Restaurant getProxyByIdOrThrow(@NotNull EntityManager em, int id) {
        Restaurant restaurant;
        try (Session session = em.unwrap(Session.class)) {
            Optional<Restaurant> restaurantProxy = session.byId(Restaurant.class).loadOptional(id);
            restaurant = restaurantProxy.orElseThrow(() -> RESTAURANT_NOT_FOUND_FUNCTION.apply(id));
        }
        return restaurant;
    }
}
