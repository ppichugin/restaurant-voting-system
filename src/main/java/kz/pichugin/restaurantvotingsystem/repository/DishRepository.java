package kz.pichugin.restaurantvotingsystem.repository;

import kz.pichugin.restaurantvotingsystem.error.DishException;
import kz.pichugin.restaurantvotingsystem.model.Dish;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static kz.pichugin.restaurantvotingsystem.web.GlobalExceptionHandler.EXCEPTION_DISH_NOT_FOUND;

@Transactional(readOnly = true)
public interface DishRepository extends BaseRepository<Dish> {

    @Query("SELECT d FROM Dish d WHERE d.id=?1 AND d.restaurant.id=?2")
    Optional<Dish> get(int id, int restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=?1 ORDER BY d.id")
    List<Dish> getAll(int restaurantId);

    @Query("SELECT d FROM Dish d WHERE d.restaurant.id=?1 AND d.servingDate=?2 ORDER BY d.id DESC")
    List<Dish> getAllByDate(int restaurantId, LocalDate date);

    default Dish checkRelation(int id, int restaurantId) {
        return get(id, restaurantId)
                .orElseThrow(() ->
                        new DishException(EXCEPTION_DISH_NOT_FOUND + ": DishId=" + id + " at RestaurantId=" + restaurantId));
    }
}
