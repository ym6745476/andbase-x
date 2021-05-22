package com.andbase.library.view.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.andbase.library.R;
import com.andbase.library.utils.AbDateUtil;
import com.andbase.library.utils.AbImageUtil;
import com.andbase.library.utils.AbStrUtil;
import com.andbase.library.utils.AbViewUtil;
import com.andbase.library.view.listener.AbOnItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 15:26
 * Email 396196516@qq.com
 * Info 日历View
 */

public class AbCalendarView extends LinearLayout {

    /**
     * The context.
     */
    private Context context;

    /** 单选 */
    public boolean singleSelection;

    /** 显示星期几的头部. */
    public boolean showWeekHeader;

    /**
     * The layout params fw.
     */
    private LayoutParams layoutParamsFW = null;

    /**
     * The m linear layout header.
     */
    private LinearLayout linearLayoutHeader = null;

    /**
     * The m linear layout content.
     */
    private LinearLayout linearLayoutContent = null;

    /**
     * The m calendar header.
     */
    private AbCalendarHeader calendarHeader = null;

    /**
     * The width.
     */
    private int width = 320;

    /**
     * The height.
     */
    private int height = 480;

    /**
     * 星期头的行高.
     */
    private int headerHeight = 40;

    /**
     * 每个单元格的宽度.
     */
    private int cellWidth = 40;

    /**
     * 每个单元格的高度.
     */
    private int cellHeight = 40;

    /**
     * 这个日历的日期.
     */
    public static Calendar currentCalendar = Calendar.getInstance();

    /**
     * 今天.
     */
    private Calendar todayCalendar = Calendar.getInstance();

    /**
     * 累计日期.
     */
    private Calendar tempCalendar = Calendar.getInstance();

    /**
     * The current month.
     */
    private int currentMonth = 0;

    /**
     * The current year.
     */
    private int currentYear = 0;

    /**
     * 本日历的第一个单元格的星期.
     */
    private int firstDayOfWeek = Calendar.SUNDAY;

    /**
     * 当前显示的单元格.
     */
    private ArrayList<AbCalendarCell> calendarCells = new ArrayList<>();

    /**
     * The m on item click listener.
     */
    private AbOnItemClickListener onItemClickListener;

    public AbCalendarConfig calendarConfig;

    /** 不让日历响应点击事件 */
    private boolean editable = true;

    public Bitmap recordBitmap;

    public AbCalendarView(Context context) {
        this(context, null);
    }


