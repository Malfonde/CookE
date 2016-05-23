var main = require('./Main')
  , Apriori = require('apriori')
  , Underscore = require('underscore');

// -----------------------------------
// -------------- Add ----------------
// -----------------------------------

// --- Recipe ---  
exports.addRecipe = function (req, res)
 {
	var recipe = req.body.recipe; // Getting the parameters
	var recipejson = JSON.parse(recipe);
	
	console.log("main.addRecipe part*****************");
    main.addRecipe(recipejson,function (err, recipe) {
        if (err) {
            console.log("Faild to add new recipe: " + err);
			res.send(JSON.stringify({ status:"Fail", recipe: recipe }));
        } else {
            console.log("New recipe saved!");
            res.send(JSON.stringify({ status:"Success", recipe: recipe }));
        }
    });
 };
 
 exports.addUser = function (req, res)
 {
	var user = req.body.recipe; // Getting the parameters
    console.log(user);
	var userjson = JSON.parse(user);
	
	console.log("main.addUser part*****************");
	main.findUser(userjson.userID, function(err, user)
	{
	    if(err)
	    {
            console.log("Faild to check if the user already exists: " + err);
			res.send(JSON.stringify({ status:"Fail", user: user }));
		}
		else
		{
		    if(user)
		    {
		        console.log("User already exists");
		        res.send(JSON.stringify({ status:"Success", user: user }));
		    }
		    else
		    {
		       main.addNewUser(userjson,function (err, user)
		       {
                   if (err)
                   {
                        console.log("Faild to add new user: " + err);
                        res.send(JSON.stringify({ status:"Fail", user: user }));
                   }
                   else
                   {
                       console.log("New user saved!");
                       res.send(JSON.stringify({ status:"Success", user: user }));
                   }
               });
		    }
		}
	});

 };

exports.editRecipe = function (req, res)
{
    var recipe = req.body.recipe; // Getting the parameters
    var recipejson = JSON.parse(recipe);

    console.log("main.editRecipe part*****************");
    main.editRecipe(recipejson,function (err, recipe) {
        if (err) {
            console.log("Faild to update recipe: " + err);
            res.send(JSON.stringify({ status:"Fail", recipe: recipe }));
        } else {
            console.log("recipe saved!");
            res.send(JSON.stringify({ status:"Success", recipe: recipe }));
        }
    });
};

exports.deleteRecipe = function (req, res)
{
    var recipe = req.body.recipe; // Getting the parameters
    console.log("************************* : " + recipe);
    var recipejson = JSON.parse(recipe);

    console.log("main.deleteRecipe part*****************");
    main.deleteRecipe(recipejson._id,function (err, recipeID) {
        if (err) {
            console.log("Faild to delete recipe: " + err);
            res.send(JSON.stringify({ status:"Fail", recipe: recipeID }));
        } else {
            console.log("recipe deleted!");
            res.send(JSON.stringify({ status:"Success", recipe: recipeID }));
        }
    });
};

 exports.getUserRecipes = function (req,res)
 {
     var temp = req.body.recipe; // Getting the parameters
     var jsonId = JSON.parse(temp);

     console.log("main.getUserRecipes part*****************");

     main.getAllRecipesByUser(jsonId.userID,function (err, results)
     {
         if (err) {
             console.log("Faild to get all the recipes of a user: " + err);
             res.send(JSON.stringify({ status: "Fail" }));
         }
         else {
             console.log("got all the user's recipes");
             res.send(JSON.stringify({ Recipes: results }));
         }
     });
 };

 exports.getAllUsersRecipes = function(req,res)
 {
    console.log("main.getAllUsersRecipes part*****************");
    main.getAllUsersRecipes(function(err, results)
    {
       if (err)
       {
         console.log("Faild to get all the recipes:  " + err);
         res.send(JSON.stringify({ status: "Fail" }));
       }
       else
       {
         console.log("got all the recipes");
         res.send(JSON.stringify({ Recipes: results }));
       }
    });
 };

 exports.addRecipeToUserFavorites = function (req,res)
 {
    console.log("main.addRecipeToUserFavorites part*****************");
	 var recipe = req.body.recipe;
	 var recipejson = JSON.parse(recipe);
	 console.log("recipejson.requestedUserID : " + recipejson.requestedUserID);

	  main.addRecipeToUserFavorites(recipejson,recipejson.requestedUserID,function (err, recipe) {
        if (err) {
            console.log("Faild to add recipe to user favorites : " + err);
			res.send(JSON.stringify({ status:"Fail", recipe: recipe }));
        } else {
            console.log("Recipe was added to favorites!");
            res.send(JSON.stringify({ status:"Success", recipe: recipe }));
        }
    });
 };

 exports.removeRecipeFromUserFavorites = function(req,res)
 {
     var recipe = req.body.recipe;
     var recipejson = JSON.parse(recipe);
    console.log("main.removeRecipeFromUserFavorits part*****************");

      main.removeRecipeFromUserFavorites(recipejson,recipejson.requestedUserID,function (err, recipe) {
            if (err) {
                console.log("Faild to remove recipe from user favorites : " + err);
    			res.send(JSON.stringify({ status:"Fail", recipe: recipe }));
            } else {
                console.log("Recipe was removes from favorites!");
                res.send(JSON.stringify({ status:"Success", recipe: recipe }));
            }
        });
 };

 exports.getUserFavoriteRecipes = function(req,res)
 {
    console.log("main.getUserFavoriteRecipes part*****************");
    var recipe = req.body.recipe;
    var userjson = JSON.parse(recipe);

    main.findUser(userjson.userID, function (err, user)
    {
        if(err)
        {
            console.log("Faild to find user : " + err);
            res.send(JSON.stringify({status:"Fail"}));
        }
        else
        {
            console.log("Success in finding user : ");
            res.send(JSON.stringify({status:"Success", Recipes:user.Favorites}));
        }
    });
 };


