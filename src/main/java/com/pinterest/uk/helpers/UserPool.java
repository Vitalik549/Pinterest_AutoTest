package com.pinterest.uk.helpers;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.Selenide.sleep;

public class UserPool {

    /**
     * Class that contains pool of users available for parallel test runs.
     *
     * @implNote addUsersToPool(User) - method to fill pool with users
     * @implNote getFreeAdminUser() - returns random free user and blocks it until user released by releaseAdminUser(User)
     */

    private static ConcurrentHashMap<User, Boolean> adminUserPool = new ConcurrentHashMap<>();
    private static final Logger LOGGER = Logger.getLogger(UserPool.class);

    public static final int MAX_WAITING_SECONDS = 30;

    static {
        addUsersToPool(new User("Vitalik549@gmail.com", "1111222334"));
        //addUsersToPool(new User("login", "password"));
    }

    public static User getFreeAdminUser() {
        User freeUser;
        int counter = 0;
        do {
            freeUser = getFreeUserFromPoolSet();
            sleep(500);
        } while (freeUser == null && counter++ < MAX_WAITING_SECONDS);
        return freeUser;
    }

    private static synchronized User getFreeUserFromPoolSet() {
        return adminUserPool.entrySet()
                .stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

    }

    public synchronized static void releaseAdminUser(User user) {
        if (adminUserPool.containsKey(user)) {
            adminUserPool.put(user, true);
            LOGGER.info("Next user was released in pool : " + user.getFullNaming());
        } else {
            LOGGER.info("!!!User " + user.getFullNaming() + " is not from default user pool. Please re-check!!!");
        }
    }

    public static synchronized void addUsersToPool(User... users) {
        LOGGER.info("Following users were added to UserPool for current test run: ");
        Arrays.asList(users).forEach(a -> {
                    adminUserPool.put(a, true);
                    LOGGER.info(a.email);
                }
        );
    }
}
