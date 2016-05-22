var Underscore = require('underscore');
var Dal = require('./DAL')

module.exports = {
	
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
    		Dal.deleteRecipe(recipeIDToDelete, function (err, recipeID)
    		{
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

	findUser: function(userToVerify, callback){
        console.log('main - findUser');
        console.log(userToVerify);
        Dal.findUser(userToVerify.userID, function(err,user)
        {
            if(err){
                console.log('find user error: ' + err);
                callback(err, user);
            }
            else{
                console.log('find user success: '+ user);
                callback(null, user);
            }
        });
	},

	addRecipeToUserFavorites: function(favRecipe, userID, callback){
		console.log('main - addRecipeToUserFavorits');
        console.log(favRecipe);
		Dal.addRecipeToUserFavorites(favRecipe, userID, function(err,recipe)
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

	removeRecipeFromUserFavorites: function(favRecipe, userID, callback)
	{
        console.log('main - removeRecipeFromUserFavorits');
        console.log(favRecipe);
        Dal.removeRecipeFromUserFavorites(favRecipe, userID, function(err,recipe)
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

	
	getRecipesByIDs: function (jsonArr, callback){

		Dal.getRecipesByIDs(jsonArr, function(err,results)
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

	getAllUsersRecipes: function(callback){
	    console.log('main - getAllUsersRecipes');

	    Dal.getAllRecipes(function(err, results){
	        if(err)
	        {
	            console.log('get all recipes error: ' + err);
                callback(err, results);
	        }
	        else{
                console.log('get all recipes success');
                callback(err, results);
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

    /*queryUserByID: function (Id, callback){
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
    },*/

    getAllFavoriteLists: function(callback){
        console.log('main - getAllFavoriteLists part*******');
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





