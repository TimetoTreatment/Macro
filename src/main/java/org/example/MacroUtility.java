package org.example;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class MacroUtility
{
    public static void sleepUntil(LocalDateTime targetDateTime)
    {
        LocalDateTime prevTime = LocalDateTime.now();

        for (; ; )
        {
            LocalDateTime currentTime = LocalDateTime.now();

            if (prevTime.getSecond() != currentTime.getSecond())
            {
                prevTime = currentTime;
                System.out.print(ChronoUnit.SECONDS.between(currentTime, targetDateTime) + 1 + " ");
            }

            if (currentTime.isAfter(targetDateTime) && currentTime.isBefore(targetDateTime.plusSeconds(10)))
            {
                break;
            }

            try
            {
                TimeUnit.MILLISECONDS.sleep(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void sleepUntil(LocalTime targetDateTime)
    {
        LocalTime prevTime = LocalTime.now();

        for (; ; )
        {
            LocalTime currentTime = LocalTime.now();

            if (prevTime.getSecond() != currentTime.getSecond())
            {
                prevTime = currentTime;
                System.out.print(ChronoUnit.SECONDS.between(currentTime, targetDateTime) + 1 + " ");
            }

            if (currentTime.isAfter(targetDateTime) && currentTime.isBefore(targetDateTime.plusSeconds(10)))
            {
                break;
            }

            try
            {
                TimeUnit.MILLISECONDS.sleep(1);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
