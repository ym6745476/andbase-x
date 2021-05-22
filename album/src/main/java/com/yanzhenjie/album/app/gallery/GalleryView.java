/*
 * Copyright 2018 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.album.app.gallery;

import android.app.Activity;
import android.content.res.ColorStateList;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.album.Album;
import com.yanzhenjie.album.AlbumFile;
import com.yanzhenjie.album.R;
import com.yanzhenjie.album.api.widget.Widget;
import com.yanzhenjie.album.app.Contract;

import java.util.List;

/**
 * Created by YanZhenjie on 2018/4/9.
 */
public class GalleryView<Data> extends Contract.GalleryView<Data> implements View.OnClickListener {

    private Activity mActivity;

    private MenuItem mCompleteMenu;

    private ViewPager mViewPager;
    private RelativeLayout mLayoutBottom;
    private TextView mTvDuration;
    private AppCompatCheckBox mCheckBox;
    private FrameLayout mLayoutLayer;

    public GalleryView(Activity activity, Contract.GalleryPresenter presenter) {
        super(activity, presenter);
        this.mActivity = activity;
        this.mViewPager = activity.findViewById(R.id.view_pager);
        this.mLayoutBottom = activity.findViewById(R.id.layout_bottom);
        this.mTvDuration = activity.findViewById(R.id.tv_duration);
        this.mCheckBox = activity.findViewById(R.id.check_box);
        this.mLayoutLayer = activity.findViewById(R.id.layout_layer);

        this.mCheckBox.setOnClickListener(this);
        this.mLayoutLayer.setOnClickListener(this);
    }

    @Override
    protected void onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu_gallery, menu);
        mCompleteMenu = menu.findItem(R.id.album_menu_finish);
    }

    @Override
    protected void onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.album_menu_finish) {
            getPresenter().complete();
        }
    }

    @Override
    public void setupViews(Widget widget, boolean checkable) {

        setHomeAsUpIndicator(R.drawable.ic_action_back_white);
        if (!checkable) {
            mCompleteMenu.setVisible(false);
            mCheckBox.setVisibility(View.GONE);
        } else {
            ColorStateList itemSelector = widget.getMediaItemCheckSelector();
            mCheckBox.setTextColor(itemSelector);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getPresenter().onCurrentChanged(position);
            }
        });
    }

    @Override
    public void bindData(List<Data> dataList) {
        PreviewAdapter<Data> adapter = new PreviewAdapter<Data>(getContext(), dataList) {
            @Override
            protected void loadPreview(ImageView imageView, Data item, int position) {
                if (item instanceof String) {
                    Album.getAlbumConfig().getAlbumLoader().load(imageView, (String)item);
                } else if (item instanceof AlbumFile) {
                    Album.getAlbumConfig().getAlbumLoader().load(imageView, (AlbumFile)item);
                }
            }
        };
        adapter.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().clickItem(mViewPager.getCurrentItem());
            }
        });
        adapter.setItemLongClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().longClickItem(mViewPager.getCurrentItem());
            }
        });
        if (adapter.getCount() > 3) {
            mViewPager.setOffscreenPageLimit(3);
        } else if (adapter.getCount() > 2) {
            mViewPager.setOffscreenPageLimit(2);
        }
        mViewPager.setAdapter(adapter);
    }

    @Override
    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void setDurationDisplay(boolean display) {
        mTvDuration.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setDuration(String duration) {
        mTvDuration.setText(duration);
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    @Override
    public void setBottomDisplay(boolean display) {
        mLayoutBottom.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setLayerDisplay(boolean display) {
        mLayoutLayer.setVisibility(display ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCompleteText(String text) {
        mCompleteMenu.setTitle(text);
    }

    @Override
    public void onClick(View v) {
        if (v == mCheckBox) {
            getPresenter().onCheckedChanged();
        } else if (v == mLayoutLayer) {
            // Intercept click events.
        }
    }
}