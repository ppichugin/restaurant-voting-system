package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static kz.pichugin.restaurantvotingsystem.util.DishUtil.getDishTos;

@UtilityClass
public final class RestaurantUtil {

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
}
