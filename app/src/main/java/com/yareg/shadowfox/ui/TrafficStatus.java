package com.yareg.shadowfox.ui;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.yareg.shadowfox.R;
import com.yareg.shadowfox.core.TrafficSessionManager;
import com.yareg.shadowfox.util.SessionContent;
import com.yareg.shadowfox.util.TrafficStatusAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TrafficStatus extends Activity {

    public static TrafficStatus Instance;

    private TrafficStatusAdapter adapter;

    @BindView(R.id.loading)      View         loadingView;
    @BindView(R.id.session_list) RecyclerView sessionList;
    @BindView(R.id.scroller)     FastScroller fastScroller;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_traffic_status);
        ButterKnife.bind(this);
    
        Instance = this;
    
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        
        TextView overview = (TextView) findViewById(R.id.traffic_overview);
        String overviewContent = "RX：" + TrafficSessionManager.OriginBytesSent + " B, TX：" + TrafficSessionManager.OriginBytesReceived + " B\n" +
                "RX：" + TrafficSessionManager.EncryptedBytesSent + " B, TX：" + TrafficSessionManager.EncryptedBytesReceived + " B\n" +
                "RX：" + TrafficSessionManager.VideoBytesSent + " B, TX：" + TrafficSessionManager.VideoBytesReceived + " B";
        overview.setText(overviewContent);
        
        sessionList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        sessionList.setItemAnimator(new DefaultItemAnimator());
    
        Observable<List<SessionContent>> observable = Observable.create(new ObservableOnSubscribe<List<SessionContent>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SessionContent>> emitter) throws Exception {
                adapter = new TrafficStatusAdapter();
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        
        Observer<List<SessionContent>> observer = new Observer<List<SessionContent>>() {
            @Override
            public void onSubscribe(Disposable d) { }
    
            @Override
            public void onNext(List<SessionContent> value) { }
    
            @Override
            public void onError(Throwable e) { }
    
            @Override
            public void onComplete() {
                sessionList.setAdapter(adapter);
                fastScroller.setRecyclerView(sessionList);
                
                long animTime = 2;
                
                sessionList.setAlpha(0);
                sessionList.setVisibility(View.VISIBLE);
                sessionList.animate().alpha(1).setDuration(animTime);
                
                loadingView.animate().alpha(0).setDuration(animTime).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) { }
    
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingView.setVisibility(View.GONE);
                    }
    
                    @Override
                    public void onAnimationCancel(Animator animation) { }
    
                    @Override
                    public void onAnimationRepeat(Animator animation) { }
                });
            }
        };
        
        observable.subscribe(observer);
     }
     
    public void startNewActivity(Intent intent) {
        startActivity(intent);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
