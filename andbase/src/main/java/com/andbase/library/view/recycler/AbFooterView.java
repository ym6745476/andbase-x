package com.andbase.library.view.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.utils.AbColorUtil;
import com.andbase.library.view.sample.AbWaveView;


public class AbFooterView extends LinearLayout {

    private Context context;
    private TextView messageText;
    private FrameLayout loadingView;
    private int status = 1;

    /** 准备状态 */
    public final static int STATE_READY = 1;

    /** 加载状态 */
    public final static int STATE_LOADING = 2;

    /** 加载完成 */
    public final static int STATE_FINISH = 3;

    /** 没有更多了 */
    public final static int STATE_NO_MORE = 4;

    /** 加载失败了 */
    public final static int STATE_FAIL = 5;

    /** 文字 */
    public String[] loadMessages = new String[]{"加载更多","正在加载...","加载完成","到底了","加载失败"};


    public AbFooterView(Context context) {
        super(context);
        init(context);
    }

    public AbFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context){

        this.context = context;
        this.setOrientation(LinearLayout.HORIZONTAL);
        View footView = View.inflate(context, R.layout.ab_item_more_footer, null);
        this.addView(footView,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        messageText = (TextView)footView.findViewById(R.id.load_message);
        loadingView = (FrameLayout) footView.findViewById(R.id.loading_view);
        messageText.setText(loadMessages[0]);
        loadingView.setVisibility(View.GONE);
        //loading  view
        setLoadingView(null);

    }

    public void setStatus(int status){
        this.status = status;
        if(status == STATE_READY){
            messageText.setText(loadMessages[0]);
            loadingView.setVisibility(View.GONE);
        }else if(status == STATE_LOADING){
            messageText.setText(loadMessages[1]);
            loadingView.setVisibility(View.VISIBLE);
        }else if(status == STATE_FINISH){
            messageText.setText(loadMessages[2]);
            loadingView.setVisibility(View.GONE);
        }else if(status == STATE_NO_MORE){
            messageText.setText(loadMessages[3]);
            loadingView.setVisibility(View.GONE);
        }else if(status == STATE_FAIL){
            messageText.setText(loadMessages[4]);
            loadingView.setVisibility(View.GONE);
        }
    }

    public int getStatus() {
        return status;
    }

    public View getLoadingView() {
        return loadingView;
    }

    public void setLoadingView(View view) {
        if(view == null){
            int borderColor = AbColorUtil.getAttrColor(context,R.attr.colorAccent);
            int waveColor = AbColorUtil.getAttrColor(context,R.attr.colorAccent);
            int borderWidth = 1;
            AbWaveView waveView = new AbWaveView(context);
            waveView.setBorder(borderWidth, borderColor);
            waveView.setWaveColor(waveColor,waveColor);
            //waveView.setShapeType(AbWaveView.ShapeType.SQUARE);
            waveView.setShapeType(AbWaveView.ShapeType.CIRCLE);
            waveView.start(100);
            this.loadingView.addView(waveView);
        }else{
            this.loadingView.addView(view);
        }
    }

    public String[] getLoadMessages() {
        return loadMessages;
    }

    public void setLoadMessages(String[] loadMessages) {
        this.loadMessages = loadMessages;
    }
}
