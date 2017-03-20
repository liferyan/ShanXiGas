package com.cosw.shanxigas.activate;

import static com.cosw.shanxigas.util.Constant.ACTIVATE_FAILED_TO_ACTIVATE_MSG;
import static com.cosw.shanxigas.util.Constant.ACTIVATE_FAILED_TO_BIND_MSG;
import static com.cosw.shanxigas.util.Constant.ACTIVATE_LOADING_ACTIVATE;
import static com.cosw.shanxigas.util.Constant.ACTIVATE_LOADING_GET_CARD_INFO;
import static com.cosw.shanxigas.util.Constant.ACTIVATE_FAILED_TO_GET_CARD_INFO_MSG;

/**
 * Created by Ryan on 2017/1/10.
 */

public class ActivatePresenter implements ActivateContract.Presenter,
    ActivateContract.Model.QueryCardInfoForBindCallback,
    ActivateContract.Model.QueryCardKeyCallback, ActivateContract.Model.UserInfoInitCallback,
    ActivateContract.Model.CardBindCallback {

  private ActivateContract.View mView;
  private ActivateContract.Model mModel;
  private boolean mOnlyBind;

  public ActivatePresenter(ActivateContract.View view, ActivateContract.Model model) {
    mView = view;
    mModel = model;
  }

  @Override
  public void start() {
  }

  @Override
  public void getCardInfo(String cardNo) {
    mView.showLoading(ACTIVATE_LOADING_GET_CARD_INFO);
    mModel.loadCardInfo(cardNo, this);
  }

  @Override
  public void activate(boolean onlyBind) {
    mView.showLoading(ACTIVATE_LOADING_ACTIVATE);
    if (onlyBind || mOnlyBind) {
      mModel.cardBind(this);
    } else {
      mModel.queryCardKey(this);
    }
  }

  private void setMessage(String message) {
    mView.hideLoading();
    mView.showMessage(message);
  }

  @Override
  public void onQueryCardInfoForBindSuccess(String cardNo, String userName, String address,
      String gapType) {
    mView.hideLoading();
    mView.showCardInfo(cardNo, userName, address, gapType);
  }

  @Override
  public void onQueryCardInfoForBindFailed() {
    setMessage(ACTIVATE_FAILED_TO_GET_CARD_INFO_MSG);
  }

  @Override
  public void onQueryCardKeySuccess() {
    mModel.userInfoInit(this);
  }

  @Override
  public void onUserInfoInitSuccess() {
    mModel.cardBind(this);
  }

  @Override
  public void onCardBindFailed() {
    mOnlyBind = true;
    setMessage(ACTIVATE_FAILED_TO_BIND_MSG);
  }

  @Override
  public void onCardBindSuccess() {
    mView.hideLoading();
    mView.goToMainActivity();
  }

  @Override
  public void onActivateFailed() {
    setMessage(ACTIVATE_FAILED_TO_ACTIVATE_MSG);
  }
}
