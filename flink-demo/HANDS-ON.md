
# Hands On

* Add a second source, replace the mapping function with a joining function


## Step 1

* Duplicate `KafkaSource<Tuple2<OrderKey, Order>> source` pulling in the `OrderEnriched` entity already included.


## Step 2

* Pull in each Kafka Source into a `DataStream`.

* `env.fromSource()` will do this, you also need to pick a WatermarkStrategy, use ` WatermarkStrategy.forBoundedOutOfOrderness` and determine
a good interval to use.


## Step 3

* Join the order data-stream to the enriched data-stream built in step #2.

* Three things to do

  * Set the where clause (what you are joining from)
  * Set the equalTo clause (what you are joining to)
  * Set the windowing 
  * apply() --> this will replace the single map function from existing code
  * sink to the sink (repeat from existing code).

## Build and Deploy

 * Nothing in the scripts change, `setup.sh` has the pieces

 * Build

   * `(cd application; gradle build shadowJar; cp ./build/libs/flink_application-all.jar ../../flink/jars)`

 * Deploy 

   * `docker exec -it flink_jobmanager sh -c "flink run -p 4 --detached /jars/flink_application-all.jar"`

## Step 4

 * consume the new topic to see if the enrcichment happened.

## Notes

* DataTypes in Flink when it comes to Kafka, are typically a Tuple2<KEY,VALUE>

* Join takes 3 datatypes the datatype of the left join, datatype of the right join, and datatype of the result, very similar to Kafka Streams aggregate.

