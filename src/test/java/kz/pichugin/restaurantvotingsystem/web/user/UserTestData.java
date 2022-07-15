package kz.pichugin.restaurantvotingsystem.web.user;

import kz.pichugin.restaurantvotingsystem.model.Role;
import kz.pichugin.restaurantvotingsystem.model.User;
import kz.pichugin.restaurantvotingsystem.util.JsonUtil;
import kz.pichugin.restaurantvotingsystem.web.MatcherFactory;

import java.util.Collections;
import java.util.Date;

public class UserTestData {
    public static final MatcherFactory.Matcher<User> USER_MATCHER = MatcherFactory
            .usingIgnoringFieldsComparator(User.class, "registered", "password");
    public static final int START_SEQ = 100000;

    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = START_SEQ - 1;
    public static final String USER_MAIL = "user1@yandex.ru";
    public static final String ADMIN_MAIL = "admin@gmail.com";

    public static final User user1 = new User(USER_ID, "User1", USER_MAIL, "password", Role.USER);
    public static final User admin = new User(ADMIN_ID, "Admin", ADMIN_MAIL, "admin", Role.ADMIN, Role.USER);
    public static final User user2 = new User(START_SEQ + 2, "User2", "user2@yandex.ru", "password", Role.USER);
    public static final User user3 = new User(START_SEQ + 3, "User3", "user3@yandex.ru", "password", Role.USER);
    public static final User user4 = new User(START_SEQ + 4, "User4", "user4@yandex.ru", "password", Role.USER);

    public static User getNew() {
        return new User(null, "New", "new@gmail.com", "newPass", false, new Date(), Collections.singleton(Role.USER));
    }

    public static User getUpdated() {
        return new User(USER_ID, "UpdatedName", USER_MAIL, "newPass", false, new Date(), Collections.singleton(Role.ADMIN));
    }

    public static String jsonWithPassword(User user, String passw) {
        return JsonUtil.writeAdditionProps(user, "password", passw);
    }
}