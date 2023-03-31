#!/bin/bash

docker stop broker && \
docker rm broker && \
docker rmi rabbit:2.0 && \
docker build -t rabbit:2.0 . && \
docker run -d --name broker \
        -p 5672:5672 \
        -p 61613:61613 \
        -p 15692:15692 \
        --restart=unless-stopped \
        -v /home/ec2-user/app/rabbitmq/rabbitmq-config:/etc/rabbitmq \
        rabbit:2.0