package com.chr.para.utils;

/**
 * 属性文件的枚举对象
 *
 * @author RAY
 */
public enum PropertyEnum {

  /** zookeeper链接地址 */
  ZOOKEEPER_URL("zookeeper.url"),

  /** ACL用户名和密码 */
  ZOOKEEPER_USERPASSWORD("zookeeper.userpassword");

  private String key;

  PropertyEnum(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
