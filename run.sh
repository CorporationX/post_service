docker-compose up -d
docker run -d --rm -p 9000:9000 \
 -e KAFKA_BROKERCONNECT=<host:port,host:port> \
 -e SERVER_SERVLET_CONTEXTPATH="/" \
 obsidiandynamics/kafdrop