package cn.sh.changxing.selfkeepmycar.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.bmob.v3.Bmob;
import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.base.BaseActivity;
import cn.sh.changxing.selfkeepmycar.fragment.FuelConsumptionFragment;
import cn.sh.changxing.selfkeepmycar.fragment.HomeFragment;
import cn.sh.changxing.selfkeepmycar.fragment.RecordFragment;
import cn.sh.changxing.selfkeepmycar.fragment.WeekFuelFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout mContentLayout;
    private TextView mHomeTv;
    private TextView mMessageTv;
    private TextView mMineTv;
    private FragmentManager mFm;
    private HomeFragment mHomeFragment;
    private RecordFragment mRecordFragment;
    private FuelConsumptionFragment mFuelFragment;
    private WeekFuelFragment mWeekFragment;
    private ImageView mBkMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //第一：默认初始化
        Bmob.initialize(this, "654ec0192b9cf51e3ab1d3809e4bf41e");

        initView();
        mFm = getSupportFragmentManager();
        mHomeTv.performClick();
    }

    private void initView() {

        mContentLayout = ((RelativeLayout) findViewById(R.id.cotent_view));
        mHomeTv = ((TextView) findViewById(R.id.tv_home));
        mHomeTv.setOnClickListener(this);
        mMessageTv = ((TextView) findViewById(R.id.tv_record));
        mMessageTv.setOnClickListener(this);
        mMineTv = ((TextView) findViewById(R.id.tv_fuel_consumption));
        mMineTv.setOnClickListener(this);
        mBkMain = ((ImageView) findViewById(R.id.bk_main));
        Glide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524822723673&di=866e4895af9e304d4a9b24230ceafe94&imgtype=0&src=http%3A%2F%2Fimg2.ph.126.net%2FX5bNnx85ljFUP8Bjy4-iqA%3D%3D%2F1008243366595017101.jpg").into(mBkMain);
    }

    public void intoWeek(int year, int month){
        FragmentTransaction transaction = mFm.beginTransaction();
        hideFragment(mHomeFragment, transaction);
        hideFragment(mRecordFragment, transaction);
        hideFragment(mFuelFragment, transaction);
//        mWeekFragment.
        if (mWeekFragment == null) {
            mWeekFragment = new WeekFuelFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("YEAR", year);
            bundle.putInt("MONTH", (month+1));
            mWeekFragment.setArguments(bundle);
            transaction.add(R.id.cotent_view, mWeekFragment);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("YEAR", year);
            bundle.putInt("MONTH", (month+1));
            mWeekFragment.getArguments().clear();
            mWeekFragment.getArguments().putAll(bundle);
            transaction.show(mWeekFragment);
        }
        transaction.commit();
    }

    public void intoFuel(){
        FragmentTransaction transaction = mFm.beginTransaction();
        intoFuel(transaction);
        transaction.commit();
    }
    public void intoFuel(FragmentTransaction transaction){
        hideFragment(mRecordFragment, transaction);
        hideFragment(mHomeFragment, transaction);
        hideFragment(mWeekFragment, transaction);
        if (mFuelFragment == null) {
            mFuelFragment = new FuelConsumptionFragment();
            transaction.add(R.id.cotent_view, mFuelFragment);
        } else {
            transaction.show(mFuelFragment);
        }
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = mFm.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_home:
                mHomeTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mMessageTv.setTextColor(getResources().getColor(R.color.white));
                mMineTv.setTextColor(getResources().getColor(R.color.white));

                hideFragment(mRecordFragment, transaction);
                hideFragment(mFuelFragment, transaction);
                hideFragment(mWeekFragment, transaction);
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                    transaction.add(R.id.cotent_view, mHomeFragment);
                } else {
                    transaction.show(mHomeFragment);
                }
                break;
            case R.id.tv_record:
                mMessageTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mHomeTv.setTextColor(getResources().getColor(R.color.white));
                mMineTv.setTextColor(getResources().getColor(R.color.white));

                hideFragment(mHomeFragment, transaction);
                hideFragment(mFuelFragment, transaction);
                hideFragment(mWeekFragment, transaction);
                if (mRecordFragment == null) {
                    mRecordFragment = new RecordFragment();
                    transaction.add(R.id.cotent_view, mRecordFragment);
                } else {
                    transaction.show(mRecordFragment);
                }
                break;
            case R.id.tv_fuel_consumption:
                mMineTv.setTextColor(getResources().getColor(R.color.colorPrimary));
                mMessageTv.setTextColor(getResources().getColor(R.color.white));
                mHomeTv.setTextColor(getResources().getColor(R.color.white));

                intoFuel(transaction);
                break;

        }
        transaction.commit();
    }

    private void hideFragment(Fragment fragment, FragmentTransaction transaction){
        if (fragment != null) {
            transaction.hide(fragment);
        }
    }
}
