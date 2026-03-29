package hu.progmasters.dailybugle.domain;

import lombok.Getter;

@Getter
public enum Category {

    POLITICS("politics"),
    SCIENCE("science"),
    TECHNOLOGY("technology"),
    WORLD("world"),
    SPORTS("sports"),
    CULTURE("culture"),
    ENTERTAINMENT("entertainment"),
    HEALTH("health"),
    MUSIC("music"),
    SPIDER_MAN("spider-man"),
    CRIME("crime"),
    BEAUTY("beauty"),
    FASHION("fashion"),
    FOOD("food"),
    TRAVEL("travel"),
    BUSINESS("business"),
    EDUCATION("education"),
    ENVIRONMENT("environment"),
    HORROR("horror"),
    HOROSCOPE("horoscope"),
    OTHER("other");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

}
