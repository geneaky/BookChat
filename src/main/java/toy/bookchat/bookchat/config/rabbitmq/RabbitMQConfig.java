package toy.bookchat.bookchat.config.rabbitmq;

import static toy.bookchat.bookchat.config.rabbitmq.RabbitMQProperties.CACHE_CLEAR_EXCHANGE_NAME;
import static toy.bookchat.bookchat.config.rabbitmq.RabbitMQProperties.CACHE_CLEAR_QUEUE_NAME;
import static toy.bookchat.bookchat.config.rabbitmq.RabbitMQProperties.CACHE_CLEAR_ROUTING_KEY;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toy.bookchat.bookchat.config.websocket.ExternalBrokerProperties;

@Configuration
public class RabbitMQConfig {

    private final ExternalBrokerProperties externalBrokerProperties;

    public RabbitMQConfig(ExternalBrokerProperties externalBrokerProperties) {
        this.externalBrokerProperties = externalBrokerProperties;
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(CACHE_CLEAR_EXCHANGE_NAME.getValue());
    }

    @Bean
    Queue queue() {
        return new Queue(CACHE_CLEAR_QUEUE_NAME.getValue());
    }

    @Bean
    Binding binding(TopicExchange exchange, Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(CACHE_CLEAR_ROUTING_KEY.getValue());
    }

    @Bean
    ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setVirtualHost(externalBrokerProperties.getVirtualHost());
        connectionFactory.setHost(externalBrokerProperties.getHost());
        connectionFactory.setPort(externalBrokerProperties.getAmqpPort());
        connectionFactory.setUsername(externalBrokerProperties.getLogin());
        connectionFactory.setPassword(externalBrokerProperties.getPasscode());
        return connectionFactory;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
        MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setChannelTransacted(true);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
