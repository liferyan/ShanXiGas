package com.cosw.shanxigas.card;

import static com.cosw.shanxigas.util.Constant.CARD_INFO1;
import static com.cosw.shanxigas.util.Constant.SELECT_3F01;

import com.cosw.shanxigas.app.MyApplication;

/**
 * Created by Ryan on 2017/1/12.
 */

public class CardUtil {

  private static CardUtil INSTANCE;

  private MyApplication app;

  private ICard mCard;

  private CardUtil(ICard card) {
    mCard = card;
  }

  public static CardUtil getInstance(ICard card) {
    if (INSTANCE == null) {
      if (card != null) {
        INSTANCE = new CardUtil(card);
      } else {
        INSTANCE = null;
      }
    }
    return INSTANCE;
  }

  public String getBalance() throws CardException {
    //00006400 -> 100
    String balance;
    mCard.cardReset();
    mCard.transmit(SELECT_3F01);
    balance = mCard.transmit(CARD_INFO1).getData();
    int amount = Integer.valueOf(balance.substring(0, balance.length() - 2), 16);
    return String.valueOf(amount);
  }

}
