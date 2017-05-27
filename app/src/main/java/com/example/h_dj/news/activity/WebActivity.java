package com.example.h_dj.news.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.h_dj.news.R;
import com.example.h_dj.news.bean.CommentBean;
import com.example.h_dj.news.utils.LogUtil;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by H_DJ on 2017/5/17.
 */

public class WebActivity extends BaseActivity {
    @BindView(R.id.web)
    WebView mWeb;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.progress)
    ProgressBar mProgress;
    @BindView(R.id.comment)
    EditText mComment;
    @BindView(R.id.send)
    Button mSend;


    private String url;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void init() {
        super.init();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle data = intent.getBundleExtra("data");
            if (data != null && !data.isEmpty()) {
                url = data.getString("url");
                mWeb.loadUrl(url);
            }
        }
        initProgress();
        initToolbar();
        initWeb();
    }

    /**
     * 初始化进度
     */
    private void initProgress() {
        mProgress.setMax(100);
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.web_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWeb.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWeb.getSettings().setJavaScriptEnabled(false);
    }

    /**
     * 初始化webView
     */
    private void initWeb() {
        mWeb.getSettings().setUseWideViewPort(true); //将图片调整到适合webview的大小
        mWeb.getSettings().setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        mWeb.getSettings().setLoadsImagesAutomatically(true); //支持自动加载图片
        mWeb.getSettings().setDefaultTextEncodingName("utf-8");//设置编码格式

        mWeb.setWebViewClient(new WebViewClient() {
            //防止查看网页时，调用系统浏览器或qit
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals(url)) {
                    // This is my web site, so do not override; let my WebView load the page
                    return false;
                }
                // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                goTo(WebActivity.class, bundle);
                return true;
            }

        });

        mWeb.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView webView, int i) {
                if (mProgress == null) {
                    return;
                }
                if (i <= 100) {
                    LogUtil.e("加载进度：" + i);
                    mProgress.setVisibility(View.VISIBLE);
                    mProgress.setProgress(i);
                } else {
                    mProgress.setVisibility(View.GONE);
                }

            }
        });

    }


    @Override
    protected void onDestroy() {
        if (mWeb != null) {
            //清除网页访问留下的缓存
            //由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            mWeb.clearCache(true);

            //清除当前webview访问的历史记录
            //只会webview访问历史记录里的所有记录除了当前访问记录
            mWeb.clearHistory();
        }
        super.onDestroy();
    }

    @OnClick(R.id.send)
    public void onViewClicked() {
        if (checkLogin()) {
            String comment = mComment.getText().toString().trim();
            if (TextUtils.isEmpty(comment)) {
                Toast.makeText(WebActivity.this, "还没填写评论", Toast.LENGTH_SHORT).show();
                return;
            }
            sendComment(comment);
        } else {
            goTo(LoginActivity.class);
        }
    }

    /**
     * 发送评论
     *
     * @param comment 评论内容
     */
    private void sendComment(String comment) {
        CommentBean comments = new CommentBean();
        //注意：不能调用gameScore.setObjectId("")方法
        comments.setUserId(mUser.getObjectId());
        comments.setCommentContent(comment);
        comments.setCommentUrl(mWeb.getOriginalUrl());
        comments.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Toast.makeText(WebActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    Toast.makeText(WebActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
