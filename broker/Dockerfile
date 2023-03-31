FROM rabbitmq

ENV RABBITMQ_VERSION 3.8.3

RUN rabbitmq-plugins enable --offline rabbitmq_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_web_stomp
RUN rabbitmq-plugins enable --offline rabbitmq_prometheus

EXPOSE 61613
EXPOSE 15692