//---- Apriori Methods ----

exports.getRecommandedRecipesByUser = function (req, res)
  {
 	console.log("main.getRecommandedRecipesByUser part*****************");
 	var userFromClient = req.body.recipe; // Getting the parameters
 	var userjson = JSON.parse(userFromClient);

 	main.findUser(userjson.userID, function (errUser, user)
 	{
 		main.getAllFavoriteLists(function (err, results)
 		{
 		//TODO: this is static!!
 		var staticResults =  [['5739d6919ff546d08d7fe11a', '5739d6919ff546d08d7fe11e'],
                             ['5739d6919ff546d08d7fe11a', '5739d6919ff546d08d7fe11d','5739d6919ff546d08d7fe11e']];
        var staticUserFavorites = ['5739d6919ff546d08d7fe11a'];

 			if (err) {
 				console.log("Faild to get all the favorite recipes of all users: " + err);
 				res.send(JSON.stringify({ status: "Fail" }));
 			}
 			else {
 				console.log("got all the users favorite recipes");
 				//var aprioriRes = getAprioriResults(user.Favorites, results);
 				var aprioriRes = getAprioriResults(staticUserFavorites, staticResults);
 				aprioriRes.forEach(function(currID)
 				{
 				    console.log("the id in the result : " + currID);
 				});

 				if (aprioriRes.length > 0)
 				{
 					var idsJson = [];
 					aprioriRes.forEach(function (currId) {
 						idsJson.push({ id: currId });
 					});

 					main.getRecipesByIDs(idsJson, function (err, retRecipes) {
 						if (err) {
 							console.log(err);
 							res.send(JSON.stringify({ status: "Fail" }));
 						}
 						else {
 							console.log(retRecipes);
 							console.log("getRecommandedRecipesByUser success!!");
 							res.send(JSON.stringify({
 								status: "Success",
 								Recipes: retRecipes
 							}));
 						}
 					});
 				}
 			}
 		});
 	});
  };
 
 //--- private Apriori methods ---
 
var getAprioriResults = function (arrayToFind, allLists)
{
    console.log("getAprioriResults part ********************************");
    var algResults = new Apriori.Algorithm(0.4, 0.6, false).analyze(allLists);
    var associationRules = algResults.associationRules;
    console.log("assosiation rules results : ");

    var formatJson = JSON.stringify(associationRules);
    console.log(formatJson);

    var resultsArr = [];
    var maxLength = 0;
    var maxRhs = [];

    for (var i = 0; i < associationRules.length; i++) {
        var currRule = associationRules[i];        
        var intersect = Underscore.intersection(arrayToFind, currRule.lhs);
        console.log("*********************** intersect.length : " + intersect.length);
        console.log("**********************  arrayToFind.length : " + arrayToFind.length);
        if (intersect.length == arrayToFind.length) {
            maxLength = intersect.length;
            maxRhs = currRule.rhs;
            break;
        }
        else {
            if (intersect.length > maxLength) {
                maxLength = intersect.length;
                var maxRhs = currRule.rhs;
            }
            else if (intersect.length == maxLength && maxLength != 0) {
                if (maxRhs.length < currRule.rhs.length) {
                    maxLength = intersect.length;
                    var maxRhs = currRule.rhs;
                }
            }
        }
    };
console.log("the results are : maxRhs **************** " + maxRhs);
    return maxRhs;
};