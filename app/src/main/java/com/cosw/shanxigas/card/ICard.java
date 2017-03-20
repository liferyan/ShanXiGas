package com.cosw.shanxigas.card;

import com.cosw.shanxigas.entity.APDUReturn;

/**
 * Created by Ryan on 2017/1/12.
 */

public interface ICard {

  APDUReturn transmit(String apdu) throws CardException;

  void cardReset() throws CardException;

}
