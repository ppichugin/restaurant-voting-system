package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.to.DishTo;
import kz.pichugin.restaurantvotingsystem.to.NamedTo;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class DishUtil {

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
