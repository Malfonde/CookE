var main = require('./Main')
  , Apriori = require('apriori')
  , Underscore = require('underscore');
/*
// -----------------------------------
// ------------- Queries -------------
// -----------------------------------

// --- Sales ---

exports.querySales = function (req, res) {
    console.log('bl - querySales');
    main.querySales(req.body, function (err, sales) {
        if (err) {
            console.log('error bl - querySales: ' + err);
            res.json({ sales: sales });
        } else {
            console.log('query sales success');

            res.json(sales);
        }
    });
};

exports.concertSalesWithUserDetails = function (req, res) {
    // Get concert's sales by its id
    main.querySales(req.body, function (err, sales) {
        if (err) {
            console.log("error bl - concertSalesWithUserDetails " + err);
            res.json({ status: "Fail" });
        } else {
            console.log("concertSalesWithUserDetails succeed");
            fillUsersInfo(sales, 0, function (err, r) {
                console.log("Finished!");
                console.log(r);
                res.json(sales);
            });

        }
    });
};


var fillUsersInfo = function (sales, currentSale, callback) {
    if (currentSale < sales.length) {
        main.queryUserByID(sales[currentSale].UserID, function (err, userDetailes) {
            console.log("after queryUserById");
            if (err != null)
                console.log("error in queryUserById : " + err);

            // Add the detailes to the json
            sales[currentSale]["SellerName"] = userDetailes.username;
            sales[currentSale]["SellerEmail"] = userDetailes.email;
            sales[currentSale]["SellerPhone"] = userDetailes.phoneNumber;
            sales[currentSale]["Pic"] = userDetailes.Pic;

            fillUsersInfo(sales, currentSale + 1, callback);
        });
    }
    else {
        callback(null, sales);
    }
};

exports.getSaleDetailsForUpdate = function (req, res, userId) {
    main.querySales(req.body, function (err, sales) {
        if (err) {
            if (err == "Wrong ID") {
                console.log('error bl - getSaleDetailsForUpdate: ' + err);
                res.json({ status: "Wrong ID" });
            }
            else {
                console.log('error bl - getSaleDetailsForUpdate: ' + err);
                res.json({ status: "Fail" });
            }
        } else {
            if (sales.length == 0) {
                res.json({ status: "Sale Not Found" });
            } else {
                if (sales[0].UserID == userId) {
                    res.json({
                        status: "Success",
                        sale: sales[0]
                    });
                } else {
                    res.json({
                        status: "Wrong User"
                    });
                }
            }
        }
    });
};

// --- Shows ---

exports.queryShows = function (req, res) {
    console.log('bl - queryShows');
    main.queryShows(req.body, function (err, shows) {
        if (err) {
            console.log('error bl - queryShows: ' + err);
            res.json({
                status: "Fail",
                shows: shows
            });
        } else {
            console.log('query events success');

            res.json({
                status: "Success",
                results: shows
            });
        }
    });
};

// Quering the show with the most sales 
exports.queryMostPopularEvent = function (req, res) {
    main.queryMostPopularEvent
}

exports.popularEvents = function (req, res) {
    main.getWatchLists(function (err, results) {
        if (err) {
            res.json({ status: "Fail" });
        }
        else {
            var algRes = getAprioriPopular(results);

            if (algRes.length > 0) {

                var idsJson = [];
                algRes.forEach(function (currId) {
                    idsJson.push({ id: currId });
                });

                main.queryShows(idsJson, function (showsErr, retShows) {
                    if (showsErr) {
                        console.log(showsErr);
                        res.json({ status: "Fail" });
                    }
                    else {
                        console.log(retShows);
                        console.log("getAprioriPopular success!!");
                        res.json({
                            status: "No LoggedOn User",
                            popEvents: retShows
                        });
                    }
                });
            }
        }
    });
};


// --- User ---

exports.getUserByUsername = function (req, res, userIdFromCookies) {

    console.log(req.body.Username);

    main.getUserByUsername({ username: req.body.Username }, function (err, resultUser) {

        if (err) {
            console.log('error bl - getUserByUsername: ' + err);
            res.json({ status: "Failed To Get User By Username" });
        } else {
            console.log('query getUserByUsername success');

            if (resultUser != undefined && resultUser != null) {
                console.log(resultUser._id);

                if (resultUser._id == userIdFromCookies) {
                    res.json({ status: "Success", user: resultUser });
                }
                else {
                    res.json({ status: "Not The Logged User" });
                }
            }
            else {
                res.json({ status: "User Not Found" });
            }
        }

    });
};

exports.getUserById = function (req, res, userIdFromCookies) {

    console.log(req.body.Username);

    main.queryUserByID(userIdFromCookies, function (err, resultUser) {

        if (err) {
            console.log('error bl - getUserById: ' + err);
            res.json({ status: "Failed To Get User By ID" });
        } else {
            console.log('query getUserById success');

            if (resultUser != undefined && resultUser != null) {
                res.json({ status: "Success", user: resultUser });
            }
            else {
                res.json({ status: "User Not Found" });
            }
        }

    });
};

exports.userLocation = function (req, res, userIdFromCookies) {

    main.queryUserByID(userIdFromCookies, function (errUser, user) {
        if (errUser) {
            console.log(errUser);
            res.json({ status: "Fail" });
        }
        else {
            console.log("get userLocation success");
            res.json({
                status: "Success",
                userDetails: {
                    fullAddress: user.fullAddress
                    //city: user.city,
                    //address: user.address
                }
            });
        }
    });
};

// -----------------------------------
// ------------- Delete --------------
// -----------------------------------

// --- Sales ---

exports.deleteConcertSale = function (req, res, userId) {
    var saleQuery = [{ id: req.params.id }];

    if (userId == "undefined") {
        res.json({ status: "No Logged In User" });
    }
    else {
        main.querySales(saleQuery, function (err, sale) {
            if (err) {
                console.log('error bl - querySales: ' + err);
                res.json({ status: "Fail", sales: sales });
            } else {
                if (sale != undefined && sale[0].UserID != userId) {
                    console.log("The user not permitted to delete");
                    res.json({ status: "permissionError" });
                }
                else {
                    main.deleteSale(req.params.id, function (err, updateConcert) {
                        if (err) {
                            console("Failed to delete sale: " + err);
                            res.json({ status: "Fail" });
                        } else {
                            res.json({ status: "Success", sale: updateConcert });
                        }
                    });
                }
            }
        });
    }
};


*/
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
	main.findUser(userjson, function(err, user)
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
    var recipeID = req.body.recipeID; // Getting the parameters
    var recipeIDjson = JSON.parse(recipeID);

    console.log("main.deleteRecipe part*****************");
    main.deleteRecipe(recipeIDjson,function (err, recipeID) {
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

      main.removeRecipeFromUserFavorites(recipejson,userID,function (err, recipe) {
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

    main.findUser(userjson, function (err, user)
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

 	var userFromClient = req.body.user; // Getting the parameters
 	var userjson = JSON.parse(userFromClient);

 	main.findUser(userjson, function (errUser, user)
 	{
 		main.getAllFavoriteLists(function (err, results)
 		{
 			if (err) {
 				console.log("Faild to get all the favorite recipes of all users: " + err);
 				res.send(JSON.stringify({ status: "Fail" }));
 			}
 			else {
 				console.log("got all the users favorite recipes");
 				var aprioriRes = getAprioriResults(user.Favorites, results);
 				console.log(aprioriRes);

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
 								popRecipes: retRecipes
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

    var formatJson = JSON.stringify(associationRules);
    console.log(formatJson);

    var resultsArr = [];
    var maxLength = 0;
    var maxRhs = [];

    for (var i = 0; i < associationRules.length; i++) {
        var currRule = associationRules[i];        
        var intersect = Underscore.intersection(arrayToFind, currRule.lhs);

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

/*

exports.recommandByUser = function (req, res, idFromCookies) {
    main.queryUserByID(idFromCookies, function (errUser, user) {
        if (errUser) {
            console.log(errUser);
            res.json({ status: "Fail" });
        }
        else {
            main.getWatchLists(function (err, results) {
                if (err) {
                    console.log(err);
                    res.json({ status: "Fail" });
                }
                else {
                    console.log(user.watchList);
                    var aprioriRes = getAprioriResults(user.watchList, results);

                    console.log(aprioriRes);

                    if (aprioriRes.length > 0) {

                        var idsJson = [];
                        aprioriRes.forEach(function (currId) {
                            idsJson.push({ id: currId });
                        });

                        main.queryShows(idsJson, function (showsErr, retShows) {
                            if (showsErr) {
                                console.log(showsErr);
                                res.json({ status: "Fail" });
                            }
                            else {
                                console.log(retShows);
                                console.log("recommandByEvent success!!");
                                res.json({
                                    status: "Success",
                                    popEvents: retShows
                                });
                            }
                        });
                    }
                    else {
                        console.log("There were no results found");
                        var algRes = getAprioriPopular(results);

                        if (algRes.length > 0) {

                            var idsJson = [];
                            algRes.forEach(function (currId) {
                                idsJson.push({ id: currId });
                            });

                            main.queryShows(idsJson, function (showsErr, retShows) {
                                if (showsErr) {
                                    console.log(showsErr);
                                    res.json({ status: "Fail" });
                                }
                                else {
                                    console.log(retShows);
                                    console.log("getAprioriPopular success!!");
                                    res.json({
                                        status: "No Results For User",
                                        popEvents: retShows
                                    });
                                }
                            });
                        }
                    }
                }
            });

        }

    });

};


// --- Users ---

exports.updateUser = function (req, res, idFromCookies) {

    main.getUserByUsername({ username: req.body.Username }, function (err, resultUser) {

        if (err) {
            console.log("Faild to update user: " + err);
            res.json({ status: "User Not Found" });
        } else {

            if (resultUser._id == idFromCookies) {
                main.updateUser(req.body, idFromCookies, function (updateErr, user) {
                    if (updateErr) {
                        console.log("Faild to update user: " + updateErr);
                        res.json({ status: "Faild To Update" });
                    } else {
                        console.log("updated user saved!");
                        res.json({
                            status: "Success",
                            user: user
                        });
                    }
                });
            }
            else {
                console.log("Could not update because this is not the logged user!!");
                res.json({ status: "Not The Logged User" });
            }
        }
    });
};

// --- Logins ---
exports.login = function (req, res) {
    console.log(req.body);
    main.getUserByUsername({ username: req.body.username }, function (err, user) {
        if (err) {
            console.log("Faild to login: " + err);
            res.json({ status: "Fail" });
        } else {
            if (user != undefined && user != null) {
                if (user.password == req.body.password) {
                    console.log("Login succeded!");
                    res.json({
                        status: "Success",
                        user: user
                    });
                }
                else {
                    res.json({ status: "Incorrect Password" });
                }
            }
            else {
                res.json({ status: "User Not Found" });
            }
        }
    });
};

exports.AddToWatchList = function (ShowId, username) {

    main.AddToWatchList(ShowId, username, function (err) {
        if (err) {
            console.log("Faild AddToWatchList: " + err);
        } else {
            console.log("AddToWatchList succeded!");
        }
    });
};


var getAprioriPopular = function (allLists) {

    var algResults = new Apriori.Algorithm(0.4, 0.0, false).analyze(allLists);
    var frequentItemSets = algResults.frequentItemSets["1"];

    var popResults = [];

    if (frequentItemSets != undefined) {
        frequentItemSets.forEach(function (itemSet) {
            popResults.push(itemSet.itemSet[0]);
        });
    }
    var formatJson = JSON.stringify(popResults);
    console.log(formatJson);

    return popResults;
};*/