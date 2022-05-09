> use permalinks
switched to db permalinks
> db.createUser({user: "permalinks", pwd: "per001",roles: [ "readWrite", "dbAdmin" ]})
Successfully added user: { "user" : "permalinks", "roles" : [ "readWrite", "dbAdmin" ] }
> exit
bye


db.createUser({user: "permalinks", pwd: "per001",roles: [ { role: "userAdmin", db: "permalinks"} ]})