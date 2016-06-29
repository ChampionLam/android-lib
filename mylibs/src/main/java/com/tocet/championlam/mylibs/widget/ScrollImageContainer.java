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
import android.widget.Toast;

import com.tocet.championlam.mylibs.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.LinkedList;
import java.util.List;
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
    private LinkedList<ImageView> mViews;
    private boolean IsAutoLoop = false;
    private int mInterval = 5000;
    private loopHandler mlHandler;
    private boolean mIsStartToRight = true;
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

    public void setImageData(String[] images, ImageOptions imageOptions, boolean IsLoop) {
        this.mImageOptions = imageOptions != null ? imageOptions : this.mImageOptions;
        if (IsLoop) {
            vpContainer.setAdapter(new loopAdapter(images));
            vpContainer.setCurrentItem(1);
        }else{
            vpContainer.setAdapter(new viewPagerAdapter(images));
            vpContainer.setCurrentItem(0);
        }
        initViewPager(images.length, IsLoop);
    }

    public void setAutoLoop(int interval, boolean IsStartToRight) {
        this.mInterval = interval;
        mIsStartToRight = IsStartToRight;
        mlHandler = new loopHandler();
        mlHandler.startLoop();
    }

    private void initViewPager(int length, final boolean isLoop) {

        vpContainer.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do noting
            }

            @Override
            public void onPageSelected(int position) {

                currIndex = position;
                if (isLoop) {
                    if (position == 0) {
                        position = mViews.size() - 2;
                    } else if (position == mViews.size() - 1) {
                        position = 1;
                    }
                    vpContainer.setCurrentItem(position, false);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do noting
            }
        });
    }

    class viewPagerAdapter extends PagerAdapter {

        private String[] mImages;

        public viewPagerAdapter(String[] images) {
            this.mImages = images;
        }

        @Override
        public int getCount() {
            return this.mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = new ImageView(mContext);
            x.image().bind(iv, this.mImages[position], mImageOptions);
            container.addView(iv);
            return iv;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public ViewPager getVierPager(){
        return vpContainer;
    }


    class loopAdapter extends PagerAdapter {


        public loopAdapter(String[] images) {
            mViews = new LinkedList<ImageView>();
            if (images != null && images.length > 1) {
                //add first imageview
                ImageView iv_head = new ImageView(mContext);
                x.image().bind(iv_head, images[images.length - 1], mImageOptions);
                mViews.add(iv_head);

                //add mid
                for (String url : images) {
                    ImageView iv = new ImageView(mContext);
                    x.image().bind(iv, url, mImageOptions);
                    mViews.add(iv);
                }

                //add last imageview
                ImageView iv_last = new ImageView(mContext);
                x.image().bind(iv_last, images[0], mImageOptions);
                mViews.add(iv_last);
            } else if (images.length == 1) {
                //if length = 1
                ImageView iv = new ImageView(mContext);
                x.image().bind(iv, images[0], mImageOptions);
                mViews.add(iv);
            }
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = mViews.get(position);
            container.addView(iv);
            return iv;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class loopHandler extends Handler {

        public boolean pause = false;

        @Override
        public void handleMessage(Message msg) {
            if (!pause) {
                if(mIsStartToRight)
                    vpContainer.setCurrentItem(currIndex + 1);
                else
                    vpContainer.setCurrentItem(currIndex - 1);
            }
            sendEmptyMessageDelayed(msg.what, mInterval);
        }

        void startLoop() {
            pause = false;
            removeCallbacksAndMessages(null);
            sendEmptyMessageDelayed(1, mInterval);
        }

        void stopLoop() {
            removeCallbacksAndMessages(null);
        }
    }

}
