package com.spherebattles.NBT;

public class MathHelper {
    public static int floor(double value)
    {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
    public static int floor(float value)
    {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }
    public static int roundUp(int number, int interval) {
        if (interval == 0) {
           return 0;
        } else if (number == 0) {
           return interval;
        } else {
           if (number < 0) {
              interval *= -1;
           }

           int i = number % interval;
           return i == 0 ? number : number + interval - i;
        }
     }
}
