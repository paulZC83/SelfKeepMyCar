package cn.sh.changxing.selfkeepmycar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.db.model.ItemWeek;

/**
 * Created by ZengChao on 2018/4/25.
 */

public class WeekFuelAdapter extends BaseAdapter {
    private List<ItemWeek> mData;
    private Context mContext;
    public WeekFuelAdapter(Context context, List<ItemWeek> data) {
        mContext = context;
        mData = data;
    }

    public void updateData(List<ItemWeek> data){
        mData.clear();
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_week_fuel, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (mData.get(position).getShDays().endsWith("0天")) {
            viewHolder.shFuel.setVisibility(View.GONE);
            viewHolder.shDis.setVisibility(View.GONE);
            viewHolder.shIn.setVisibility(View.GONE);
            viewHolder.shOut.setVisibility(View.GONE);
        } else {
            viewHolder.shFuel.setVisibility(View.VISIBLE);
            viewHolder.shDis.setVisibility(View.VISIBLE);
            viewHolder.shIn.setVisibility(View.VISIBLE);
            viewHolder.shOut.setVisibility(View.VISIBLE);
        }

        if (mData.get(position).getXiaDays().endsWith("0天")) {
            viewHolder.xiaFuel.setVisibility(View.GONE);
            viewHolder.xiaDis.setVisibility(View.GONE);
            viewHolder.xiaIn.setVisibility(View.GONE);
            viewHolder.xiaOut.setVisibility(View.GONE);
        } else {
            viewHolder.xiaFuel.setVisibility(View.VISIBLE);
            viewHolder.xiaDis.setVisibility(View.VISIBLE);
            viewHolder.xiaIn.setVisibility(View.VISIBLE);
            viewHolder.xiaOut.setVisibility(View.VISIBLE);
        }
        viewHolder.title.setText(mData.get(position).getTitle());
        viewHolder.date.setText(mData.get(position).getDate());
        viewHolder.shFuel.setText(mData.get(position).getShFuel());
        viewHolder.shDis.setText(mData.get(position).getShDis());
        viewHolder.shIn.setText(mData.get(position).getShIn());
        viewHolder.shOut.setText(mData.get(position).getShOut());
        viewHolder.shDays.setText(mData.get(position).getShDays());
        viewHolder.xiaFuel.setText(mData.get(position).getXiaFuel());
        viewHolder.xiaDis.setText(mData.get(position).getXiaDis());
        viewHolder.xiaIn.setText(mData.get(position).getXiaIn());
        viewHolder.xiaOut.setText(mData.get(position).getXiaOut());
        viewHolder.xiaDays.setText(mData.get(position).getXiaDays());
        return view;
    }

    public class ViewHolder {
        private TextView title, date, shFuel, shDis, shIn, shOut, shDays, xiaFuel, xiaDis, xiaIn, xiaOut, xiaDays;
        public ViewHolder(View view){
            title = ((TextView) view.findViewById(R.id.item_week_title));
            date = ((TextView) view.findViewById(R.id.item_week_date));
            shFuel = ((TextView) view.findViewById(R.id.item_week_shang_fuel));
            shDis = ((TextView) view.findViewById(R.id.item_week_shang_distance));
            shIn = ((TextView) view.findViewById(R.id.item_week_shang_in));
            shOut = ((TextView) view.findViewById(R.id.item_week_shang_out));
            shDays = ((TextView) view.findViewById(R.id.item_week_shang_days));
            xiaFuel = ((TextView) view.findViewById(R.id.item_week_xia_fuel));
            xiaDis = ((TextView) view.findViewById(R.id.item_week_xia_distance));
            xiaIn = ((TextView) view.findViewById(R.id.item_week_xia_in));
            xiaOut = ((TextView) view.findViewById(R.id.item_week_xia_out));
            xiaDays = ((TextView) view.findViewById(R.id.item_week_xia_days));
        }
    }
}
