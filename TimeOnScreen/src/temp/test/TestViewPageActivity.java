package temp.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.example.viewpagerdemo.R;
import com.pyq.timeonscreen.MainActivity;

public class TestViewPageActivity extends Activity implements OnClickListener {

	private static final int[] RES = { R.drawable.b1, R.drawable.b2,
			R.drawable.b3 };
	private LinearLayout llIndexState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testviewpage);

		llIndexState = (LinearLayout) findViewById(R.id.index_state);
		final Button btnStart = (Button) findViewById(R.id.btn_start);
		ViewPager vp = (ViewPager) findViewById(R.id.vp_test);

		initIndexState();

		vp.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				updateIndexState(arg0);
				if (arg0 == RES.length - 1) {
					btnStart.setVisibility(View.VISIBLE);
					llIndexState.setVisibility(View.GONE);
				} else {
					btnStart.setVisibility(View.GONE);
					llIndexState.setVisibility(View.VISIBLE);
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			public void onPageScrollStateChanged(int arg0) {

			}
		});
		vp.setAdapter(new MyPageAdapter());
		btnStart.setOnClickListener(this);
	}

	private void updateIndexState(int arg0) {
		for (int i = 0; i < llIndexState.getChildCount(); i++) {
			if (i == arg0)
				llIndexState.getChildAt(i).setPressed(true);
			else
				llIndexState.getChildAt(i).setPressed(false);

		}
	}

	private void initIndexState() {

		for (int i = 0; i < RES.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setImageResource(R.drawable.index_state_check_selector);
			if (i == 0) {
				iv.setPressed(true);
			} else
				iv.setPressed(false);
			iv.setPadding(5, 5, 5, 5);
			llIndexState.addView(iv);
		}

	}

	class MyPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = new ImageView(TestViewPageActivity.this);
			iv.setScaleType(ScaleType.FIT_XY);
			iv.setImageResource(RES[position]);

			container.addView(iv);

			iv.setPressed(false);

			return iv;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView((ImageView) object);
		}

	}

	public void onClick(View arg0) {
		if (arg0.getId() == R.id.btn_start) {
			startActivity(new Intent(this, MainActivity.class));
		}
	}
}
