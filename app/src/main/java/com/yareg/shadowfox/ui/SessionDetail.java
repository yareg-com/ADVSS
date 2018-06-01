package com.yareg.shadowfox.ui;

import android.animation.Animator;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.yareg.shadowfox.R;
import com.yareg.shadowfox.core.TrafficSessionManager;
import com.yareg.shadowfox.util.SessionContent;
import com.yareg.shadowfox.util.SessionDetailAdapter;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SessionDetail extends Activity {

    private SessionDetailAdapter adapter;

    @BindView(R.id.loading)     View         loadingView;
    @BindView(R.id.packet_list) RecyclerView packetList;
    @BindView(R.id.scroller)    FastScroller fastScroller;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_session_detail);
        
        Bundle bundle = this.getIntent().getExtras();
        int port = bundle.getInt("port");
        final SessionContent content = TrafficSessionManager.getByPort(port);
        
        TextView textView = findViewById(R.id.session_overview);
        String overview = content.getDomain() + ":" + content.getLocalPort() + "\n"
                + "RX: " + content.getBytesReceived() + ", TX: " + content.getBytesSent();
        textView.setText(overview);
    
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        packetList.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        packetList.setItemAnimator(new DefaultItemAnimator());
    
        Observable<List<SessionContent>> observable = Observable.create(new ObservableOnSubscribe<List<SessionContent>>() {
            @Override
            public void subscribe(ObservableEmitter<List<SessionContent>> emitter) throws Exception {
                adapter = new SessionDetailAdapter(content);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    
        Observer<List<SessionContent>> observer = new Observer<List<SessionContent>>() {
            @Override
            public void onSubscribe(Disposable d) { }
        
            @Override
            public void onNext(List<SessionContent> value) { }
        
            @Override
            public void onError(Throwable e) { }
        
            @Override
            public void onComplete() {
                packetList.setAdapter(adapter);
                fastScroller.setRecyclerView(packetList);
            
            
                long animTime = 1;
            
                packetList.setAlpha(0);
                packetList.setVisibility(View.VISIBLE);
                packetList.animate().alpha(1).setDuration(animTime);
            
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
