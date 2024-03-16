# <u> Installation/Use Instructions
## Including the API in Gradle</u>
### In your build.gradle file:
add to  `dependencies`:
``` 
modImplementation 'com.github.MyShampooIsDrunk:weapons_api:<version>'
```
and if you want your mod to be packaged with the API (please include something in your README that credits me), add:
```
include 'com.github.MyShampooIsDrunk:weapons_api:1.1.1'
```
add to `repositories`:
```
maven { url 'https://jitpack.io' }
```

## <u>Implementing a Custom Item</u>
### Creating your custom item:
Create a custom class that `extends` the abstract class `AbstractCustomItem` 
\
Here you will define all of the custom features of your item
\
\
You'll want to `@Override` and create a new constructor with no parameters 
\
In this constructor you need to have a `super()` statement containing an `Item`, an `Identifier` with a namespace that should be the id of your mod defined in your mod's `fabric.mod.json`, and, optionally, a name for your item that would link to a lang file. 
\
Because lang files are defined as part of a texture pack, you would have to load the texture pack on the client side and the server side for it to work. Alternatively, if you set the name to be a regular string rather than a link to a texture pack, it'll work for that regular name.
\
\
If you want to give your item a custom interaction, eg. having it spawn a fireball on a right click, you can simply `@Override` the `onUse` method of its parent class and implement your feature however you want
### Registering your custom item:
In the `onInitialize` method in your main class, instantiate your custom item. \
for example, if you named your item `MyCustomItem`, you should instantiate it in `onInitialize` and assign it to a variable.\
For example: 
```
public void onInitialize() {
    MyCustomItem myItem = new MyCustomItem();
}
```
Now that you have your custom item instantiated, to register it, call the static method in `CustomItemRegistry` called `registerItem` like so:
``` 
CustomItemRegistry.registerItem(myItem);
```
Then, if you want to add it to a group (the ones you see in the creative menu), you can call `addToGroup` like so:
```
CustomItemRegistry.addToGroup(timeStopItem, ItemGroups.REDSTONE);
//you can replace ItemGroups.REDSTONE with whatever group you want!
```
Note that you will only be able to get the item from the creative menu if you have the mod installed.

### Obtaining your item other ways:
#### Using Null Recipes
If you want to get your item without having to access the creative menu, you can do this through a command or through a recipe. Either way, you'll have to create a recipe.\
If you want to get your item without needing to create a recipe, you can just register it with a `null` recipe like so:
```
CustomItemRegistry.registerRecipe(null,<identifier>,myItem)
```
where \<identifier> represents an identifier for the `DataGenerator` to use when generating the advancement and loot table for the `null` recipe\
In this case, you can obtain the item by giving yourself the advancement in-game called `<identifier>_recipe_advancement` through the `/advancement grant` command or by giving yourself the loot table in-game called `<identifier>_recipe_output` through the `/loot` command
#### Using Crafting Recipes

Alternatively, you can obtain the item through a crafting recipe that you can register by calling the `registerRecipe()` method but replacing the `null` recipe with a `ShapedRecipe` or `ShapelessRecipe`\
For example:
```
CustomItemRegistry.registerRecipe(
    new ShapelessRecipe("", CraftingRecipeCategory.MISC,
        myItem.create(),
        DefaultedList.copyOf(Ingredient.EMPTY,Ingredient.ofItems(Items.STONE),Ingredient.ofItems(Items.STONE))
    ),myItem.getIdentifier(),
    myItem
);
```
In this case, I just define a shapeless recipe that requires 2 stone to craft\
This way, you can also obtain the item by using the commands mentioned 

#### Generating Your Recipe and Its Give Command
Regardless of if you chose to use a null recipe or create your own crafting recipe, you'll still have to do this step.
Luckily, this is the easiest step.\
At the end of your `onInitialize` method, run the static method `WeaponAPI.initializeRecipes()`\
Then, in the `onInitializeDataGenerator` method within your DataGenerator class, run the static method `WeaponAPIDataGenerator.initializeDataGen(fabricDataGenerator)` where \<fabricDataGenerator> is the parameter for the `onInitializeDataGenerator` method \
After doing this, run the client and then the dataGenerator and you should be all set!\
\
\
As per usual, if you have any specific questions about implementing features or implementing custom items, feel free to join our discord: https://discord.com/invite/JUee9ausGb
