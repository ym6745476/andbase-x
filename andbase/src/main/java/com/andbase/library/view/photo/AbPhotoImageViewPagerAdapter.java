package com.andbase.library.view.photo;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.andbase.library.utils.AbStrUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.LinkedList;
import java.util.List;


/**
 * Copyright ymbok.com
 * Author 还如一梦中
 * Date 2016/6/16 13:27
 * Email 396196516@qq.com
 * Info ViewPager适配器
 */
public class AbPhotoImageViewPagerAdapter<T> extends PagerAdapter{

	/** 上下文. */
	private Context context;

	/** View列表. */
	final LinkedList<AbPhotoImageView> viewCache = new LinkedList<AbPhotoImageView>();

	private List<T> list = null;

	private AbPhotoImageViewPager photoImageViewPager = null;

	private RequestManager glide;


	/**
	 * 构造函数.
	 * @param context
	 * @param list
	 */
	public AbPhotoImageViewPagerAdapter(Context context, AbPhotoImageViewPager photoImageViewPager, List<T>  list) {
		this.context = context;
		this.list = list;
		this.photoImageViewPager = photoImageViewPager;
		this.glide = Glide.with(context);
	}

	/**
	 * 获取数量.
	 * @return the count
	 */
	@Override
	public int getCount() {
		return this.list.size();
	}

	/**
	 * Object是否对应这个View.
	 * @param view the arg0
	 * @param obj the arg1
	 * @return true, if is view from object
	 */
	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == (obj);
	}

	/**
	 * 显示View.
	 * @param container the container
	 * @param position the position
	 * @return the object
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		final AbPhotoImageView photoImageView;
		if (viewCache.size() > 0) {
			photoImageView = viewCache.remove();
			photoImageView.setImageBitmap(null);
			photoImageView.reset();
		} else {
			photoImageView = new AbPhotoImageView(context);
		}
		loadImage(photoImageView,this.list.get(position));

		container.addView(photoImageView);
		return photoImageView;
	}

	/**
	 * 移除View.
	 * @param container the container
	 * @param position the position
	 * @param object the object
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		AbPhotoImageView photoImageView = (AbPhotoImageView) object;
		container.removeView(photoImageView);
		viewCache.add(photoImageView);
	}
	
	/**
	 * 很重要，否则不能notifyDataSetChanged.
	 * @param object the object
	 * @return the item position
	 */
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}


	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		final AbPhotoImageView photoImageView = (AbPhotoImageView) object;
		this.photoImageViewPager.setMainImageView(photoImageView);
		loadImage(photoImageView,this.list.get(position));
	}


	public void  loadImage(final AbPhotoImageView photoImageView,final T image){
		if(image instanceof  String){
			String urlPath = (String)image;
			if(!AbStrUtil.isEmpty(urlPath)) {
				if (urlPath.indexOf("http://") != -1 || urlPath.indexOf("https://") != -1 || urlPath.indexOf("www.") != -1) {
					glide.load(urlPath).into(photoImageView);
				} else if (AbStrUtil.isNumber(urlPath)) {
					try {
						int res = Integer.parseInt(urlPath);
						photoImageView.setImageDrawable(context.getResources().getDrawable(res));
					} catch (Exception e) {
					}
				} else {
					glide.applyDefaultRequestOptions(new RequestOptions().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)).load(new File(urlPath)).into(photoImageView);
				}
			}
		}else if(image instanceof Bitmap){
			try {
				photoImageView.setImageBitmap((Bitmap)image);
			} catch (Exception e) {
			}
		}

	}
}
