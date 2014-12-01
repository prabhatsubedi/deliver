package com.yetistep.delivr.enums;

/**
 * Created with IntelliJ IDEA.
 * User: surendraJ
 * Date: 11/26/14
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenderCustom extends PersistentEnumUserType<Gender> {
@Override
  public Class<Gender> returnedClass() {
        return Gender.class;
  }

}
