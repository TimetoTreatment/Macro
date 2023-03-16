package org.timetotreatment;

import org.timetotreatment.domain.ExerciseType;
import org.timetotreatment.macro.MisaParkMacro;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.timetotreatment.MacroUtility.sleepUntil;

public class MacroApplication
{
    private static final boolean enableSubmit = true;

    /* Phase 1: 달력에서 날짜 선택하는 시각 */
    private static final LocalTime executionTime = LocalTime.of(8, 59, 59, 100 * 1_000_000);

    /* Phase 2: 신청하기 버튼 누르는 시각 */
    private static final LocalTime submitTime = LocalTime.of(9, 0, 4, (100 + (int) (Math.random() * 100)) * 1_000_000);

    public static void main(String[] args)
    {
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        MisaParkMacro misaParkMacroTennis = new MisaParkMacro();
        MisaParkMacro misaParkMacroFootball = new MisaParkMacro();
        LocalDate currentDate = LocalDate.now();

        misaParkMacroTennis.initialize("4096");

        if (enableSubmit)
        {
            MacroUtility.sleepUntil(executionTime);
        }
        else
        {
            MacroUtility.sleepUntil(LocalDateTime.now().plusSeconds(2));
        }

        /** 월요일 -> 일, 월, 화요일 예약 */
        if (currentDate.getDayOfWeek() == DayOfWeek.MONDAY)
        {
            /* 테니스, 일요일 */
            runMacro(misaParkMacroTennis, ExerciseType.TENNIS, currentDate.plusDays(13), new String[]{"10", "12"});

            /* 테니스, 월요일 */
            runMacro(misaParkMacroTennis, ExerciseType.TENNIS, currentDate.plusDays(14), new String[]{"20"});

            /* 테니스, 화요일 */
            runMacro(misaParkMacroTennis, ExerciseType.TENNIS, currentDate.plusDays(15), new String[]{"20"});

            /* 풋살, 일요일 */
            runMacro(misaParkMacroTennis, ExerciseType.FOOTBALL, currentDate.plusDays(13), new String[]{"08"});
            runMacro(misaParkMacroTennis, ExerciseType.FOOTBALL, currentDate.plusDays(13), new String[]{"10"});
        }
        /** 금요일 -> 토요일 예약 */
        else if (currentDate.getDayOfWeek() == DayOfWeek.FRIDAY)
        {
            /* 테니스, 토요일 */
            runMacro(misaParkMacroTennis, ExerciseType.TENNIS, currentDate.plusDays(15), new String[]{"08"});
        }
        /** 그 외 -> 2두 하루 뒤 평일 예약 */
        else
        {
            /* 테니스, 15일 뒤 평일 */
            runMacro(misaParkMacroTennis, ExerciseType.TENNIS, currentDate.plusDays(15), new String[]{"20"});
        }
    }

    private static void runMacro(MisaParkMacro misaParkMacro, ExerciseType exerciseType, LocalDate targetDate, String[] preferTimeList)
    {
        int[] preferCourtList = new int[]{4, 3, 2, 1};

        if (exerciseType == ExerciseType.TENNIS)
        {
            if (Math.random() < 0.35)
            {
                preferCourtList[0] = 3;
                preferCourtList[1] = 4;
            }
        }
        else if (exerciseType == ExerciseType.FOOTBALL)
        {
            preferCourtList = new int[]{2, 1};

            if (Math.random() < 0.5)
            {
                preferCourtList[0] = 1;
                preferCourtList[1] = 2;
            }
        }

        for (String time : preferTimeList)
        {
            boolean isSuccess = false;

            for (int court : preferCourtList)
            {
                isSuccess = misaParkMacro.run(exerciseType, time, court, targetDate, enableSubmit, submitTime);

                if (isSuccess)
                    break;
            }

            if (isSuccess)
                break;
        }
    }
}