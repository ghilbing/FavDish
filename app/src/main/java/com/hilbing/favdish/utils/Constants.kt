package com.hilbing.favdish.utils

object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"
    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    const val EXTRA_DISH_DETAILS: String = "DishDetails"

    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "FilterSelection"

    //RETROFIT SERVICE VALUES
    const val BASE_URL = "https://api.spoonacular.com/"
    const val API_ENDPOINT = "recipes/random"
    //KEY PARAMS
    const val API_KEY: String = "apiKey"
    const val LIMIT_LICENSE: String = "limitLicense"
    const val TAGS: String = "tags"
    const val NUMBER: String = "number"
    //KEY VALUES
    const val API_KEY_VALUE: String = "510334c39d3f4560873f4673f23db30f"
    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val TAGS_VALUE: String = "vegetarian, dessert"
    const val NUMBER_VALUE: Int = 1


    fun dishTypes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")
        return list

    }

    fun dishCategories(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("pizza")
        list.add("bbq")
        list.add("bakery")
        list.add("burger")
        list.add("cafe")
        list.add("chicken")
        list.add("dessert")
        list.add("drinks")
        list.add("hot_dogs")
        list.add("juices")
        list.add("sandwich")
        list.add("tea & coffee")
        list.add("wraps")
        list.add("other")
        return list
    }

    fun dishCookingTimes(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("50")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("160")
        list.add("180")
        return list
    }

}