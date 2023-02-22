package toy.bookchat.bookchat.config.rabbitmq;

import lombok.Getter;

@Getter
public enum RabbitMQProperties {

    CACHE_CLEAR_EXCHANGE_NAME("cache.exchange"),
    CACHE_CLEAR_QUEUE_NAME("cache.queue"),
    CACHE_CLEAR_ROUTING_KEY("cache.clear.#");

    private final String value;

    RabbitMQProperties(String value) {
        this.value = value;
    }
}
