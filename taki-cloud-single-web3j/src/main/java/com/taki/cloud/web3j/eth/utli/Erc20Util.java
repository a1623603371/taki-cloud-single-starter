package com.taki.cloud.web3j.eth.utli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Erc20Util {
    private static Logger logger = LoggerFactory.getLogger(Erc20Util.class);

    public static boolean isTransferFunc(String input) {
        return input.length() >= 10 && input.substring(2, 10).equals("a9059cbb");
    }


    public static boolean isSafeTransferFrom1155(String input) {
        return input.length() >= 10 && input.substring(2, 10).equals("f242432a");
    }

    public static boolean isSafeTransferFrom721(String input) {
        return input.length() >= 10 && input.substring(2, 10).equals("42842e0e");
    }

    public static boolean isCrossFunc(String input) {
        return input.length() >= 10 && input.substring(2, 10).equals("38ed1739");
    }

    public static String getToAddress(String input) {
        // input 合约地址 字段的编码规则为：0x + 函数名(8个字节） + 转出地址（64个字节）+ 转出值（64个字节）
        if (input.length() >= 74) {
            return "0x"+input.substring(34, 74);
        }
        return null;
    }

    public static BigDecimal getTransferValue(String input, int decimal) {
        try {
            if (input.length() >= 138) {
                String strHexValue = input.substring(74, 138);
                String transferValue = new BigInteger(strHexValue, 16).toString(10);
                return new BigDecimal(transferValue).setScale(4, RoundingMode.DOWN).divide(BigDecimal.valueOf(Math.pow(10, decimal)), RoundingMode.DOWN);
            }else{
                String transferValue = new BigInteger(input, 16).toString(10);
                return new BigDecimal(transferValue).setScale(4, RoundingMode.DOWN).divide(BigDecimal.valueOf(Math.pow(10, decimal)), RoundingMode.DOWN);
            }
//            return null;
        } catch (NumberFormatException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

}
