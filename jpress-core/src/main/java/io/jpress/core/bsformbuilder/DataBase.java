package io.jpress.core.bsformbuilder;

import com.jfinal.kit.TypeKit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

public class DataBase extends HashMap<String,Object> {


    private String getId(){
        return getString("id");
    }



    /**
     * Get attribute of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
     */
    public String getString(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return val != null ? val.toString() : null;
    }

    /**
     * Get attribute of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
     */
    public Integer getInt(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toInt(val);
    }

    /**
     * Get attribute of mysql type: bigint, unsign int
     */
    public Long getLong(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toLong(val);
    }

    /**
     * Get attribute of mysql type: unsigned bigint
     */
    public BigInteger getBigInteger(String attr) {
        Object val = get(attr);
        if (val instanceof BigInteger) {
            return (BigInteger)val;
        }
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }

        // 数据类型 id(19 number)在 Oracle Jdbc 下对应的是 BigDecimal,
        // 但是在 MySql 下对应的是 BigInteger，这会导致在 MySql 下生成的代码无法在 Oracle 数据库中使用
        if (val instanceof BigDecimal) {
            return ((BigDecimal)val).toBigInteger();
        } else if (val instanceof Number) {
            return BigInteger.valueOf(((Number)val).longValue());
        } else if (val instanceof String) {
            return new BigInteger((String)val);
        }

        return (BigInteger)val;
    }


    /**
     * Get attribute of mysql type: real, double
     */
    public Double getDouble(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toDouble(val);
    }

    /**
     * Get attribute of mysql type: float
     */
    public Float getFloat(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toFloat(val);
    }

    /**
     * Get attribute of mysql type: bit, tinyint(1)
     */
    public Boolean getBoolean(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toBoolean(val);
    }

    /**
     * Get attribute of mysql type: decimal, numeric
     */
    public BigDecimal getBigDecimal(String attr) {
        Object val = get(attr);
        if (val instanceof String && ((String) val).trim().length() == 0){
            return null;
        }
        return TypeKit.toBigDecimal(val);
    }


}
