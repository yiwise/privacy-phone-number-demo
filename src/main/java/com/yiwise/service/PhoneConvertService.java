package com.yiwise.service;

/**
 * 号码的变换
 */
public interface PhoneConvertService {
    /**
     * 将原始被叫号码转换为隐私号码
     * @param originNumber
     * @return
     */
    String encript(String originNumber);

    /**
     * 将隐私被叫号码转换为原始号码
     * @param encriptedNumber
     * @return
     */
    String decript(String encriptedNumber);
}
