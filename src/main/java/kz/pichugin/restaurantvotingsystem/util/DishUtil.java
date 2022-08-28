package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.error.DishException;
import kz.pichugin.restaurantvotingsystem.error.DishNotFoundException;
import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.to.DishTo;
import kz.pichugin.restaurantvotingsystem.to.NamedTo;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_DISH_NOT_FOUND;

@UtilityClass
public final class DishUtil {

    public static final BiFunction<Integer, Integer, DishException> DISH_EXCEPTION_NOT_FOUND_FUNCTION =
            (dishId, restaurantId) -> new DishNotFoundException(EXCEPTION_DISH_NOT_FOUND + ": DishId=" + dishId + " at RestaurantId=" + restaurantId);

    @NotNull
    @Contract("_ -> new")
    public static Dish getDish(@NotNull DishTo dishTo) {
        return new Dish(dishTo.getId(), dishTo.getName(), dishTo.getPrice());
    }

    @NotNull
    public static DishTo createDishTo(@NotNull Dish dish) {
        return new DishTo(dish.getId(), dish.getName(), dish.getPrice());
    }

    @NotNull
    public static List<DishTo> getDishTos(@NotNull List<Dish> dishes) {
        return dishes.stream()
                .map(DishUtil::createDishTo)
                .sorted(Comparator.comparing(NamedTo::getName))
                .collect(Collectors.toList());
    }
}
