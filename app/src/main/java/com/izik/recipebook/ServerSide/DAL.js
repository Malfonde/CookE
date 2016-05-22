var mydb = require('./myDB')
 , ObjectID = require('mongodb').ObjectID;

var connection;
mydb.openDatabase(function (err, db) {
    if (db != null) {
        connection = db;
    }
});

module.exports = {

    getAllRecipes: function (callback) {
        // Return all the Recipes in the db
        var results;
        connection.collection("Recipes").find({}).toArray(function (err, messages) {
            if (err) {
                console.log('err:' + err);
                callback(err, results);
            }
            else {
                results = messages;
                callback(null, results);
            }
        });
    },

    saveNewRecipe: function (newRecipe, callback) {
        var collection = connection.collection("Recipes");

        // Insert new Recipe to Recipes collection
        var result = collection.insert({
            Name: newRecipe.name,
			UserID: newRecipe.userID,
            CookingInstructions: newRecipe.cookingInstructions,
			ServingInstructions: newRecipe.servingInstructions,
			Description: newRecipe.description,
			Ingredients: newRecipe.ingredients,
			ImagePath: newRecipe.image
			
        }, function (err, data) {
            if (err) {
                console.log('new recipe error: ' + err);
                callback(err, data);
            }
            else {
				console.log('new recipe success');
                callback(null, data.ops[0]);
            }
        });
    },

    editRecipe: function (recipe, callback) {
        var o_id = ObjectID(recipe._id);
        var collection = connection.collection("Recipes");
        var result = collection.update(
        {_id: o_id },
        {Name: recipe.name,
        UserID: recipe.userID,
        CookingInstructions: recipe.cookingInstructions,
        ServingInstructions: recipe.servingInstructions,
        Description: recipe.description,
        Ingredients: recipe.ingredients,
        ImagePath: recipe.image
        }, function (err, numberOfRecordsUpdatedJson) {
            if (err) {
                    console.log('edit recipe error: ' + err);
                    callback(err, objectWithStatusOperation);
                }
                else {
                    console.log('edit recipe success');
                    console.log('number of records Changed : ' + numberOfRecordsUpdatedJson);
                    callback(null, numberOfRecordsUpdatedJson);
                }
        });
    },

    deleteRecipe: function (recipeID, callback) {
    var collection = connection.collection("Recipes")
    var o_id = ObjectID(recipeID);
    var result = collection.remove(
            {_id: o_id},
            {
                justOne: true
            }
            , function (err, data) {
                if (err) {
                        console.log('delete recipe error: ' + err);
                        callback(err, recipeID);
                    }
                    else {
                        console.log('delete recipe success');
                        callback(null, recipeID);
                    }
            });
    },
	
	addNewUser: function (newUser, callback){
		var collection = connection.collection("Users");
		
		var result = collection.insert({
			//Favorites: newUser.favorites,
			UserID: newUser.userID
		}, function (err, data){
			
			if(err){
				console.log('new user error: ' + err);
				callback(err,data);
			}
			else{
				console.log('new user success');
                callback(null, data.ops[0]);
			}			
		});
	},

	findUser: function (userID, callback){
	    var collection = connection.collection("Users");

        var result = collection.findOne({UserID: userID}, function(err,result){
            if(err){
                console.log('err: ' + err);
                callback(err,result);
            }
            else
            {
                console.log('!@#!@#!@#!@!#@!#@ found 1 user !#!@#!@#!@#!@#!@# : ' + result);
                callback(null,result);
            }
        });
	},
	
	
	getAllRecipesByUser: function(userId,callback){
		// Returns all the recipes of the user
		var results;
		connection.collection("Recipes").find({UserID:userId}).toArray(function (err, recipes) {
            if (err) {
                console.log('err:' + err);
                callback(err, recipes);
            }
            else {
                results = recipes;				
                callback(null, results);
            }
        });		
	},

	getAllUserFavoriteRecipes: function(userID, callback){
    		var results;
    		var o_id = ObjectID(userID);

    		connection.collection("Users").find({_id:o_id}, { fields: { 'Favorites': 1, '_id': 0 } }).toArray(function (err, favoritesList)
    		{
    			if(err)
    			{
    				console.log(err);
                    callback(err, results);
    			}
    			else
    			{
    			//todo: get reipes from recipes collection by favorites ids
    				results = favoritesList;
    				console.log(results);
    				callback(null, results);
    			}
    		});
    	},

	// returns recipes by query
	getRecipesByIDs: function (jsonArr, callback)
	{
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

		var results;
		connection.collection("Recipes").find({ $or: queryArr }).toArray(function (err, retRecipes)
		{
			if(err)
			{
				console.log(err);		
				callback(err, results);
			}
			else
			{
				results = retRecipes;
				console.log(results);				
				callback(null, results);
			}			
		});
	},
	
	addRecipeToUserFavorites: function (recipeToAdd, userID, callback)
	{
        var collection = connection.collection("Users");
       // var o_id = ObjectID(userID);
        collection.update({ UserID: userID }, { $addToSet: { Favorites: recipeToAdd }}, function(err)
        {
            if (err)
            {
                console.log('update favorites error: ' + err);
                callback(err);
            }
            else
            {
                console.log('update favorites success');
                callback(null);
            }
        });
    	},

    removeRecipeFromUserFavorites: function (favRecipe, userID, callback)
    {
        var collection = connection.collection("Users");
        collection.update({ UserID: userID }, { $pull: { Favorites: favRecipe }}, function(err)
        {
            if (err)
            {
                console.log('update favorites error: ' + err);
                callback(err);
            }
            else
            {
                console.log('update favorites success');
                callback(null);
            }
        });
    },

  /*  queryUserByID: function (queryJson, callback){
        var results;
        console.log(queryJson);
        db.collection("Users").findOne(queryJson, function (err, userDetails) {
            if (err) {
                console.log(err);
                callback(err, results);
            }
            else {
                results = userDetails;
                console.log(results);
                callback(null, results);
            }
        });
    },*/

    getAllFavoriteLists: function(callback){
        var results;
        var collection = connection.collection("Users");
        collection.find({}, { fields: { 'Favorites': 1, '_id': 0 } }).toArray(function (err, favoritesLists)
        {
            if (err)
            {
                console.log(err);
                callback(err, results);
            }
            else
            {
                var allLists = [];
                favoritesLists.forEach(function (list) {
                    allLists.push(list.watchList);
                });

                results = allLists;
                console.log(results);
                callback(null, results);
            }
        });
    }
};