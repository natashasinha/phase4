
#
# start up a kafka instance
# 

```
cd data-demo/kafka/local/kafka-1

fix docker-compose.yml (...)

docker compose up -d 
```

```
cd data-demo/mockdata-daemon
../gradlew bootRunDaemon -Pspring.profiles.active=kafka
```

```
cd data-demo-companion/pinot
./setup.sh
```

