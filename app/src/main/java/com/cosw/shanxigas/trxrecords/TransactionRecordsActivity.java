package com.cosw.shanxigas.trxrecords;

import static com.cosw.shanxigas.util.Constant.EXTRA_ORDER_NO;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cosw.shanxigas.R;
import com.cosw.shanxigas.base.BaseActivity;
import com.cosw.shanxigas.entity.TrxDetail;
import com.cosw.shanxigas.entity.TrxRecords;
import com.cosw.shanxigas.trxrecords.TrxRecordsContract.Presenter;
import java.util.ArrayList;
import java.util.List;

public class TransactionRecordsActivity extends BaseActivity implements TrxRecordsContract.View,
    OnItemClickListener {

  private TrxAdapter mAdapter;

  private TextView mEmptyStateTextView;

  private TrxRecordsContract.Presenter mPresenter;

  private boolean isBottom;

  private boolean loadCompleted;

  private int onceLoadCount;

  private int hasLoadedCount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.trasanction_records_act);

    mAdapter = new TrxAdapter(this, R.layout.trx_record_list_item, new ArrayList<TrxRecords>());
    ListView mTrxRecordsListView = (ListView) findViewById(R.id.list_view);
    mTrxRecordsListView.setAdapter(mAdapter);
    mTrxRecordsListView.setOnItemClickListener(this);

    mTrxRecordsListView.setOnScrollListener(new OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
          if (isBottom) {
            // 下载更多数据
            if (loadCompleted) {
              Toast.makeText(TransactionRecordsActivity.this,
                  R.string.transaction_record_load_no_data,
                  Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(TransactionRecordsActivity.this, R.string.transaction_records_loading,
                  Toast.LENGTH_SHORT).show();
            }
            //加载数据的方法代码.......
            mPresenter.getQueryOrderList(hasLoadedCount);
          }
        }
      }

      @Override
      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
          int totalItemCount) {
        // 说明:
        // fistVisibleItem:表示划出屏幕的ListView子项个数
        // visibleItemCount:表示屏幕中正在显示的ListView子项个数
        // totalItemCount:表示ListView子项的总数
        // 前两个相加==最后一个说明ListView滑到底部
        isBottom = firstVisibleItem + visibleItemCount == totalItemCount;
      }
    });

    mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
    //mTrxRecordsListView.setEmptyView(mEmptyStateTextView);
    mEmptyStateTextView.setText(R.string.transaction_records_no_record);
    mEmptyStateTextView.setVisibility(View.GONE);

    initViews();

    setPresenter(new TrxRecordPresenter(this, TrxRecordModel.getInstance()));

    mPresenter.start();
  }

  private void initViews() {
    TextView tvTitle = (TextView) findViewById(R.id.tv_title);
    tvTitle.setText(R.string.transaction_records_title);
    ImageView ivTitleLeft = (ImageView) findViewById(R.id.img_left);
    // 设置消息页面为初始页面
    ivTitleLeft.setVisibility(View.VISIBLE);
    ivTitleLeft.setImageResource(R.drawable.ic_arrow_back_white_24dp);
    ivTitleLeft.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    TrxRecords record = mAdapter.getItem(position);
    if (record != null) {
      String orderNo = record.getOrderNo();
      Intent intent = new Intent(this, TransactionDetailActivity.class);
      intent.putExtra(EXTRA_ORDER_NO, orderNo);
      startActivity(intent);
    }
  }

  @Override
  public void setPresenter(Presenter presenter) {
    mPresenter = presenter;
  }

  @Override
  public void setData(List<TrxRecords> trxRecords) {
    int loadSize = 0;
    if (trxRecords == null) {
      mEmptyStateTextView.setVisibility(View.VISIBLE);
      return;
    }
    if (!trxRecords.isEmpty()) {
      loadSize = trxRecords.size();
      mEmptyStateTextView.setVisibility(View.GONE);
      mAdapter.addAll(trxRecords);
    }
    if (loadSize < onceLoadCount) {
      loadCompleted = true;
    }
    onceLoadCount = loadSize;
    hasLoadedCount += loadSize;
    if (isBottom && loadSize == 0) {
      loadCompleted = true;
    }
  }

  @Override
  public void setDetail(TrxDetail detail) {
  }
}
