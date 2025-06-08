package com.nt.tracker.common;

public class Constants {
    private Constants() {} // 防止实例化

    /** 性别类型 */
    public static final int GENDER_MALE = 1;        // 男
    public static final int GENDER_FEMALE = 2;      // 女

    /** 营养素类型 */
    public static final int MACRO_CARBOHYDRATE = 1; // 碳水
    public static final int MACRO_PROTEIN = 2;      // 蛋白质
    public static final int MACRO_FAT = 3;          // 脂肪

    /** 餐次类型 */
    public static final int MEAL_BREAKFAST = 1;     // 早餐
    public static final int MEAL_LUNCH = 2;         // 中餐
    public static final int MEAL_DINNER = 3;        // 晚餐
    public static final int MEAL_SNACK = 4;         // 加餐

    /** 食物tag:
    * 1,高蛋白
    * 2,低脂肪
    * 3,高脂肪
    * 4,低碳水
    * 5,高碳水
    * 6,高热量
    * 7,低热量
    * 8,增肌推荐
    * 9,减脂推荐
    */
    public static final int FOOD_TAG_HIGH_PROTEIN = 1;
    public static final int FOOD_TAG_LOW_FAT = 2;
    public static final int FOOD_TAG_HIGH_FAT = 3;
    public static final int FOOD_TAG_LOW_CARBOHYDRATE = 4;
    public static final int FOOD_TAG_HIGH_CARBOHYDRATE = 5;
    public static final int FOOD_TAG_HIGH_CALORIE = 6;
    public static final int FOOD_TAG_LOW_CALORIE = 7;
    public static final int FOOD_TAG_BUILD_MUSCLE = 8;
    public static final int FOOD_TAG_LOSE_WEIGHT = 9;

    /**
     * Redis缓存key
     */

    public static final String REDIS_KEY_USER_FOOD_INVENTORY = "food_inv";
    public static final String REDIS_KEY_FOOD_LIST = "food_list";




}
