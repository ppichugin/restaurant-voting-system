package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.to.NamedTo;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.util.DishUtil.getDishTos;

@UtilityClass
public final class RestaurantUtil {

    public static RestaurantTo getRestaurantTo(Restaurant rt) {
        return new RestaurantTo(rt.getId(), rt.getName(), getDishTos(rt.getDishes()));
    }

    public static List<RestaurantTo> getRestaurantTos(Collection<Restaurant> restaurants) {
        List<RestaurantTo> restaurantTos = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            Integer id = restaurant.getId();
            String name = restaurant.getName();
            List<Dish> menu = restaurant.getDishes();
            restaurantTos.add(new RestaurantTo(id, name, getDishTos(menu)));
        }
        restaurantTos.sort(Comparator.comparing(NamedTo::getName));
        return restaurantTos;
    }
}
