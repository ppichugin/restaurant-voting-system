package kz.pichugin.restaurantvotingsystem.repository;

import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {
    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r JOIN FETCH r.dishes d WHERE d.servingDate=:date ORDER BY r.name")
    List<Restaurant> getAllByDateWithMenu(LocalDate date);

    @EntityGraph(attributePaths = {"dishes"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT r FROM Restaurant r JOIN FETCH r.dishes d WHERE d.servingDate=:date AND r.id=:id")
    Optional<Restaurant> getByIdAndDateWithMenu(int id, LocalDate date);
}