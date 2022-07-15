package kz.pichugin.restaurantvotingsystem.util;

import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.to.DishTo;

import java.util.ArrayList;
import java.util.List;

public final class DishUtil {
    private DishUtil() {
    }

    public static Dish getDish(DishTo dishTo) {
        return new Dish(dishTo.getId(), dishTo.getName(), dishTo.getPrice());
    }

    public static List<DishTo> getDishTos(List<Dish> menu) {
        List<DishTo> dishTos = new ArrayList<>();
        for (Dish dish : menu) {
            Integer id = dish.getId();
            String name = dish.getName();
            Double price = dish.getPrice();
            dishTos.add(new DishTo(id, name, price));
        }
        return dishTos;
    }
}
