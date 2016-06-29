package com.tocet.championlam.android_lib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.tocet.championlam.mylibs.base.BaseActivity;
import com.tocet.championlam.mylibs.widget.ScrollImageContainer;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * MainActivity for demo
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {


    @ViewInject(R.id.lv_menu)
    private ListView lvMenu;
    @ViewInject(R.id.sic_images)
    private ScrollImageContainer sicImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);

        initView();
        initData();
        initEvent();

    }

    /**
     * init event
     */
    private void initEvent() {

    }

    /**
     * init data
     */
    private void initData() {
        String[] images = new String[]{
                "http://g.hiphotos.baidu.com/image/pic/item/ac4bd11373f08202449f681f4efbfbedab641b84.jpg",
                "http://d.hiphotos.baidu.com/image/pic/item/77c6a7efce1b9d1646c87e7ef1deb48f8c5464a6.jpg",
                "http://g.hiphotos.baidu.com/image/pic/item/32fa828ba61ea8d3935d0305950a304e251f58b7.jpg"
        };

        sicImages.setImageData(images, null, true);
        sicImages.setAutoLoop(5000,false);
    }

    /**
     * init view
     */
    private void initView() {
        //use inject , so u can't write findviewbyid
    }
}
