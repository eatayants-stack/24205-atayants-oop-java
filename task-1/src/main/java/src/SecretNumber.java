package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecretNumber {

    private final String value;


    private SecretNumber(String value)
    {
        this.value = value;
    }


    public static SecretNumber GenerateSecret()
    {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i <= 9; i++) {
            digits.add(i);
        }

        Collections.shuffle(digits);

        StringBuilder sb = new StringBuilder();

        for (int digit : digits.subList(0, 4)) {
            sb.append(digit);
        }
        return new SecretNumber(sb.toString());
    }

    public static SecretNumber of(String value) {
        return new SecretNumber(value);
    }

    public String getValue() {return value; }
}