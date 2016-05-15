var Underscore = require('underscore');
var Dal = require('./DAL')

module.exports = {

    // -----------------------------------
    // ------------- Queries -------------
    // -----------------------------------

    // --- Sales ---

    /*querySales: function (jsonArr, callback) {

        // create query array from the json from the client

        var queryArr = [];
        console.log(jsonArr);

        for (i = 0; i < jsonArr.length; i++) {
            var currQuery = {};

            // Go over all the posible parameters to query by, and create the matching qurey format.
            if (jsonArr[i].id != undefined) {
                if (jsonArr[i].id.length == 12 || jsonArr[i].id.length == 24) {
                    currQuery._id = ObjectID(jsonArr[i].id);
                }
                else {
                    callback("Wrong ID", null);
                    return;
                }
            }

            if (jsonArr[i].Event != undefined) {
                currQuery.Event = jsonArr[i].Event;
            }

            if (jsonArr[i].TickNum != undefined) {
                currQuery.TickNum = jsonArr[i].TickNum;
            }

            if (jsonArr[i].TickPrice != undefined) {
                currQuery.TickPrice = { $lte: jsonArr[i].TickPrice };
            }

            if (jsonArr[i].TickDesc != undefined) {
                //RegEx!!!!
                currQuery.TickDesc = new RegExp(jsonArr[i].TickDesc, 'i');
            }

            if (jsonArr[i].SellerName != undefined) {
                //RegEx!!!!
                currQuery.SellerName = new RegExp(jsonArr[i].SellerName, 'i');
            }

            if (jsonArr[i].SellerPhone != undefined) {
                currQuery.SellerPhone = jsonArr[i].SellerPhone;
            }

            if (jsonArr[i].SellerEmail != undefined) {
                //RegEx!!!
                currQuery.SellerEmail = new RegExp(jsonArr[i].SellerEmail, 'i');
            }

            if (jsonArr[i].SellerFullAddress != undefined) {
                //RegEx!!!
                currQuery.SellerFullAddress = new RegExp(jsonArr[i].SellerFullAddress, 'i');
            }

            if (!Underscore.isEmpty(currQuery)) {
                queryArr.push(currQuery);
            }
            else {
                if (Underscore.isEmpty(jsonArr[i])) {
                    queryArr.push(currQuery);
                }
            }
        }

        mongoClient.open(function (err, mongoClient) {
            if (mongoClient == null)
                console.log("No connection to internet!");

            var db = mongoClient.db(dbName);
            var results;
            console.log(queryArr);
            db.collection("Sales").find({ $or: queryArr }).toArray(function (err, retSales) {
                if (err) {
                    console.log(err);
                    mongoClient.close();
                    callback(err, results);
                }
                else {
                    results = retSales;
                    console.log(results);
                    mongoClient.close();
                    callback(null, results);
                }
            })
        });
    },


    // --- Shows ---

    queryShows: function (jsonArr, callback) {

        // create query array from the json from the client

        var queryArr = [];

        for (i = 0; i < jsonArr.length; i++) {
            var currQuery = {};

            // Go over all the posible parameters to query by, and create the matching qurey format.
            if (jsonArr[i].id != undefined) {

                if (jsonArr[i].id.length == 12 || jsonArr[i].id.length == 24) {
                    currQuery._id = ObjectID(jsonArr[i].id);
                }
                else {
                    callback("Wrong ID", null);
                    return;
                }
            }

            if (jsonArr[i].EventName != undefined) {
                currQuery.EventName = new RegExp(jsonArr[i].EventName, 'i');
            }

            if (jsonArr[i].Location != undefined) {
                currQuery.Location = new RegExp(jsonArr[i].Location, 'i');
            }

            if (jsonArr[i].Artist != undefined) {
                currQuery.Artist = new RegExp(jsonArr[i].Artist, 'i');
            }

            if (jsonArr[i].Type != undefined) {
                currQuery.Type = jsonArr[i].Type;
            }

            if (jsonArr[i].Date != undefined) {
                currQuery.Date = jsonArr[i].Date;

                if (!Underscore.isEmpty(currQuery)) {
                    queryArr.push(currQuery);
                }
                else {
                    if (Underscore.isEmpty(jsonArr[i])) {
                        queryArr.push(currQuery);
                    }
                }
            }
            else if (jsonArr[i].startDate != undefined || jsonArr[i].endDate != undefined) {
                var startDateJson = {};
                var endDateJson = {};
                startDateJson.Date = { $gte: jsonArr[i].startDate };
                endDateJson.Date = { $lte: jsonArr[i].endDate };

                var andArr = [];

                if (!Underscore.isEmpty(currQuery)) {
                    andArr.push(currQuery);
                }
                if (!Underscore.isEmpty(startDateJson)) {
                    andArr.push(startDateJson);
                }
                if (!Underscore.isEmpty(endDateJson)) {
                    andArr.push(endDateJson);
                }

                queryArr.push({ $and: andArr });
            }
            else {
                if (!Underscore.isEmpty(currQuery)) {
                    queryArr.push(currQuery);
                }
                else {
                    if (Underscore.isEmpty(jsonArr[i])) {
                        queryArr.push(currQuery);
                    }
                }
            }
        }

        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;
            console.log(queryArr);
            db.collection("Shows").find({ $or: queryArr }).toArray(function (err, retShows) {
                if (err) {
                    console.log(err);
                    mongoClient.close();
                    callback(err, results);
                }
                else {
                    results = retShows;
                    console.log(results);
                    mongoClient.close();
                    callback(null, results);
                }
            })
        });
    },

    popularEvents: function (callback) {

        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;

            db.collection("Sales").group(['Event'], {}, { "count": 0 },
                " function (curr, result) { result.count++; }", function (err, popEvents) {
                    if (err) {
                        console.log(err);
                        mongoClient.close();
                        callback(err, results);
                    }
                    else {
                        results = popEvents;
                        console.log(popEvents);
                        mongoClient.close();
                        callback(null, results);
                    };
                });
        });

    },

    queryUserByID: function (Id, callback) {

        if (Id.length != 12 && Id.length != 24) {
            callback("Wrong ID", null);
            return;
        }

        var queryJson = { _id: ObjectID(Id) };

        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;
            console.log(queryJson);
            db.collection("Users").findOne(queryJson, function (err, userDetails) {
                if (err) {
                    console.log(err);
                    mongoClient.close();
                    callback(err, results);
                }
                else {
                    results = userDetails;
                    console.log(results);
                    mongoClient.close();
                    callback(null, results);
                }
            })
        });
    },
    // -----------------------------------
    // ------------- Delete --------------
    // -----------------------------------

    // --- Sales ---

    deleteSale: function (saleIdToDelete, callback) {
        console.log('main - deleteShow');
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);

            var results;
            var collection = db.collection("Sales");

            collection.remove({ _id: ObjectID(saleIdToDelete) }, function () {

                mongoClient.close();
                console.log('after delete');
                callback(null, null);
            });
        });
    },


*/
    // -----------------------------------
    // -------------- Add ----------------
    // -----------------------------------

    // --- Sales ---    
/*
    addSale: function (saleToAdd, userId, callback) {
        console.log('main - addSale');
        console.log(saleToAdd);
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);

            var results;
            var collection = db.collection("Sales");

            var result = collection.insert({
                Event: saleToAdd.choosenEvent.value,
                TickNum: saleToAdd.choosenTickNum,
                TickPrice: saleToAdd.choosenTickPrice,
                TickDesc: saleToAdd.choosenTickDesc,
                SellerName: saleToAdd.SellerName,
                SellerPhone: saleToAdd.SellerPhone,
                SellerEmail: saleToAdd.SellerEmail,
                SellerFullAddress: saleToAdd.SellerFullAddress,
                //SellerCity: saleToAdd.SellerCity,
                //SellerAddress: saleToAdd.SellerAddress,
                Pic: saleToAdd.Pic,
                UserID: userId
            }, function (err, sale) {
                if (err) {
                    console.log('new sale error: ' + err);
                    mongoClient.close();
                    callback(err, sale);
                }
                else {
                    console.log('new sale success');
                    mongoClient.close();
                    callback(null, sale);
                }
            });
        });
    },*/
	
	addRecipe: function(recipeToAdd, callback) {
		console.log('main - recipeToAdd');
        console.log(recipeToAdd);
		Dal.saveNewRecipe(recipeToAdd, function (err, recipe)
		{
			 if (err) {
				console.log('new recipe error: ' + err);                   
				callback(err, recipe);
			}
			else {
				console.log('new recipe success');                    
				callback(null, recipe);
			}
		});		
	},

	editRecipe: function(recipeToEdit, callback) {
		console.log('main - recipeToEdit');
		console.log(recipeToEdit);
		Dal.editRecipe(recipeToEdit, function (err, recipe)
		{
			if (err) {
				console.log('edit recipe error: ' + err);
				callback(err, recipe);
			}
			else {
				console.log('edit recipe success');
				callback(null, recipe);
			}
		});
	},

	deleteRecipe: function(recipeIDToDelete, callback) {
    		console.log('main - recipeToDelete');
    		console.log(recipeIDToDelete);
    		Dal.editRecipe(recipeIDToDelete, function (err, recipe)
    		{
    			if (err) {
    				console.log('delete recipe error: ' + err);
    				callback(err, recipe);
    			}
    			else {
    				console.log('delete recipe success');
    				callback(null, recipe);
    			}
    		});
    	},

	addNewUser: function(userToAdd, callback){
		console.log('main - addNewUser');
        console.log(userToAdd);
		Dal.addNewUser(userToAdd, function(err,user)
		{
			if(err){
				console.log('new user error: ' + err);                   
				callback(err, user);
			}
			else{
				console.log('new user success');                    
				callback(null, user);
			}
		});
	},
	
	addRecipeToUserFavorits: function(favRecipe, userID, callback){
		console.log('main - addRecipeToUserFavorits');
        console.log(favRecipe);
		Dal.addRecipeToUserFavorits(recipeToAdd, userID, function(err,recipe)
		{
			if(err){
				callback(err,favRecipe);
			}
			else
			{
				callback(null, favRecipe);
			}
		});		
	},
	


/*
    // --- Shows ---

    addShow: function (showToAdd, callback) {
        console.log('main - addShow');
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);

            var results;
            var collection = db.collection("Shows");

            var result = collection.insert({
                Artist: showToAdd.Artist,
                Description: showToAdd.Description,
                Date: showToAdd.Date,
                Location: showToAdd.Location,
                Pic: showToAdd.Pic,
                EventName: showToAdd.EventName
            }, function (err, show) {
                if (err) {
                    console.log('new show error: ' + err);
                    mongoClient.close();
                    callback(err, show);
                }
                else {
                    console.log('new show success');
                    mongoClient.close();
                    callback(null, show);
                }
            });
        });
    },


    // --- Users ---

    register: function (newUser, callback) {
        console.log('main - register');
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);

            var results;
            var collection = db.collection("Users");

            var result = collection.insert({
                username: newUser.Username,
                password: newUser.Password,
                name: newUser.Name,
                role: "1",
                phoneNumber: newUser.PhoneNumber,
                email: newUser.Email,
                fullAddress: newUser.FullAddress,
                //city: newUser.City,
                //address: newUser.Address,
                Pic: newUser.Pic,
                watchList: []
            }, function (err, user) {
                if (err) {
                    console.log('new user error: ' + err);
                    mongoClient.close();
                    callback(err, user);
                }
                else {
                    console.log('new user success');
                    mongoClient.close();
                    callback(null, user);
                }
            });
        });
    },


    // -----------------------------------
    // ------------- Update --------------
    // -----------------------------------

    // --- Shows ---

    updateShow: function (showToUpdate, callback) {
        console.log('main - addShow');
        console.log(showToUpdate._id);
        //console.log(showToUpdate);
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var o_id = ObjectID(showToUpdate._id);
            var results;
            var collection = db.collection("Shows");
            var updatedShow = {
                Artist: showToUpdate.Artist,
                Description: showToUpdate.Description,
                Date: showToUpdate.Date,
                Location: showToUpdate.Location,
                Pic: showToUpdate.Pic,
                EventName: showToUpdate.EventName
            };

            collection.update({ _id: o_id }, updatedShow, function (err) {
                if (err) {
                    console.log('update show error: ' + err);
                    mongoClient.close();
                    callback(err, showToUpdate);
                }
                else {
                    updatedShow["_id"] = showToUpdate._id;
                    console.log('update show success');
                    console.log(updatedShow);
                    mongoClient.close();
                    callback(null, updatedShow);
                }
            });
        });
    },


    // --- Sales ---  

    updateSale: function (saleToUpdate, callback) {
        console.log('main - addSale');
        console.log(saleToUpdate);
        console.log("callback");
        console.log(callback);
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var o_id = ObjectID(saleToUpdate.id);
            var results;
            var collection = db.collection("Sales");
            var updatedSale = {
                Event: saleToUpdate.choosenEvent.value,
                TickNum: saleToUpdate.choosenTickNum,
                TickPrice: saleToUpdate.choosenTickPrice,
                TickDesc: saleToUpdate.choosenTickDesc,
                SellerName: saleToUpdate.SellerName,
                SellerPhone: saleToUpdate.SellerPhone,
                SellerEmail: saleToUpdate.SellerEmail,
                SellerFullAddress: saleToUpdate.SellerFullAddress,
                Pic: saleToUpdate.Pic
            };

            collection.update({ _id: o_id }, { $set: updatedSale }, function (err) {
                console.log("callback");
                console.log(callback);
                if (err) {
                    console.log('update sale error: ' + err);
                    mongoClient.close();
                    callback(err, saleToUpdate);
                }
                else {
                    updatedSale["_id"] = saleToUpdate.id;
                    console.log('update sale success');
                    console.log(updatedSale);
                    mongoClient.close();
                    callback(null, updatedSale);
                }
            });
        });
    },

    updateUser: function (userToUpdate, id, callback) {
        console.log('main - updateUser');
        console.log(userToUpdate);
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;
            var collection = db.collection("Users");
            var updatedUser = {
                password: userToUpdate.Password,
                name: userToUpdate.Name,
                phoneNumber: userToUpdate.PhoneNumber,
                email: userToUpdate.Email,
                fullAddress: userToUpdate.FullAddress,
                pic: userToUpdate.Pic
            };

            collection.update({ username: userToUpdate.Username }, { $set: updatedUser }, function (err) {
                if (err) {
                    console.log('update user error: ' + err);
                    mongoClient.close();
                    callback(err, userToUpdate);
                }
                else {
                    updatedUser["_id"] = id;
                    console.log('update user success');
                    console.log(updatedUser);
                    mongoClient.close();
                    callback(null, updatedUser);
                }
            });
        });
    },

    // -----------------------------------
    // ------------- Logins --------------
    // -----------------------------------
    getUserByUsername: function (user, callback) {
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;
            console.log(user);
            db.collection("Users").findOne(user, function (err, retUser) {
                if (err) {
                    console.log(err);
                    mongoClient.close();
                    callback(err, results);
                }
                else {
                    results = retUser;
                    console.log(retUser + "Ran!");
                    mongoClient.close();
                    callback(null, results);
                }
            })
        });

    },

    AddToWatchList: function (ShowId, user, callback) {

        console.log('add to watch list');
        console.log(ShowId);
        console.log(user);
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var o_id = ObjectID(user);
            var results;
            var collection = db.collection("Users");

            collection.update({ _id: o_id }, { $addToSet: { watchList: ShowId } }, function (err) {
                if (err) {
                    console.log('update AddToWatchList error: ' + err);
                    mongoClient.close();
                    callback(err);
                }
                else {

                    console.log('update AddToWatchList success');

                    mongoClient.close();
                    callback(null);
                }
            });
        });
    },

    // -----------------------------------
    // ------------- Apriori --------------
    // -----------------------------------

    aprioriTest: function () {

        var testArray = [['Bread', 'Milk'],
        ['Bread', 'Diaper', 'Beer', 'Eggs'],
        ['Milk', 'Diaper', 'Beer', 'Coke'],
        ['Bread', 'Milk', 'Diaper', 'Beer'],
        ['Bread', 'Milk', 'Diaper', 'Coke']];

        var AS = new Apriori.Algorithm(0.4, 0.7, false).analyze(testArray);
        var asso = AS.associationRules;
        var formatJson = JSON.stringify(asso);
        console.log(formatJson);
    },


    getWatchLists: function (callback) {
        mongoClient.open(function (err, mongoClient) {
            var db = mongoClient.db(dbName);
            var results;
            db.collection("Users").find({}, { fields: { 'watchList': 1, '_id': 0 } }).toArray(function (err, watchLists) {
                if (err) {
                    console.log(err);
                    mongoClient.close();
                    callback(err, results);
                }
                else {
                    var allLists = [];
                    watchLists.forEach(function (list) {
                        allLists.push(list.watchList);
                    });

                    results = allLists;
                    console.log(results);
                    mongoClient.close();
                    callback(null, results);
                }
            })
        });
    }*/
	
	getRecipesByIDs: function (jsonArr, callback){
		// create query array from the json from the client
		var queryArr = [];

		for (i = 0; i < jsonArr.length; i++) {
			var currQuery = {};

			// Go over all the posible parameters to query by, and create the matching qurey format.
			if (jsonArr[i].id != undefined) 
			{
				currQuery._id = ObjectID(jsonArr[i].id);	
				queryArr.push(currQuery);
			}
		}	
		
		Dal.getRecipesByQueryArray(queryArr, function(err,results)
		{
			if(err)
			{
				console.log('couldnt get recipes by ids - query, err:' + err);
				callback(err,results);
			}
			else
			{
				callback(null, results);
			}
		});
	},
	
	getAllRecipesByUser: function(userID, callback){
		console.log('main - getAllRecipesByUser');
		console.log('UserID:' + userID);
		Dal.getAllRecipesByUser(userID, function (err, results)
		{
			if(err){
				console.log('get all recipes by user error: ' + err);                   
				callback(err, results);
			}
			else
			{
				var allLists = [];
				results.forEach(function (list) {
					allLists.push(list);
				});
				
				results = allLists;
				console.log(results);				
				callback(null, results);
			}
		});
	},

	getAllUserFavoriteRecipes: function(userID, callback){
    		console.log('main - getAllUserFavoriteRecipes');
    		console.log('UserID:' + userID);
    		Dal.getAllUserFavoriteRecipes(userID, function (err, results)
    		{
    			if(err)
    			{
    				console.log('get users favorite recipes error: ' + err);
    				callback(err, results);
    			}
    			else
    			{
    				console.log('get users favorite recipes success');
    				callback(err, results);
    			}
    		});
    	},

    queryUserByID: function (Id, callback){
        var queryJson = { _id: ObjectID(Id) };

        Dal.queryUserByID(queryJson, function (err, results)
        {
           if(err)
           {
                console.log('error while retrieving userquery by id : ' + err);
                callback(err, results);
           }
           else
           {
                console.log('retrieving userquery by id success');
                callback(err, results);
           }
        });
    },

    getAllFavoriteLists: function(callback){
        console.log('main - getAllFavoriteLists');
        Dal.getAllFavoriteLists(function (err, results)
        {
            if(err)
            {
                 console.log(err);
                 callback(err, results);
            }
            else
            {
                console.log('get all users favorite recipes success');
                 callback(err, results);
            }
        });
    }
};





