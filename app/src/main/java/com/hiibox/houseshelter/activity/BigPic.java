package com.hiibox.houseshelter.activity;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.hiibox.houseshelter.ShaerlocActivity;
import com.hiibox.houseshelter.util.PreferenceUtil;
import com.zgan.youbao.R;

public class BigPic extends ShaerlocActivity {
	ImageView imageView;
	ImageView back;
	ImageView phone;
	private String emergencyContact = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bigpic);
		imageView = (ImageView) findViewById(R.id.big_pic);
		back = (ImageView) findViewById(R.id.back_iv);
		phone = (ImageView) findViewById(R.id.phone_iv);
		emergencyContact = PreferenceUtil.getInstance(mContext).getString(
				"emergencyContact", null);

		if (getIntent().getExtras() != null) {
			String url = getIntent().getStringExtra("url");
			finalBitmap.display(imageView, url);

		}
		back.setOnClickListener(listener);
		phone.setOnClickListener(listener);
		imageView.setOnTouchListener(new MulitPointTouchListener());
	}

	OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.back_iv:
				finish();
				break;

			case R.id.phone_iv:
				Intent intent = new Intent();
				intent.setAction(intent.ACTION_DIAL);
				if (!TextUtils.isEmpty(emergencyContact)) {
					intent.setData(Uri.parse("tel:" + emergencyContact));
				} else {
					intent.setData(Uri.parse("tel:" + 110));
				}
				startActivity(intent);
				break;
			}
		}
	};

	public class MulitPointTouchListener implements OnTouchListener {

		private static final String TAG = "Touch";
		// These matrices will be used to move and zoom image
		Matrix matrix = new Matrix();
		Matrix savedMatrix = new Matrix();

		// We can be in one of these 3 states
		static final int NONE = 0;
		static final int DRAG = 1;
		static final int ZOOM = 2;
		int mode = NONE;

		// Remember some things for zooming
		PointF start = new PointF();
		PointF mid = new PointF();
		float oldDist = 1f;

		/**
		 * 图片拖动监听
		 */
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			ImageView view = (ImageView) v;
			
			dumpEvent(event);

			// Handle touch events here...
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:

				matrix.set(view.getImageMatrix());
				savedMatrix.set(matrix);
				start.set(event.getX(), event.getY());
				// Log.d(TAG, "mode=DRAG");
				mode = DRAG;

				// Log.d(TAG, "mode=NONE");
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDist = spacing(event);
				// Log.d(TAG, "oldDist=" + oldDist);
				if (oldDist > 10f) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
					// Log.d(TAG, "mode=ZOOM");
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				// Log.e("view.getWidth", view.getWidth() + "");
				// Log.e("view.getHeight", view.getHeight() + "");

				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					// ...
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					// Log.d(TAG, "newDist=" + newDist);
					if (newDist > 10f) {
						matrix.set(savedMatrix);
						float scale = newDist / oldDist;
						matrix.postScale(scale, scale, mid.x, mid.y);
					}
				}
				break;
			}

			view.setImageMatrix(matrix);
			return true; // indicate event was handled
		}

		private void midPoint(PointF point, MotionEvent event) {
			// TODO Auto-generated method stub
			float x = event.getX(0) + event.getX(1);
			float y = event.getY(0) + event.getY(1);
			point.set(x / 2, y / 2);
		}

		private float spacing(MotionEvent event) {
			// TODO Auto-generated method stub
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}

		private void dumpEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
					"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			StringBuilder sb = new StringBuilder();
			int action = event.getAction();
			int actionCode = action & MotionEvent.ACTION_MASK;
			sb.append("event ACTION_").append(names[actionCode]);
			if (actionCode == MotionEvent.ACTION_POINTER_DOWN
					|| actionCode == MotionEvent.ACTION_POINTER_UP) {
				sb.append("(pid ").append(
						action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
				sb.append(")");
			}
			sb.append("[");
			for (int i = 0; i < event.getPointerCount(); i++) {
				sb.append("#").append(i);
				sb.append("(pid ").append(event.getPointerId(i));
				sb.append(")=").append((int) event.getX(i));
				sb.append(",").append((int) event.getY(i));
				if (i + 1 < event.getPointerCount())
					sb.append(";");
			}
			sb.append("]");
			// Log.d(TAG, sb.toString());
		}
	}

}
