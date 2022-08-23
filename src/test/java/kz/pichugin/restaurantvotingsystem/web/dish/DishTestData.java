package kz.pichugin.restaurantvotingsystem.web.dish;

import kz.pichugin.restaurantvotingsystem.model.Dish;
import kz.pichugin.restaurantvotingsystem.to.DishTo;
import kz.pichugin.restaurantvotingsystem.web.MatcherFactory;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.util.List;

import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.START_SEQ;

@UtilityClass
public class DishTestData {
    public static final int DISH_START_ID = START_SEQ + 12;
    public static final MatcherFactory.Matcher<Dish> DISH_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Dish.class, "restaurant");
    public static final MatcherFactory.Matcher<DishTo> DISH_TO_MATCHER = MatcherFactory.usingEqualsComparator(DishTo.class);
    public static final Dish bavariusDish1 = new Dish(DISH_START_ID, "Coffee", 3, null, LocalDate.now());
    public static final Dish bavariusDish2 = new Dish(DISH_START_ID + 1, "Croissant", 4, null, LocalDate.now());
    public static final Dish bavariusDish3 = new Dish(DISH_START_ID + 2, "Spring salad", 7, null, LocalDate.now());
    public static final Dish bavariusDish4 = new Dish(DISH_START_ID + 3, "Beef with cream", 25, null, LocalDate.now());
    public static final Dish bavariusDish5 = new Dish(DISH_START_ID + 4, "Hamburger", 13, null, LocalDate.now());
    public static final Dish citybrewDish1 = new Dish(DISH_START_ID + 5, "Full English Breakfast", 34, null, LocalDate.now());
    public static final Dish citybrewDish2 = new Dish(DISH_START_ID + 6, "Pizza Italia", 11, null, LocalDate.now());
    public static final Dish citybrewDish3 = new Dish(DISH_START_ID + 7, "Turkish tea", 1, null, LocalDate.now());
    public static final Dish citybrewDish4 = new Dish(DISH_START_ID + 8, "English tea", 1, null, LocalDate.now());
    public static final Dish citybrewDish5 = new Dish(DISH_START_ID + 9, "Panna Cotta", 9, null, LocalDate.now());
    public static final Dish mokitoDish1 = new Dish(DISH_START_ID + 10, "Pizza four seasons", 15, null, LocalDate.now());
    public static final Dish mokitoDish2 = new Dish(DISH_START_ID + 11, "Pizza chicken", 12, null, LocalDate.now());
    public static final Dish mokitoDish3 = new Dish(DISH_START_ID + 12, "Pizza Milano", 24, null, LocalDate.now());
    public static final Dish mokitoDish4 = new Dish(DISH_START_ID + 13, "Pizza cheesy", 31, null, LocalDate.now());
    public static final Dish filadelphiaDish1 = new Dish(DISH_START_ID + 14, "McFlurry Oreo", 26, null, LocalDate.now());
    public static final Dish filadelphiaDish2 = new Dish(DISH_START_ID + 15, "Tiramisu", 9, null, LocalDate.now());
    public static final Dish filadelphiaDish3 = new Dish(DISH_START_ID + 16, "Paella valenciana", 32, null, LocalDate.now());
    public static final Dish filadelphiaDish4 = new Dish(DISH_START_ID + 17, "Shrimp cocktail", 14, null, LocalDate.now());
    public static final Dish roofToHavenDish1 = new Dish(DISH_START_ID + 18, "Sushi", 10, null, LocalDate.now());
    public static final Dish roofToHavenDish2 = new Dish(DISH_START_ID + 19, "Seekh Kebab", 9, null, LocalDate.now());
    public static final Dish roofToHavenDish3 = new Dish(DISH_START_ID + 20, "Rib Eye", 17, null, LocalDate.now());
    public static final Dish yamatoDish1 = new Dish(DISH_START_ID + 21, "Schlenkerla", 8, null, LocalDate.now());
    public static final Dish yamatoDish2 = new Dish(DISH_START_ID + 22, "Rothaus Tannen Zapfle", 5, null, LocalDate.now());
    public static final Dish yamatoDish3 = new Dish(DISH_START_ID + 23, "Rothaus Zapfle", 5, null, LocalDate.now());
    public static final Dish yamatoDishPast1 = new Dish(DISH_START_ID + 24, "Sushi Yamato", 3, null, LocalDate.now().minusDays(1));
    public static final Dish yamatoDishPast2 = new Dish(DISH_START_ID + 25, "Tea with ice", 5, null, LocalDate.now().minusDays(1));
    public static final Dish yamatoDishPast3 = new Dish(DISH_START_ID + 26, "Waffles with cream", 2, null, LocalDate.now().minusDays(2));
    public static final List<Dish> bavarius_menu = List.of(bavariusDish5, bavariusDish4, bavariusDish3, bavariusDish2, bavariusDish1);
    public static final List<Dish> citybrew_menu = List.of(citybrewDish5, citybrewDish4, citybrewDish3, citybrewDish2, citybrewDish1);
    public static final List<Dish> mokito_menu = List.of(mokitoDish4, mokitoDish3, mokitoDish2, mokitoDish1);
    public static final List<Dish> filadelphia_menu = List.of(filadelphiaDish4, filadelphiaDish3, filadelphiaDish2, filadelphiaDish1);
    public static final List<Dish> roofToHaven_menu = List.of(roofToHavenDish3, roofToHavenDish2, roofToHavenDish1);
    public static final List<Dish> yamato_menu_today = List.of(yamatoDish3, yamatoDish2, yamatoDish1);
    public static final List<Dish> yamato_menu_all = List.of(yamatoDish1, yamatoDish2, yamatoDish3, yamatoDishPast1, yamatoDishPast2, yamatoDishPast3);

    public static Dish getNew() {
        return new Dish(null, "New Dish1", 5, null, LocalDate.now());
    }

    public static Dish getUpdated() {
        Dish updated = new Dish(bavariusDish1);
        updated.setName("Coffee updated");
        return updated;
    }
}