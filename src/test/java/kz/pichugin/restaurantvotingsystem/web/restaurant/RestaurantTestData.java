package kz.pichugin.restaurantvotingsystem.web.restaurant;

import kz.pichugin.restaurantvotingsystem.model.Restaurant;
import kz.pichugin.restaurantvotingsystem.to.RestaurantTo;
import kz.pichugin.restaurantvotingsystem.web.MatcherFactory;
import lombok.experimental.UtilityClass;

import static kz.pichugin.restaurantvotingsystem.web.user.UserTestData.START_SEQ;

@UtilityClass
public class RestaurantTestData {
    public static final MatcherFactory.Matcher<Restaurant> REST_MATCHER = MatcherFactory.usingEqualsComparator(Restaurant.class);
    public static final MatcherFactory.Matcher<RestaurantTo> REST_TO_MATCHER = MatcherFactory.usingEqualsComparator(RestaurantTo.class);
    public static final int BAVARIUS_ID = START_SEQ + 5;
    public static final int CITYBREW_ID = START_SEQ + 6;
    public static final int MOKITO_ID = START_SEQ + 7;
    public static final int FILADELPHIA_ID = START_SEQ + 8;
    public static final int ROOFTOHEAVEN_ID = START_SEQ + 9;
    public static final int YAMATO_ID = START_SEQ + 10;
    public static final int DUMMY_ID = START_SEQ + 11;
    public static final int NOT_FOUND = START_SEQ + 1000;
    public static final Restaurant bavarius = new Restaurant(BAVARIUS_ID, "Bavarius");
    public static final Restaurant citybrew = new Restaurant(CITYBREW_ID, "Citybrew");
    public static final Restaurant mokito = new Restaurant(MOKITO_ID, "Mokito");
    public static final Restaurant filadelphia = new Restaurant(FILADELPHIA_ID, "Filadelphia");
    public static final Restaurant roofToHeaven = new Restaurant(ROOFTOHEAVEN_ID, "Roof to Heaven");
    public static final Restaurant yamato = new Restaurant(YAMATO_ID, "Yamato");
    public static final Restaurant dummyWithoutMenu = new Restaurant(DUMMY_ID, "Dummy");

    public static Restaurant getNew() {
        return new Restaurant(null, "New restaurant");
    }

    public static Restaurant getUpdated() {
        Restaurant updated = new Restaurant(yamato);
        updated.setName("Yamato updated");
        return updated;
    }
}
