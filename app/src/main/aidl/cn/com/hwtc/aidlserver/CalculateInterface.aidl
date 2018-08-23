// CalculateInterface.aidl
package cn.com.hwtc.aidlserver;

// Declare any non-default types here with import statements

interface CalculateInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    int doCalculateAdd(int x,int y);
    int doCalculateSubtract(int x,int y);
    int doCalculateMultiply(int x,int y);
}
