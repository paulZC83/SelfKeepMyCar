package cn.sh.changxing.selfkeepmycar.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.base.BaseActivity;
import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.utils.Utils;

public class AddRecordActivity extends BaseActivity {
    @BindView(R.id.back)
    Button mBack;
    @BindView(R.id.save)
    Button mSave;
    @BindView(R.id.add_date)
    TextView mTvDate;
    @BindView(R.id.sp_target)
    Spinner mSpTarget;
    @BindView(R.id.other_target)
    EditText mEdOtherTarget;
    @BindView(R.id.distance)
    EditText mEdDistance;
    @BindView(R.id.et_fuel_consumption)
    EditText mEdFuel;
    @BindView(R.id.ed_fuel_price)
    EditText mEdFuelPrice;
    @BindView(R.id.sp_re)
    Spinner mSpReType;
    @BindView(R.id.ed_re_money)
    EditText mEdReMoney;
    @BindView(R.id.ed_drive_money)
    TextView mEdDriveMoney;
    @BindView(R.id.sp_ex)
    Spinner mSpExType;
    @BindView(R.id.ed_ex_money)
    EditText mEdExMoney;
    @BindView(R.id.ed_ex_other_tx)
    EditText mEdExOtherTx;
    @BindView(R.id.save_bt)
    Button mBtSave;
    @BindView(R.id.computDrive)
    Button mBtComputDrive;
    @BindView(R.id.add_record_bk)
    ImageView mAddBk;
    @BindView(R.id.reset_price)
    Button mBtResetPrice;

    private Date mDate;
    private int mYear, mMonth, mDay;
    private SharedPreferences mSharePrefenrences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        ButterKnife.bind(this);
        mSharePrefenrences = getSharedPreferences("data", MODE_PRIVATE);
        Glide.with(this).load("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=121541430,514856759&fm=27&gp=0.jpg").into(mAddBk);
        initData();
        setViewListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mEdFuelPrice.setText(mSharePrefenrences.getString("price", "6.45"));
    }

    private void initData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        mDate = new Date();
        mTvDate.setText(format.format(mDate));

        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);

    }
    private void setViewListener(){
        mEdOtherTarget.setVisibility(View.GONE);
        mEdExOtherTx.setVisibility(View.GONE);
        mSpTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    mEdOtherTarget.setVisibility(View.VISIBLE);
                } else {
                    mEdOtherTarget.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpExType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    mEdExOtherTx.setVisibility(View.VISIBLE);
                } else {
                    mEdExOtherTx.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private boolean checkData(){
        boolean result = true;
        if (Double.parseDouble(mEdFuel.getText().toString()) == 0) {
            if (Double.parseDouble(mEdExMoney.getText().toString()) > 0) {
                // 允许记录只有花费的场合
                result = true;
            }
        } else {
            if (!TextUtils.isEmpty(mEdReMoney.getText().toString()) && Double.parseDouble(mEdReMoney.getText().toString()) > 0d) {
                result = true;
            } else {
                result = false;
                Toast.makeText(this, "养车记录必须有收入，否则不用记录", Toast.LENGTH_SHORT).show();
            }
        }

        return result;
    }

    private void saveData(){
        if (checkData()) {
            final Record record = new Record();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            record.setDateYmd(mTvDate.getText().toString());
            record.setTarget(mSpTarget.getSelectedItem().toString());
            if (mEdOtherTarget.getVisibility() == View.VISIBLE) {
                record.setOtherTarget(mEdOtherTarget.getText().toString());
            }
            record.setDistance(Double.parseDouble(mEdDistance.getText().toString()));
            record.setUnitPrice(Double.parseDouble(mEdFuelPrice.getText().toString()));
            record.setReceiptsType(mSpReType.getSelectedItem().toString());
            if (!TextUtils.isEmpty(mEdReMoney.getText().toString())){
                record.setReceiptsMoney(Double.parseDouble(mEdReMoney.getText().toString()));
            }
            if (!TextUtils.isEmpty(mEdDriveMoney.getText().toString())){
                record.setDriveMoney(Double.parseDouble(mEdDriveMoney.getText().toString()));
            }

            record.setExpensesType(mSpExType.getSelectedItem().toString());
            if (!TextUtils.isEmpty(mEdExMoney.getText().toString())) {
                record.setExpensesMoney(Double.parseDouble(mEdExMoney.getText().toString()));
            }

            if (mEdExOtherTx.getVisibility() == View.VISIBLE){
                record.setOtherExpenses(mEdExOtherTx.getText().toString());
            }
            record.setFuelConsumption(Double.parseDouble(mEdFuel.getText().toString()));


//            RecordBmob recordBmob = Utils.castToBmobFromRecord(record);
            record.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        Log.d("AddRecordActivity", "Record添加数据成功:"+objectId);
                        record.setObjId(objectId);
                        record.setObjectId(objectId);
                        record.saves();
                        finish();
                    } else {
                        Log.d("AddRecordActivity", "Record创建数据失败:"+e.getMessage());
                        record.setObjId(objectId);
                        record.setObjectId(objectId);
                        record.saves();
                        finish();
                    }
                }
            });

        } else {
            Toast.makeText(this, "数据未录入完整，请检查后再试", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.back, R.id.save, R.id.save_bt, R.id.computDrive, R.id.add_date, R.id.reset_price})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_date:
                (new DatePickerDialog(this, mdateListener, mYear, mMonth, mDay)).show();
                break;
            case R.id.computDrive:
                if (TextUtils.isEmpty(mEdFuel.getText().toString())) {
                    Toast.makeText(this, "计算花费需输入油耗数据，请检查后再试", Toast.LENGTH_SHORT).show();
                } else {
                    mEdDriveMoney.setText(""+ Utils.parseDouble(Utils.div(Double.parseDouble(mEdFuel.getText().toString()), 100, 1)
                            * Double.parseDouble(mEdDistance.getText().toString())
                            * Double.parseDouble(mEdFuelPrice.getText().toString())));
                }

                break;
            case R.id.save:
            case R.id.save_bt:
                if (TextUtils.isEmpty(mEdFuel.getText().toString()) ||
                        TextUtils.isEmpty(mEdDriveMoney.getText().toString()))
                {
                    Toast.makeText(this, "油耗和行驶花费必须输入，请检查后再试", Toast.LENGTH_SHORT).show();
                } else {
                    saveData();
                }

                break;
            case R.id.reset_price:
                SharedPreferences.Editor edit = mSharePrefenrences.edit();
                edit.putString("price", String.valueOf(mEdFuelPrice.getText().toString()));
                edit.commit();
                Toast.makeText(AddRecordActivity.this, "设置成功", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mdateListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mTvDate.setText(new StringBuffer().append(mYear).append("-").append((mMonth+1)<10?("0"+(mMonth+1)):(mMonth+1)).append("-").append(mDay<10?("0"+mDay):mDay));
        }
    };
}
