package com.github.spark.grpc.proto.util;

import com.github.spark.account.Decimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtil {

    public static final int PRECISION = 18;
    public static final RoundingMode ROUNDMODE = RoundingMode.FLOOR;

    //---------------------------------------------------------------------
    // Get BigDecimal from All types
    //---------------------------------------------------------------------

    public static BigDecimal toBigDecimal(Decimal decimalValue) {
        if (null != decimalValue.getStr() && !"".equals(decimalValue.getStr().trim())) {
            return new BigDecimal(decimalValue.getStr()).setScale(PRECISION, ROUNDMODE);
        }
        return BigDecimal.valueOf(decimalValue.getUnscaledValue(),
                decimalValue.getScale()).setScale(PRECISION, ROUNDMODE);
    }

    public static BigDecimal toBigDecimal(Decimal decimalValue, BigDecimal defaultValue) {
        try {
            return toBigDecimal(decimalValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static BigDecimal toBigDecimal(double doubleValue) {
        return BigDecimal.valueOf(doubleValue).setScale(PRECISION, ROUNDMODE);
    }

    public static BigDecimal toBigDecimal(long longValue) {
        return BigDecimal.valueOf(longValue).setScale(PRECISION, ROUNDMODE);
    }

    //---------------------------------------------------------------------
    // Get Decimal from All types
    //---------------------------------------------------------------------

    public static Decimal fromBigDecimal(BigDecimal bigDecimalValue) {
        return Decimal.newBuilder()
                .setStr(bigDecimalValue.toPlainString())
                /*
                   有溢出风险，所以不启用这个字段
                 */
                //.setUnscaledValue(bigDecimalValue.unscaledValue())
                .setScale(bigDecimalValue.scale())
                .build();
    }

    public static Decimal fromLong(Long longValue) {
        BigDecimal dc = BigDecimal.valueOf(longValue).setScale(PRECISION, ROUNDMODE);
        return fromBigDecimal(dc);
    }

    public static Decimal fromDouble(Double doubleValue) {
        BigDecimal dc = BigDecimal.valueOf(doubleValue).setScale(PRECISION, ROUNDMODE);
        return fromBigDecimal(dc);
    }

    //---------------------------------------------------------------------
    // Format decimal string
    //---------------------------------------------------------------------

    public static String toTrimString(Decimal value) {
        String strDC = value.getStr();
        int dotIndex = strDC.lastIndexOf(".");

        if (dotIndex == -1) {
            return strDC + ".0";
        }

        for (int i = strDC.length() - 1; i > dotIndex; i--) {

            // 如果为整数，保留一位小数即可
            int preTest = i - 1;
            if (strDC.charAt(i) != '0' || (preTest >= 0 && strDC.charAt(preTest) == '.')) {
                return strDC.substring(0, i + 1);
            }
        }
        return strDC;
    }

    public static String toTrimString(BigDecimal value) {
        String strDC = value.toPlainString();
        int dotIndex = strDC.lastIndexOf(".");
        if (dotIndex == -1) {
            return strDC + ".0";
        }

        for (int i = strDC.length() - 1; i > dotIndex; i--) {

            // 如果为整数，保留一位小数即可
            int preTest = i - 1;
            if (strDC.charAt(i) != '0' || (preTest >= 0 && strDC.charAt(preTest) == '.')) {
                return strDC.substring(0, i + 1);
            }
        }
        return strDC;
    }

    public static void main(String[] args) {

        // 18位有效
        Decimal dc1 = Decimal.newBuilder()
                .setStr("123456789.123456789987654321")
                .setScale(18).build();

        // 18位,9位有效
        Decimal dc2 = Decimal.newBuilder()
                .setStr("123456789.123456789000000000")
                .setScale(18).build();

        // 20位有效(截取18位)
        Decimal dc3 = Decimal.newBuilder()
                .setStr("123456789.12345678998765432112")
                .setScale(18).build();

        // 20位有效(截取18位)
        Decimal dc4 = Decimal.newBuilder()
                .setStr("123456789.12345678998765432192")
                .setScale(18).build();

        System.out.println("18位有效    ：" + DecimalUtil.toBigDecimal(dc1));
        System.out.println("18位中9位有效：" + DecimalUtil.toBigDecimal(dc2));
        System.out.println("20位有效(截取18位)：" + DecimalUtil.toBigDecimal(dc3));
        System.out.println("20位有效(截取18位)：" + DecimalUtil.toBigDecimal(dc4));

        System.out.println("18位中9位有效,美化输出：" + DecimalUtil.toTrimString(dc2));
        System.out.println("测试字符处理性能...");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 3000000; i++) {
            DecimalUtil.toTrimString(dc2);
        }
        long end = System.currentTimeMillis();
        System.out.println("转换300W数据，Total spend:" + (end - start) + "(ms)");

        Decimal dc5 = fromDouble(123456789.12345678998765432112);
        System.out.println("From Double：" + dc5);

        Decimal dc7 = fromLong(1000000000000000000l);
        System.out.println("From Long：" + dc7);

        System.out.println("原始的BigDecimal:" + DecimalUtil.toBigDecimal(dc2));
        System.out.println("美化后BigDecimal:" + toTrimString(DecimalUtil.toBigDecimal(dc2)));

        // 小数后全部为0
        Decimal dc8 = Decimal.newBuilder()
                .setStr("123456789.0000000000")
                .setScale(18).build();
        System.out.println("小数点后全为0,美化输出：" + DecimalUtil.toTrimString(dc8));

        // 小数后只有一位0,要补齐为.00
        Decimal dc9 = Decimal.newBuilder()
                .setStr("123456789.0")
                .setScale(18).build();
        System.out.println("只有一位小数0,美化输出：" + DecimalUtil.toTrimString(dc9));

        // 整数,要补齐为.00
        Decimal dc10 = Decimal.newBuilder()
                .setStr("123456789")
                .setScale(18).build();
        System.out.println("整数要补齐0,美化输出：" + DecimalUtil.toTrimString(dc10));
    }
}
