package ras.asu.com.letsmeet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rakesh on 4/15/2016.
 */
public class ImageMappings {
    Map<String,Integer> data;
    ImageMappings(){

        data = new HashMap<String,Integer>();
        data.put("coffee",R.drawable.coffee_cup_icon_70002);
        data.put("restaurant",R.drawable.city_restaurant_icon);
        data.put("icecream",R.drawable.cup_ice_cream);
        data.put("pizza",R.drawable.food_pizza_icon);
        data.put("fastfood",R.drawable.food_cooking_meal_5_512);
    }

public static void main()
{



}

}
