package com.esc.printLocation;

import java.util.ArrayList;
import java.util.Collection;

import com.esc.R;
import com.esc.getDiscountInfo.GreenDialog;
import com.esc.getDiscountInfo.RedDialog;
import com.esc.getDiscountInfo.WhiteDialog;
import com.esc.getDiscountInfo.YellowDialog;
import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECORangingListener;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class MapFragment extends Fragment implements RECORangingListener {
	
	/** inflater�� fragment���̾ƿ� ���� **/
	private View mView;
	
	/** ������ ������ ���� ��ü **/
	private RECOBeaconManager mBeaconManager;
	private BeaconHelper mBeaconHelper;
//	private PositionCalculation mPositionCalculation;
	
	/** ���� �ȵ���̵� X, Y �� **/
	private double NowPosition_X;
	private double NowPosition_Y;
	
	/** ���� �� ������ �ȵ���̵� X, Y �� **/
	private double RevisionPosition_X;
	private double RevisionPosition_Y;
	private double RevisionPosition_Z;
	
	/** �ִ� ���̿� ���� ���� **/
	private double maxWidth;
	private double maxHeight;

	/** �� ���� �������� **/
	private AbsoluteLayout mapLayout;		//���� ���̾ƿ�
	private ImageView mMarker;				//��Ŀ �׸�
	private float screenWidth = 0;			// mImage �� ���� ����
	private float screenHeight = 0;			// mImage �� ���� ����
	
	// ������ �ѹ��� ����� �� �ְԲ� �ϴ� ����
	private static int greenCnt = 0, yellowCnt = 0, redCnt = 0, whiteCnt = 0 ;
	
	/** �ִϸ��̼� �������� **/
	private TranslateAnimation animation;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.fragment_map, container, false);
		
		mBeaconHelper = new BeaconHelper(getActivity().getApplicationContext());
		mBeaconManager = mBeaconHelper.getBeaconManager();
		mBeaconManager.setRangingListener(this);
		
