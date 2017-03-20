package com.cosw.shanxigas.card;

import org.simalliance.openmobileapi.SEService;

/**
 * Created by Ryan on 2017/1/12.
 */

public class CardFactory {

  public static ICard getCardManager(SEService seService, String aid) throws CardException {
    if (seService == null) {
      throw new CardException("seService is null");
    }
    return CardImplUseOpenMobile.getInstance(seService, aid);
  }

}
