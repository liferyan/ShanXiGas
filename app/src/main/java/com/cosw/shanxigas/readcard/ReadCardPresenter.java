package com.cosw.shanxigas.readcard;

import static com.cosw.shanxigas.util.Constant.READ_CARD_FAILED_CARD_HAS_BIND_MSG;
import static com.cosw.shanxigas.util.Constant.READ_CARD_FAILED_TO_GET_CARD_BIND_STATUS_MSG;
import static com.cosw.shanxigas.util.Constant.READ_CARD_FAILED_TO_READ_CARD_MSG;

import android.nfc.Tag;

/**
 * Created by Ryan on 2017/1/11.
 */

public class ReadCardPresenter implements ReadCardContract.Presenter,
    ReadCardContract.Model.ReadCardCallback, ReadCardContract.Model.QueryCardBindStatusCallback {

  private ReadCardContract.View mView;
  private ReadCardContract.Model mModel;
  private String mCardNo;

  public ReadCardPresenter(ReadCardContract.View view, ReadCardContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override
  public void start() {
  }

  @Override
  public void getCardNoFromTag(Tag tag) {
    mModel.readCardFromTag(tag, this);
  }

  @Override
  public void onReadCardSuccess(String cardNo) {
    mCardNo = cardNo;
    mModel.queryCardBindStatus(cardNo, this);
  }

  private void setMessage(String message) {
    mView.hideLoading();
    mView.showMessage(message);
  }

  @Override
  public void onReadCardFailed() {
    setMessage(READ_CARD_FAILED_TO_READ_CARD_MSG);
  }

  @Override
  public void onQueryCardBindStatusFailed() {
    setMessage(READ_CARD_FAILED_TO_GET_CARD_BIND_STATUS_MSG);
  }

  @Override
  public void onHasBind(boolean hasBind) {
    if (hasBind) {
      setMessage(READ_CARD_FAILED_CARD_HAS_BIND_MSG);
    } else {
      mView.goToActivateActivity(mCardNo);
    }
  }
}
