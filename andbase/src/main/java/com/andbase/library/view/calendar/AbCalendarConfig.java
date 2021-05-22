package com.andbase.library.view.calendar;

import android.graphics.Color;

public class AbCalendarConfig {


    /** 网格线颜色. */
    public int gridLineColor;

    /** calendar 背景颜色. */
    public int calendarBackgroundColor;

    /** cell背景颜色 */
    public int cellBackgroundColor;

    /** 不可编辑的 cell背景颜色 */
    public int uneditableCellBackgroundColor;

    /** holiday cell文字颜色. */
    public int holidayCellTextColor;

    /** 今天 cell文字颜色 */
    public int todayCellTextColor;

    /** 今天的文字颜色 */
    public int primaryColor;

    /** 关注日期的文字颜色 */
    public int attendCellTextColor;

    /** 特别重要日期的文字颜色 */
    public int importantCellTextColor;
    /** 特别重要日期的文字颜色 */
    public int importantCellBackgroundColor;

    /** 非本月的天文字颜色. */
    public int notActiveDayTextColor;

    /** 不可编辑文字颜色. */
    public int unEditableDayTextColor;

    /** 数字颜色. */
    public int dayTextColor = Color.rgb(86, 86, 86);

    /** 被选中的cell颜色. */
    public int selectedCellTextColor;
    public int selectedCellBackgroundColor;

    /** 被选中的cell形状. 1 为圆形   0为方形边框*/
    public int selectedCellShape;

    public static AbCalendarConfig getConfig(int index){
        AbCalendarConfig config = new AbCalendarConfig();

        config.gridLineColor = Color.WHITE;
        config.calendarBackgroundColor = Color.rgb(255,255, 255);
        config.cellBackgroundColor = Color.WHITE;
        config.holidayCellTextColor = Color.rgb(111, 111, 111);
        config.todayCellTextColor = Color.rgb(101, 213, 204);
        config.primaryColor = Color.rgb(101, 213, 204);
        config.selectedCellBackgroundColor = config.primaryColor;
        config.selectedCellTextColor = Color.rgb(255, 255, 255);
        config.selectedCellShape = 0;
        config.notActiveDayTextColor = Color.rgb(255, 255, 255);
        config.unEditableDayTextColor = Color.rgb(192, 192, 192);
        config.attendCellTextColor = Color.rgb(243, 118, 159);
        config.importantCellTextColor = Color.WHITE;
        config.importantCellBackgroundColor = Color.rgb(243, 118, 159);

        config.uneditableCellBackgroundColor = Color.rgb(200, 200, 200);
        return config;
    }
}
