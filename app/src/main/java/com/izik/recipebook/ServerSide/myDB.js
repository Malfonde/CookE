var MongoClient = require('mongodb').MongoClient;

function openDatabase(callback) {
    // Connect to the db
    MongoClient.connect("mongodb://127.0.0.1:27017/CookE", function (err, db) {
        if (!err) {
            console.log("We are connected");
            return callback(null, db);
        }
        else {
            return callback(err, null);
        }
    });
}

exports.openDatabase = openDatabase;