    public AbCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AbCalendarView);
        try {
            int theme = typedArray.getInteger(R.styleable.AbCalendarView_ab_calendarTheme, 0);
            Drawable recordDrawable = typedArray.getDrawable(R.styleable.AbCalendarView_ab_calendarRecordImage);
            if(recordDrawable!= null){
                recordBitmap = AbImageUtil.drawableToBitmap(recordDrawable);
            }
            calendarConfig = AbCalendarConfig.getConfig(theme);
        } finally {
            typedArray.recycle();
        }

        layoutParamsFW = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackgroundColor(calendarConfig.calendarBackgroundColor);

        WindowManager wManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        headerHeight = (int) AbViewUtil.dip2px(context, headerHeight);
        linearLayoutHeader = new LinearLayout(context);
        linearLayoutHeader.setLayoutParams(new LayoutParams(width, headerHeight));
        linearLayoutHeader.setOrientation(LinearLayout.VERTICAL);
        calendarHeader = new AbCalendarHeader(context);
        calendarHeader.setLayoutParams(new LayoutParams(width, headerHeight));
        linearLayoutHeader.addView(calendarHeader);
        addView(linearLayoutHeader);
        if (showWeekHeader) {
            linearLayoutHeader.setVisibility(View.VISIBLE);
        }else{
            linearLayoutHeader.setVisibility(View.GONE);
        }

        linearLayoutContent = new LinearLayout(context);
        linearLayoutContent.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayoutContent);

        cellWidth = width / 7;
        cellHeight = cellWidth - 10;

        setCalendar(currentCalendar);

    }


    /**
     *
     */
    public void initRow() {
        linearLayoutContent.removeAllViews();
        calendarCells.clear();

        for (int iRow = 0; iRow < 6; iRow++) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setLayoutParams(new LayoutParams(width, cellHeight));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int iDay = 0; iDay < 7; iDay++) {
                AbCalendarCell dayCell = new AbCalendarCell(context, this, (iRow * 7) + iDay, cellWidth, cellHeight);
                dayCell.setOnItemClickListener(onDayCellClick);
                linearLayout.addView(dayCell);
                calendarCells.add(dayCell);
            }
            linearLayoutContent.addView(linearLayout);
        }
    }


    /**
     * 由于日历上的日期都是从周日开始的，计算第一个单元格的日期.
     */
    private void initStartDateForMonth() {
        //获取当前的
        currentYear = currentCalendar.get(Calendar.YEAR);
        currentMonth = currentCalendar.get(Calendar.MONTH);

        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentCalendar.set(Calendar.MINUTE, 0);
        currentCalendar.set(Calendar.SECOND, 0);

        int iDay = 0;
        int iStartDay = firstDayOfWeek;

        if (iStartDay == Calendar.MONDAY) {
            iDay = currentCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }

        if (iStartDay == Calendar.SUNDAY) {
            iDay = currentCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }

        currentCalendar.add(Calendar.DAY_OF_WEEK, -iDay);
    }

    /**
     * 更新日历.
     */
    private void updateCalendar() {

        tempCalendar.setTimeInMillis(currentCalendar.getTimeInMillis());
        for (int i = 0; i < calendarCells.size(); i++) {
            AbCalendarCell dayCell = calendarCells.get(i);
            //
            final int iYear = tempCalendar.get(Calendar.YEAR);
            final int iMonth = tempCalendar.get(Calendar.MONTH);
            final int iDay = tempCalendar.get(Calendar.DAY_OF_MONTH);
            final int iDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK);

            // 判断是否当天
            boolean isToday = false;
            // check holiday
            boolean isHoliday = false;

            if (todayCalendar.get(Calendar.YEAR) == iYear) {
                if (todayCalendar.get(Calendar.MONTH) == iMonth) {
                    if (todayCalendar.get(Calendar.DAY_OF_MONTH) == iDay) {
                        isToday = true;
                    }
                }
            }

            if ((iDayOfWeek == Calendar.SATURDAY) || (iDayOfWeek == Calendar.SUNDAY)) {
                isHoliday = true;
            }
            if ((iMonth == Calendar.JANUARY) && (iDay == 1)) {
                isHoliday = true;
            }

            //cell显示数据
            dayCell.setCellData(iYear, iMonth, iDay, isToday, false, isHoliday, currentMonth);

            tempCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

		/*int count = 0;
		for (int i = calendarCells.size()-1; i > 27; i--) {
			AbCalendarCell dayCell = calendarCells.get(i);
			if(currentCalendar.get(Calendar.MONTH) != dayCell.iDateMonth){
				count ++;
			}
		}
		if(count >= 7){
			linearLayoutContent.removeViewAt(linearLayoutContent.getChildCount()-1);
		}*/

        invalidate();
    }


    public void setOnItemClickListener(
            AbOnItemClickListener mAbOnItemClickListener) {
        this.onItemClickListener = mAbOnItemClickListener;
    }

    /**
     * 设置日历时间.
     * @param calendar the calendar
     */
    public void setCalendar(Calendar calendar) {
        currentCalendar.setTimeInMillis(calendar.getTimeInMillis());
        initRow();
        initStartDateForMonth();
        updateCalendar();
    }

    /**
     * 获取所有cell.
     */
    public ArrayList<AbCalendarCell> getCalendarCells() {
        return calendarCells;
    }


    /**
     * 设置默认选择的数据.
     */
    public void setSelectedDateList(List<String> selectedDateList) {

        for(AbCalendarCell cell:calendarCells){
            cell.setSelected(false);
            for(String date:selectedDateList){

                String regex = "/";
                if(date.indexOf("-")!=-1){
                    regex = "-";
                }
                String [] yearMonthDay = date.split(regex);

                if(cell.iDateYear == Integer.parseInt(yearMonthDay[0]) && cell.iDateMonth == Integer.parseInt(yearMonthDay[1])-1  && cell.iDateDay == Integer.parseInt(yearMonthDay[2])){
                    cell.setSelected(true);
                }
            }

        }
    }

    /**
     * 设置有记录的数据.
     */
    public void setRecordDateList(List<String> recordDateList) {

        for(AbCalendarCell cell:calendarCells){
            cell.setRecord(false);
            for(String date:recordDateList){

                String regex = "/";
                if(date.indexOf("-")!=-1){
                    regex = "-";
                }
                String [] yearMonthDay = date.split(regex);

                if(cell.iDateYear == Integer.parseInt(yearMonthDay[0]) && cell.iDateMonth == Integer.parseInt(yearMonthDay[1])-1  && cell.iDateDay == Integer.parseInt(yearMonthDay[2])){
                    cell.setRecord(true);
                }
            }

        }
    }

    /**
     * 设置关注的数据.
     */
    public void setAttendDateList(List<String> attendDateList) {

        for(AbCalendarCell cell:calendarCells){
            cell.setAttend(false);
            for(String date:attendDateList){

                String regex = "/";
                if(date.indexOf("-")!=-1){
                    regex = "-";
                }
                String [] yearMonthDay = date.split(regex);

                if(cell.iDateYear == Integer.parseInt(yearMonthDay[0]) && cell.iDateMonth == Integer.parseInt(yearMonthDay[1])-1  && cell.iDateDay == Integer.parseInt(yearMonthDay[2])){
                    cell.setAttend(true);
                }
            }

        }
    }

    /**
     * 设置提醒的数据.
     */
    public void setRemindDateList(List<String> remindDateList) {

        for(AbCalendarCell cell:calendarCells){
            cell.setRemind(false);
            for(String date:remindDateList){

                String regex = "/";
                if(date.indexOf("-")!=-1){
                    regex = "-";
                }
                String [] yearMonthDay = date.split(regex);

                if(cell.iDateYear == Integer.parseInt(yearMonthDay[0]) && cell.iDateMonth == Integer.parseInt(yearMonthDay[1])-1  && cell.iDateDay == Integer.parseInt(yearMonthDay[2])){
                    cell.setRemind(true);
                }
            }

        }
    }

    /**
     * 设置重要的数据.
     */
    public void setImportantDateList(List<String> importantDateList) {

        for(AbCalendarCell cell:calendarCells){
            cell.setImportant(false);
            for(String date:importantDateList){

                String regex = "/";
                if(date.indexOf("-")!=-1){
                    regex = "-";
                }
                String [] yearMonthDay = date.split(regex);

                if(cell.iDateYear == Integer.parseInt(yearMonthDay[0]) && cell.iDateMonth == Integer.parseInt(yearMonthDay[1])-1  && cell.iDateDay == Integer.parseInt(yearMonthDay[2])){
                    cell.setImportant(true);
                }
            }

        }
    }


    /**
     * 获取当前选择的数据.
     */
    public List<String>  getSelectedDateList() {
        List<String> selectedDateList = new ArrayList<>();
        for(AbCalendarCell cell:calendarCells){
            if(cell.isSelected()){
                selectedDateList.add(AbDateUtil.getStringByFormat(cell.getCellDate().getTime(),AbDateUtil.dateFormatYMD));
            }

        }
        return selectedDateList;
    }


    /**
     * 点击日历，触发事件.
     */
    private AbOnItemClickListener onDayCellClick = new AbOnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {

            AbCalendarCell calendarCell = calendarCells.get(position);
            if (calendarCell.isActiveMonth() && editable && calendarCell.isEditable()) {
                currentCalendar.setTimeInMillis(calendarCell.getCellDate().getTimeInMillis());

                if(singleSelection){
                    for (int i = 0; i < calendarCells.size(); i++) {
                        AbCalendarCell calendarCellOther = calendarCells.get(i);
                        calendarCellOther.setSelected(false);
                    }
                }

                calendarCell.setSelected(!calendarCell.isSelected());
            }

            if (onItemClickListener != null && calendarCell.isEditable()) {
                onItemClickListener.onItemClick(null, position);
            }
        }

    };


    /**
     * 根据索引获取选择的日期.
     * @param position the position
     * @return the str date at position
     */
    public String getDate(int position) {
        AbCalendarCell mCalendarCell = calendarCells.get(position);
        Calendar calendar = mCalendarCell.getCellDate();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return year + "-" + AbStrUtil.strFormat2(String.valueOf(month)) + "-" + AbStrUtil.strFormat2(String.valueOf(day));
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    public void setSingleSelection(boolean singleSelection) {
        this.singleSelection = singleSelection;
    }

    public boolean isShowWeekHeader() {
        return showWeekHeader;
    }

    public void setShowWeekHeader(boolean showWeekHeader) {
        this.showWeekHeader = showWeekHeader;
        if (showWeekHeader) {
            linearLayoutHeader.setVisibility(View.VISIBLE);
        }else{
            linearLayoutHeader.setVisibility(View.GONE);
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}

