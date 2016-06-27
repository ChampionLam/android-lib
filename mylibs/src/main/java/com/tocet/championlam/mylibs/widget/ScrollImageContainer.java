package com.tocet.championlam.mylibs.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tocet.championlam.mylibs.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scroll Images like banner
 */
public class ScrollImageContainer extends FrameLayout {

    private Context mContext;
    private LayoutInflater mInflater;
    private ViewPager vpContainer;
    private View mContentView;
    private RelativeLayout rlIdentification;
    private int currIndex;
    private ScheduledExecutorService scheduledExecutorService;


    private ImageOptions mImageOptions = new ImageOptions.Builder()
            .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
            .setLoadingDrawableId(R.drawable.icon_default_img)//加载中默认显示图片
            .setFailureDrawableId(R.drawable.icon_default_img)//加载失败后默认显示图片
            .build();


    public ScrollImageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        init();
    }

    private void init() {
        mContentView = mInflater.inflate(R.layout.view_scrollimagecontainer, null);
        vpContainer = (ViewPager) mContentView.findViewById(R.id.vp_container);
        rlIdentification = (RelativeLayout) mContentView.findViewById(R.id.rl_identification);
        this.addView(mContentView);
    }

    public void setImageData(String[] images, ImageOptions imageOptions, boolean IsLoop, boolean IsAutoLoop, int interval) {
        this.mImageOptions = imageOptions != null ? imageOptions : this.mImageOptions;
        vpContainer.setAdapter(new viewPagerAdapter(images, IsLoop));
        initViewPager(images.length,IsLoop,IsAutoLoop,interval);
    }

    private void initViewPager(int length, boolean isLoop, boolean isAutoLoop, int interval) {

        if (isLoop)
            vpContainer.setCurrentItem(Integer.MAX_VALUE / 2 / length * length);
        if (isAutoLoop) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new changeTask(), interval, interval, TimeUnit.SECONDS);
        }
        vpContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currIndex = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class changeTask implements Runnable {

        @Override
        public void run() {
            currIndex = currIndex + 1;
            handler.sendEmptyMessage(CHANGE_IMAGE);
        }
    }

    public void setImageData(Bitmap[] images) {
        for (int i = 0; i < images.length; i++) {
            ImageView iv = new ImageView(this.mContext);
            iv.setImageBitmap(images[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        //vpContainer.setAdapter(new viewPagerAdapter());
    }

    class viewPagerAdapter extends PagerAdapter {

        private String[] mImages;
        private boolean IsLoop = true;

        public viewPagerAdapter(String[] images, boolean isLoop) {
            this.mImages = images;
            this.IsLoop = isLoop;
        }

        @Override
        public int getCount() {
            if (IsLoop) {
                if (this.mImages.length > 1)
                    return Integer.MAX_VALUE;
            }
            return this.mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            int index = position;
            if (IsLoop)
                index = position % this.mImages.length;
            ImageView iv = new ImageView(mContext);
            x.image().bind(iv, this.mImages[index], mImageOptions);
            container.addView(iv);
            return iv;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private final static int CHANGE_IMAGE = 0X110;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHANGE_IMAGE:
                    vpContainer.setCurrentItem(currIndex);
                    break;
            }
        }
    };

}
