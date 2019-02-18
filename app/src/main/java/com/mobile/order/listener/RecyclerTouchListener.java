package com.mobile.order.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

private RecyclerTouchListener.ClickListener clicklistener;
private GestureDetector gestureDetector;

public RecyclerTouchListener(Context context, final RecyclerView recycleView, final RecyclerTouchListener.ClickListener clicklistener){

        this.clicklistener=clicklistener;
        gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
@Override
public boolean onSingleTapUp(MotionEvent e) {
        return true;
        }

@Override
public void onLongPress(MotionEvent e) {
        View child=recycleView.findChildViewUnder(e.getX(),e.getY());
        if(child!=null && clicklistener!=null){
        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
        }
        }
        });
        }

@Override
public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child=rv.findChildViewUnder(e.getX(),e.getY());
        if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
        clicklistener.onClick(child,rv.getChildAdapterPosition(child));
        }

        return false;
        }

@Override
public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

@Override
public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }
        }
