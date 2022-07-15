package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class RestaurantUtil {
    private RestaurantUtil() {
    }

    public static RestaurantTo getRestaurantTo(Restaurant rt) {
        return new RestaurantTo(rt.getId(), rt.getName(), DishUtil.getDishTos(rt.getMenu()));
    }

    public static List<RestaurantTo> getRestaurantTos(Collection<Restaurant> restaurants) {
        List<RestaurantTo> restaurantTos = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {


        }
            return restaurantTos;
    }
}
