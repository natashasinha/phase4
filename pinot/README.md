
start kafka, redis, and pinot

```
cd data-demo-companion/pinot
./setup.sh
```


create data

```
cd data-demo/mockdata-daemon
../gradlew bootRunDaemon -Pspring.profiles.active=kafka
```
