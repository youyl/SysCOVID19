package com.java.youyilin.ui.news;
import android.media.Image;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.java.youyilin.R;
import com.mob.MobSDK;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class NewsDetail extends AppCompatActivity
{

    private String title;
    private String content;
    private String source;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        setTitle("阅读正文");
        title=getIntent().getStringExtra("TITLE");
        content=new StringBuilder().append("    ").append(getIntent().getStringExtra("CONTENT")).toString();
        source=getIntent().getStringExtra("SOURCE");
        date=getIntent().getStringExtra("DATE");

        ImageButton share=findViewById(R.id.share_btn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // 关闭提示
                oks.setDisappearShareToast(true);

                oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                    @Override
                    public void onShare(Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {
                        //微博分享链接和图文
                        if ("SinaWeibo".equals(platform.getName())) {
                            paramsToShare.setText("[ SysCOVID19: " + title + " ]\n" + "http://xlore.org/instance/eni27782" + "(分享自@SysCOVID19)");
                            Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.create);
                            paramsToShare.setImageData(imageData);
                        }
                        /*微信好友分享网页
                        if ("Wechat".equals(platform.getName())) {
                            paramsToShare.setTitle("标题");
                            paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                            paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                            paramsToShare.setUrl("http://sharesdk.cn");
                            paramsToShare.setShareType(Platform.SHARE_WEBPAGE);
                            Log.d("ShareSDK", paramsToShare.toMap().toString());
                        }
                         */
                        /*微信朋友圈分享图片
                        if ("WechatMoments".equals(platform.getName())) {
                            paramsToShare.setTitle("标题");
                            paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                            //Bitmap imageData = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                            //paramsToShare.setImageData(imageData);
                            paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                            paramsToShare.setShareType(Platform.SHARE_IMAGE);
                            Log.d("ShareSDK", paramsToShare.toMap().toString());
                        }
                         */
                        /*QQ分享链接
                        if ("QQ".equals(platform.getName())) {
                            paramsToShare.setTitle("标题");
                            paramsToShare.setTitleUrl("http://sharesdk.cn");
                            paramsToShare.setText("我是共用的参数，这几个平台都有text参数要求，提取出来啦");
                            paramsToShare.setImageUrl("https://hmls.hfbank.com.cn/hfapp-api/9.png");
                            Log.d("ShareSDK", paramsToShare.toMap().toString());
                        }
                         */
                    }
                });
                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                        Toast.makeText(NewsDetail.this, "分享成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {
                        Toast.makeText(NewsDetail.this, "分享失败", Toast.LENGTH_SHORT).show();
                        throwable.getMessage();
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onCancel(Platform platform, int i) {
                        Toast.makeText(NewsDetail.this, "分享已取消", Toast.LENGTH_SHORT).show();
                    }
                });
                // 启动分享GUI
                oks.show(MobSDK.getContext());
            }
        });

        TextView titleview = findViewById(R.id.news_detail_title);
        TextView sourceview = findViewById(R.id.news_detail_source);
        TextView dateview = findViewById(R.id.news_detail_date);
        TextView contentview = findViewById(R.id.news_detail_content);
        titleview.setText(title);
        sourceview.setText(source);
        dateview.setText(date);
        contentview.setText(content);
    }
}