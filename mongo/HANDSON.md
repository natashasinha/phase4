
* Install MongoSH

https://www.mongodb.com/try/download/shell

* Start Mongo

docker compose up -d


* Connect Shell

./bin/mongosh -u root -p mongo


* Explore Commands

** pick a database (will create it on first need)

use db;


** determine a collection (e.g. 'students')

** insert a document 

Use any JSON you would like to use, change to a different collection; explore.

```
db.students.insertOne({})
```

** find documents

```
db.students.find()
```

** update documents

*** by ID

```
db.students.updateMany({ _id: ObjectId('6621b480084103a08d07efe6') }, { $set: { "email" : "foo@bar"}})
```

*** by Other Fields




