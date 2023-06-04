package toy.bookchat.bookchat.exception;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Param https status code
 * @Description {404, 400, 403, 429, 401, 500}
 * @Param domain number
 * @Description {Etc:00,User:01, Book:02, BookReport:03, ChatRoom:04,Participant:05,Agony:06}
 * @Param error number
 * @Description increase last domain error number
 */
public enum ErrorCode {
    //Default Error Code
    BAD_REQUEST("400"),
    UNAUTHORIZED("401"),
    FORBIDDEN("403"),
    NOT_FOUND("404"),
    TOO_MANY_REQUESTS("429"),
    INTERNAL_SERVER("500"),

    //Default Domain Code
    ETC("00"),
    USER("01"),
    BOOK("02"),
    BOOK_REPORT("03"),
    CHAT_ROOM("04"),
    PARTICIPANT("05"),
    AGONY("06"),

    //BAD REQUEST
    NOT_SUPPORTED_PAGING_CONDITION(BAD_REQUEST.value + ETC.value + "00"),
    USER_ALREADY_SIGN_UP(BAD_REQUEST.value + USER.value + "00"),
    NOT_PARTICIPATED(BAD_REQUEST.value + PARTICIPANT.value + "00"),
    CHAT_ROOM_IS_FULL(BAD_REQUEST.value + CHAT_ROOM.value + "00"),
    NOT_ENOUGH_ROOM_SIZE(BAD_REQUEST.value + CHAT_ROOM.value + "01"),

    //UNAUTHORIZED
    DENIED_TOKEN(UNAUTHORIZED.value + ETC.value + "00"),
    EXPIRED_TOKEN(UNAUTHORIZED.value + ETC.value + "01"),
    EXPIRED_PUBLIC_KEY_CACHE(UNAUTHORIZED.value + ETC.value + "02"),
    ILLEGAL_STANDARD_TOKEN(UNAUTHORIZED.value + ETC.value + "03"),
    NOT_SUPPORTED_OAUTH2_PROVIDER(UNAUTHORIZED.value + ETC.value + "04"),
    NOT_VERIFIED_TOKEN(UNAUTHORIZED.value + ETC.value + "05"),
    WRONG_KEY_SPEC(UNAUTHORIZED.value + ETC.value + "06"),

    //FORBIDDEN
    NO_PERMISSION_PARTICIPANT(FORBIDDEN.value + PARTICIPANT.value + "00"),
    BLOCKED_USER_IN_CHAT_ROOM(FORBIDDEN.value + CHAT_ROOM.value + "00"),

    //NOT FOUND
    USER_NOT_FOUND(NOT_FOUND.value + USER.value + "00"),
    BOOK_NOT_FOUND(NOT_FOUND.value + BOOK.value + "00"),
    BOOK_REPORT_NOT_FOUND(NOT_FOUND.value + BOOK_REPORT.value + "00"),
    CHAT_ROOM_NOT_FOUND(NOT_FOUND.value + CHAT_ROOM.value + "00"),
    PARTICIPANT_NOT_FOUND(NOT_FOUND.value + PARTICIPANT.value + "00"),
    AGONY_NOT_FOUND(NOT_FOUND.value + AGONY.value + "00"),

    //TOO MANY REQUESTS
    RATE_OVER_LIMIT(TOO_MANY_REQUESTS.value + ETC.value + "00"),

    //INTERNAL SERVER
    IMAGE_INPUT_STREAM(INTERNAL_SERVER.value + ETC.value + "00"),
    IMAGE_UPLOAD_STORAGE(INTERNAL_SERVER.value + ETC.value + "01");

    private final String value;

    ErrorCode(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
