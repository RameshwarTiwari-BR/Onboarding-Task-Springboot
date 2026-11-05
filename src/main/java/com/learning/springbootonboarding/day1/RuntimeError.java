package com.learning.springbootonboarding.day1;

public class RuntimeError {

    public static void main(String[] args) {

        int a = 10;

        int b = 0;

        int result = a / b;

        System.out.println(result);

    }
}


// blueray@adminuser-Lenovo-V14-G3-IAP-1:~/Downloads/Day-1$ javac RuntimeError.java
// blueray@adminuser-Lenovo-V14-G3-IAP-1:~/Downloads/Day-1$ java RuntimeError
// Exception in thread "main" java.lang.ArithmeticException: / by zero
//         at RuntimeError.main(RuntimeError.java:9)


// This code complie successfully but throw error during run time caught by JVM.. like divided by 0