package com.andbase.library.view.picker;

import com.andbase.library.utils.AbDateUtil;
import com.andbase.library.view.listener.AbOnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info 轮子工具类
 */

public class AbPickerViewHelper {

    public final static  List<String> MDHMList = new ArrayList<String>();

    /**
     * 默认的年月日的日期选择器.
     *
     * @param pickerViewY  选择年的轮子
     * @param pickerViewM  选择月的轮子
     * @param pickerViewD  选择日的轮子
     * @param defaultYear  默认显示的年
     * @param defaultMonth the default month
     * @param defaultDay the default day
     * @param minYear    开始的年
     * @param maxYear     结束的年
     */
    public static void initPickerValueYMD(final AbPickerView pickerViewY, final AbPickerView pickerViewM, final AbPickerView pickerViewD,
                                          int defaultYear, int defaultMonth, int defaultDay, final int minYear, int maxYear){

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };
        //时间选择可以这样实现
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);

        if(defaultYear <= 0){
            defaultYear = year;
        }
        if(defaultMonth <= 0){
            defaultMonth = month;
        }
        if(defaultDay <= 0){
            defaultDay = day;
        }

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        //设置"年"的显示数据
        final List<String> yearList = new ArrayList<String>();
        for(int i= minYear;i<= maxYear;i++){
            yearList.add(i + "年");
        }
        pickerViewY.setItems(yearList);
        pickerViewY.setInitPosition(defaultYear-minYear);

        // 月
        final List<String> monthList = new ArrayList<String>();
        for(int i= 1;i< 13;i++){
            monthList.add(i + "月");
        }
        pickerViewM.setItems(monthList);
        pickerViewM.setInitPosition(defaultMonth-1);

        // 日
        // 判断大小月及是否闰年,用来确定"日"的数据
        final List<String> dayList = new ArrayList<String>();


        if (list_big.contains(String.valueOf(defaultMonth))) {
            for(int i= 1;i< 32;i++){
                dayList.add(i + "日");
            }
        } else if (list_little.contains(String.valueOf(defaultMonth))) {
            for(int i= 1;i< 31;i++){
                dayList.add(i + "日");
            }
        } else {
            // 闰年
            if (AbDateUtil.isLeapYear(year)){
                for(int i= 1;i< 30;i++){
                    dayList.add(i + "日");
                }
            }else{
                for(int i= 1;i< 29;i++){
                    dayList.add(i + "日");
                }
            }
        }
        if(pickerViewD!=null){
            pickerViewD.setItems(dayList);
            pickerViewD.setInitPosition(defaultDay-1);
        }


        // 添加"年"监听
        pickerViewY.setListener(new AbOnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String year = yearList.get(index).replace("年","");
                String month = pickerViewM.getItems().get(pickerViewM.getSelectedItem()).getValue().replace("月","");
                updateDayItems(year,month,pickerViewD);
            }
        });

        // 添加"月"监听
        pickerViewM.setListener(new AbOnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                String year = pickerViewY.getItems().get(pickerViewY.getSelectedItem()).getValue().replace("年","");
                String month = pickerViewM.getItems().get(pickerViewM.getSelectedItem()).getValue().replace("月","");

                if(pickerViewD!=null){
                    updateDayItems(year,month,pickerViewD);
                }

            }
        });

    }

    public static void updateDayItems(String year,String month,AbPickerView pickerViewD){
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        // 日
        // 判断大小月及是否闰年,用来确定"日"的数据
        final List<String> dayList = new ArrayList<String>();

        if (list_big.contains(String.valueOf(month))) {
            for(int i= 1;i< 32;i++){
                dayList.add(i + "日");
            }
        } else if (list_little.contains(String.valueOf(month))) {
            for(int i= 1;i< 31;i++){
                dayList.add(i + "日");
            }
        } else {
            // 闰年
            if (AbDateUtil.isLeapYear(Integer.parseInt(year))){
                for(int i= 1;i< 30;i++){
                    dayList.add(i + "日");
                }
            }else{
                for(int i= 1;i< 29;i++){
                    dayList.add(i + "日");
                }
            }
        }
        if(pickerViewD!=null){
            pickerViewD.setItems(dayList);
            pickerViewD.invalidate();
        }

    }

    /**
     * 默认当前时间的月日时分的时间选择器.
     * @param pickerViewMD  选择月日的轮子
     * @param mWheelViewHH 选择时间的轮子
     * @param pickerViewMM  选择分的轮
     */
    public static void initPickerValueMDHM(final AbPickerView pickerViewMD, final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //int second = calendar.get(Calendar.SECOND);
        initPickerValueMDHM(pickerViewMD,mWheelViewHH,pickerViewMM,year,month,day,hour,minute);
    }

    /**
     * 默认的月日时分的时间选择器.
     * @param pickerViewMD  选择月日的轮子
     * @param pickerViewHH the m wheel view hh
     * @param pickerViewMM  选择分的轮子
     * @param defaultYear the default year
     * @param defaultMonth the default month
     * @param defaultDay the default day
     * @param defaultHour the default hour
     * @param defaultMinute the default minute
     */
    public static void initPickerValueMDHM(final AbPickerView pickerViewMD, final AbPickerView pickerViewHH, final AbPickerView pickerViewMM,
                                           int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute){


        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
        String[] months_little = { "4", "6", "9", "11" };
        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        //
        final List<String> textDMList = new ArrayList<String>();

        for(int i=1;i<13;i++){
            if(list_big.contains(String.valueOf(i))){
                for(int j=1;j<32;j++){
                    textDMList.add(i+"月"+" "+j+"日");
                    MDHMList.add(defaultYear+"-"+i+"-"+j);
                }
            }else{
                if(i==2){
                    if(AbDateUtil.isLeapYear(defaultYear)){
                        for(int j=1;j<28;j++){
                            textDMList.add(i+"月"+" "+j+"日");
                            MDHMList.add(defaultYear+"-"+i+"-"+j);
                        }
                    }else{
                        for(int j=1;j<29;j++){
                            textDMList.add(i+"月"+" "+j+"日");
                            MDHMList.add(defaultYear+"-"+i+"-"+j);
                        }
                    }
                }else{
                    for(int j=1;j<29;j++){
                        textDMList.add(i+"月"+" "+j+"日");
                        MDHMList.add(defaultYear+"-"+i+"-"+j);
                    }
                }
            }

        }
        String currentDay = defaultMonth+"月"+" "+defaultDay+"日";
        int currentDayIndex = textDMList.indexOf(currentDay);

        // 月日
        pickerViewMD.setItems(textDMList);
        pickerViewMD.setInitPosition(currentDayIndex);

        // 时
        final List<String> textHHList = new ArrayList<String>();
        for(int i=0;i<24;i++){
            textHHList.add(i+"点");
        }

        String currentHH = defaultHour+"点";
        int currentHourIndex = textHHList.indexOf(currentHH);
        pickerViewHH.setItems(textHHList);
        pickerViewHH.setInitPosition(currentHourIndex);

        // 分
        final List<String> textMMList = new ArrayList<String>();
        for(int i=0;i<60;i++){
            textMMList.add(i+"分");
        }
        String currentMM = defaultMinute+"分";
        int currentMinuteIndex = textMMList.indexOf(currentMM);
        pickerViewMM.setItems(textMMList);
        pickerViewMM.setInitPosition(currentMinuteIndex);

    }

    /**
     * 默认的时分的时间选择器.
     * @param mWheelViewHH the m wheel view hh
     * @param pickerViewMM  选择分的轮子
     */
    public static void initPickerValueHM(final AbPickerView mWheelViewHH, final AbPickerView pickerViewMM) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        initWheelPickerHM(mWheelViewHH,pickerViewMM,hour,minute);
    }

    /**
     * 默认的时分的时间选择器.
     *
     * @param pickerViewHH the m wheel view hh
     * @param pickerViewMM  选择分的轮子
     * @param defaultHour the default hour
     * @param defaultMinute the default minute
     */
    public static void initWheelPickerHM(final AbPickerView pickerViewHH, final AbPickerView pickerViewMM,
                                         int defaultHour, int defaultMinute){

        // 时
        final List<String> textHHList = new ArrayList<String>();
        for(int i=0;i<24;i++){
            textHHList.add(i+"点");
        }

        String currentHH = defaultHour+"点";
        int currentHourIndex = textHHList.indexOf(currentHH);
        pickerViewHH.setItems(textHHList);
        pickerViewHH.setInitPosition(currentHourIndex);
        // 分
        final List<String> textMMList = new ArrayList<String>();
        for(int i=0;i<60;i++){
            textMMList.add(i+"分");
        }
        String currentMM = defaultMinute+"分";
        int currentMinuteIndex = textMMList.indexOf(currentMM);
        pickerViewMM.setItems(textMMList);
        pickerViewMM.setInitPosition(currentMinuteIndex);

    }

    /**
     * 文本选择器.
     * @param pickerView
     * @param defaultIndex
     */
    public static void initWheelPickerText(final AbPickerView pickerView,List<String> textList,int defaultIndex){
        pickerView.setItems(textList);
        pickerView.setInitPosition(defaultIndex);
    }


}
