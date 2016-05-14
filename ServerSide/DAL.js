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
	
	addNewUser: function (newUser, callback){
		var collection = connection.collection("Users");
		
		var result = collection.insert({
			Favorites: newUser.favorites			
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
	
	// returns recipes by query
	getRecipesByQueryArray: function (recipesQuery, callback){
		
		var results;
		connection.collection("Recipes").find({ $or: recipesQuery }).toArray(function (err, retRecipes) 
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
	
	addRecipeToUserFavorits: function (recipeToAdd, userID, callback){		
		var collection = connection.collection("Users");
		
		var updatedUser = {
                Favorites:  
            };
		
		
	}
};