package com.yiwise.service.impl;

import com.yiwise.service.PhoneConvertService;
import org.springframework.stereotype.Service;

/**
 * 简单的电话号码加解密实现.
 */
@Service("simplePhoneConvertService")
public class SimplePhoneConvertServiceImpl implements PhoneConvertService {
    // 秘钥：12位数字
    public static final String KEY = "483723723354";

    @Override
    public String encript(String originNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<originNumber.length();++i) {
            int a = (originNumber.charAt(i) - '0');
            int x = (KEY.charAt(i) - '0');
            int b = a ^ x;
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    @Override
    public String decript(String encriptedNumber) {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<encriptedNumber.length();++i) {
            int b = Integer.parseInt(String.valueOf(encriptedNumber.charAt(i)), 16);
            int x = (KEY.charAt(i) - '0');
            int a = b ^ x;
            sb.append((char)('0' + a));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        PhoneConvertService conv = new SimplePhoneConvertServiceImpl();
        String a = "057188876543";
        String b = conv.encript(a);
        String c = conv.decript(b);
        System.out.println(b + "<---->" + c);

        String a1 = "1799";
        String b1 = conv.encript(a1);
        String c1 = conv.decript(b1);
        System.out.println(b1 + "<---->" + c1);
    }
}
