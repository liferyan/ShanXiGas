package com.cosw.shanxigas.trxrecords;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.entity.TrxRecords;
import java.util.List;

/**
 * Created by Ryan on 2017/1/16.
 */

public class TrxAdapter extends ArrayAdapter<TrxRecords> {

  private int resourceId;

  public TrxAdapter(Context context, int resource, List<TrxRecords> objects) {
    super(context, resource, objects);
    resourceId = resource;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TrxRecords record = getItem(position);
    View view;
    ViewHolder viewHolder;
    if (convertView == null) {
      view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
      viewHolder = new ViewHolder();
      viewHolder.tvAmount = (TextView) view.findViewById(R.id.tv_amount);
      viewHolder.tvOrderNo = (TextView) view.findViewById(R.id.tv_order_no);
      viewHolder.tvCreateTime = (TextView) view.findViewById(R.id.tv_create_time);
      viewHolder.tvPayStatus = (TextView) view.findViewById(R.id.tv_pay_status);
      view.setTag(viewHolder);
    } else {
      view = convertView;
      viewHolder = (ViewHolder) view.getTag();
    }
    viewHolder.tvOrderNo.setText(record.getOrderNo());
    String payStatus = record.getPayStatus();
    if (!TextUtils.isEmpty(payStatus) && ("圈存成功".equals(payStatus) || "冲正成功".equals(payStatus))) {
      viewHolder.tvPayStatus.setTextColor(Color.parseColor("#2EAE3F"));
    } else {
      viewHolder.tvPayStatus.setTextColor(Color.RED);
    }
    viewHolder.tvPayStatus.setText(record.getPayStatus());
    viewHolder.tvCreateTime.setText(record.getCreateTime());
    viewHolder.tvAmount.setText(record.getAmount() + "元");
    return view;
  }

  private static class ViewHolder {

    private TextView tvOrderNo;
    private TextView tvAmount;
    private TextView tvCreateTime;
    private TextView tvPayStatus;
  }
}