//		mPositionCalculation = new PositionCalculation();
		mapLayout = (AbsoluteLayout)mView.findViewById(R.id.AbsoluteLayout1);
		mMarker = (ImageView)mView.findViewById(R.id.iv_productpicture);
		
		drawMaker();
		
		return mView;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mBeaconHelper = new BeaconHelper(getActivity().getApplicationContext());
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mBeaconHelper.stopAndUnbind();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mBeaconHelper.stopAndUnbind();
	}
	
	@Override
	public void didRangeBeaconsInRegion(Collection<RECOBeacon> beacons,
			RECOBeaconRegion regions) {
		// TODO Auto-generated method stub
//		Log.i("NavigationActivity",
//				"didRangeBeaconsInRegion() region: "
//						+ regions.getUniqueIdentifier()
//						+ ", number of beacons ranged: " + beacons.size());
//		
//		NowPosition_X = mPositionCalculation.getAndroidPosition_X();
//		NowPosition_Y = mPositionCalculation.getAndroidPosition_Y();
//		
//		/** Į�� ���� �� **/
//		for(int revisionCnt=0 ; revisionCnt<5 ; revisionCnt++) {
//			mPositionCalculation.setAndroidPosition(mBeaconHelper.getBeaconInfo(beacons));
//			mPositionCalculation.setCalmanRevision(revisionCnt);
//		}
//		
//		RevisionPosition_X = mPositionCalculation.getAndroidPosition_X();
//		RevisionPosition_Y = mPositionCalculation.getAndroidPosition_Y();
//		RevisionPosition_Z = mPositionCalculation.getAndroidPosition_Z();
//		maxWidth = mPositionCalculation.getDistanceGY();
//		maxHeight = mPositionCalculation.getDistanceGR();
//		
//		if ((RevisionPosition_X > 0 && RevisionPosition_X <= maxWidth)
//				&& (RevisionPosition_Y > 0 && RevisionPosition_Y <= maxHeight)) {
//			moveImage((float)(screenWidth / maxWidth * NowPosition_X), (float)(screenWidth / maxWidth * RevisionPosition_X),
//					(float)(screenHeight / maxHeight * NowPosition_Y), (float)(screenHeight / maxHeight * RevisionPosition_Y));
//		}
//		
//		Log.i("PositionCalculation", "[RevisionAfter] X="+ RevisionPosition_X+" Y="+RevisionPosition_Y +" Z="+ RevisionPosition_Z);
//		TextView txtX = (TextView)mView.findViewById(R.id.txtViewX);
//		txtX.setText("[X] : " +RevisionPosition_X);
//		TextView txtY = (TextView)mView.findViewById(R.id.txtViewY);
//		txtY.setText("[Y] : " + RevisionPosition_Y);
		
		float markerX;
		float markerY;
		
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		/** ���� ��ġ ǥ���ϴ� �� 342*342 ���� �� INVISIBLE ó��. **/
		mMarker.setLayoutParams(new AbsoluteLayout.LayoutParams(342, 342, 0, 0));
		mMarker.setVisibility(View.INVISIBLE);
		
		/** ������ ȿ�� ���� **/
		Drawable alpha = mMarker.getDrawable();
		alpha.setAlpha(100);
		
		ArrayList<BeaconInfo> mTemp = new ArrayList<BeaconInfo>();
		mTemp = mBeaconHelper.getBeaconInfo(beacons);
		
		if(mTemp.get(0) != null && (-75 <= mTemp.get(0).getRssi()) && (mTemp.get(0).getRssi() <= -5)) {
			if (greenCnt != 1) { 
				GreenDialog green = new GreenDialog(getActivity().getApplicationContext());
				green.show();
				greenCnt++;
			}
			markerX = 124;
			markerY = 503;
			mMarker.setX(markerX);
			mMarker.setY(markerY);
			mMarker.setVisibility(View.VISIBLE);
		}
		else if(mTemp.get(1) != null && (-75 <= mTemp.get(1).getRssi()) && (mTemp.get(1).getRssi() <= -5)) {
			if (yellowCnt != 1) {
				YellowDialog yellow = new YellowDialog(getActivity().getApplicationContext());
				yellow.show();
				yellowCnt++;
			}
			markerX = 567;
			markerY = 503;
			mMarker.setX(markerX);
			mMarker.setY(markerY);
			mMarker.setVisibility(View.VISIBLE);
		}
		else if(mTemp.get(2) != null  && (-75 <= mTemp.get(2).getRssi()) && (mTemp.get(2).getRssi() <= -5)) {
			if (redCnt != 1) {
				RedDialog red = new RedDialog(getActivity().getApplicationContext());
				red.show();
				redCnt++;
			}
			markerX = 1011;
			markerY = 503;
			mMarker.setX(markerX);
			mMarker.setY(markerY);
			mMarker.setVisibility(View.VISIBLE);
		}
		else if(mTemp.get(3) != null  && (-75 <= mTemp.get(3).getRssi()) && (mTemp.get(3).getRssi() <= -5)) {
			if (whiteCnt != 1) {
				WhiteDialog white = new WhiteDialog(getActivity().getApplicationContext());
				white.show();
				whiteCnt++;
			}
			markerX = 1454;
			markerY = 503;
			mMarker.setX(markerX);
			mMarker.setY(markerY);
			mMarker.setVisibility(View.VISIBLE);
		}
	}

	/** ���� Map�� �׸� ���̾ƿ� ���� �ޱ� **/
	public void drawMaker() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		
		mapLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		this.screenWidth = mapLayout.getMeasuredWidth() / dm.density;
		this.screenHeight = mapLayout.getMeasuredHeight() / dm.density;
		
		Log.i("NavigationActivity", "Width = " + screenWidth + ", Height = " + screenHeight);
		Log.i("NavigationActivity", "ScreenWidth = " + dm.widthPixels + ", ScreenHeight = " + dm.heightPixels);
	}
	
	/** Map Marker �̵� �Լ� **/
	public void moveImage(float fromX, final float toX, float fromY, final float toY) {
		animation = new TranslateAnimation(fromX, toX, fromY, toY);
		animation.setDuration(500);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
			}
		});
		
//		Log.e("NavigationActivity", "[Device]  X: " +mAndroidToBeacon.getAndroidX() + ", Y: " + mAndroidToBeacon.getAndroidY());
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		mMarker.startAnimation(animation);
	}
}