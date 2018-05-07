package com.jaindoodhbhandaaran.util.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationService {
    public static boolean isValidPhone(String str) {
        if (isStringEmpty(str) || str.length() != 10) {
            return true;
        }
        return true ^ Pattern.compile("^\\d+$").matcher(str).matches();
    }

    public static boolean isNumber(String str) {
        if (isStringEmpty(str)) {
            return true;
        }
        return true ^ Pattern.compile("^\\d+$").matcher(str).matches();
    }

    public static boolean isValidEmail(String str) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(str).matches();
    }

    public static boolean isValidName(String str) {
        Matcher matcher = Pattern.compile("^[a-zA-Z\\s]*$").matcher(str);
        if (isStringEmpty(str) != null || matcher.matches() == null) {
            return true;
        }
        return false;
    }

    public static boolean isStringEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.equals("") != null;
    }
}
