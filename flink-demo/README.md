
1. build.sh -- to install the datagen connectory in the kafka-1/connector-plugins directory

2. up.sh -- to start kafka-1 and flink 

3. setup.sh 

   a. copy the datagen avro file into the connector data directory

   b. create the two kafka topics 
  
      * datagen.orders - created by the datagen plugin, the input for the flink application
      * purchase-orders - the output of the flink application

   c. Create the Datagen plugin so demo orders are created into the `datagen.orders` topic.

   d. build the Flink application

      * creates as a shadowJar (not my first choice, but easiest for demos)

      * copy jar to directory accessible by the jobmanager containers

   e. start the flink application
 
      * set parallelization to 4 -- the same number as the partitions on the topic
   
4. Goto http://localhost:48081/ and check out Flink

5. consume `datagen.orders`

   * inspect topic to verify there is no pricing
 
   * `./consume.sh datagen.orders`
   
7. consume `purchase-orders`

   * inspect topic to verify pricing has been done

   * `./consume.sh purchase-orders`
