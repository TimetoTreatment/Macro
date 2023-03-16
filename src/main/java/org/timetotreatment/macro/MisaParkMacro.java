package org.timetotreatment.macro;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.timetotreatment.MacroUtility;
import org.timetotreatment.domain.ExerciseType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.timetotreatment.MacroUtility.sleepUntil;

public class MisaParkMacro
{
    private static final List<String> organizationNameList = new ArrayList<>()
    {{
        add("아디다스구찌");
        add("나혼자산다");
        add("비염다람쥐");
    }};

    private static final int peopleMaxNumber = 5;
    private static final String phoneNumber = "01042664380";
    private static final String documentPath = "C:\\Users\\Main\\Desktop\\1.jpg";
    private static final String urlFormat = "https://www.hanam.go.kr/www/selectMisaParkResveWeb.do?key=7465&yyyymm=%s&misaParkCode=%s%d&listType=C#n";
    private static final String football = "FS0";
    private static final String tennisCode = "TS0";

    private static ChromeDriver driver;

    public boolean run(ExerciseType exerciseType, String reservationTime, int courtNum, LocalDate targetDate, boolean enableSubmit, LocalTime submitTime)
    {
        String url = null;

        switch (exerciseType)
        {
            case TENNIS ->
            {
                url = String.format(urlFormat, String.format("%d%02d", targetDate.getYear(), targetDate.getMonthValue()), tennisCode, courtNum);
            }
            case FOOTBALL ->
            {
                url = String.format(urlFormat, String.format("%d%02d", targetDate.getYear(), targetDate.getMonthValue()), football, courtNum);
            }
        }

        /** Phase 1 */
        driver.get(url);

        try
        {
            WebElement popupCloseButtonElement = new WebDriverWait(driver, Duration.ofMillis(2000)).until(ExpectedConditions.elementToBeClickable(By.cssSelector(".divpopup_close")));
            popupCloseButtonElement.click();
        }
        catch (Exception ignored)
        {
        }

        List<WebElement> days = driver.findElements(By.cssSelector("#date_table td > button:not(.old):not(.finish)"));

        for (int dayIndex = days.size() - 1; dayIndex >= 0; dayIndex--)
        {
            WebElement day = days.get(dayIndex);

            if (day.findElement(By.cssSelector("span")).getText().equals(Integer.toString(targetDate.getDayOfMonth())))
            {
                day.click();
                break;
            }
        }

        WebElement nextStepButton = driver.findElement(By.cssSelector(".safety_date .text_center a"));
        nextStepButton.click();

        /** Step 2 */
        String formTab = driver.getWindowHandle();

        List<WebElement> timeElementList = driver.findElements(By.cssSelector(".radio_list > li > span"));
        boolean isTimeAvailable = false;

        for (WebElement timeElement : timeElementList)
        {
            if (!timeElement.findElement(By.cssSelector("input")).isEnabled())
                continue;

            String time = timeElement.findElement(By.cssSelector("label")).getText().replaceAll("\\D", "").substring(0, 2);

            if (reservationTime.equals(time))
            {
                timeElement.click();
                isTimeAvailable = true;
                break;
            }
        }

        if (!isTimeAvailable)
            return false;

        WebElement organizationNameElement = new WebDriverWait(driver, Duration.ofMillis(2000)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("#grpNm")));
        organizationNameElement.sendKeys(organizationNameList.get((int) (Math.random() * organizationNameList.size())));

        WebElement peopleNumberElement = driver.findElement(By.cssSelector("#nmpr"));
        peopleNumberElement.sendKeys(((Integer) (int) (Math.random() * peopleMaxNumber + 1)).toString());

        WebElement phoneNumberElement = driver.findElement(By.cssSelector("#moblphon"));
        phoneNumberElement.sendKeys(phoneNumber);

        /* START: Address */
        WebElement findAddressElement = driver.findElement(By.cssSelector(".proposition_write li:nth-child(3) a"));
        findAddressElement.click();

        for (String tab : driver.getWindowHandles())
        {
            if (!tab.equals(formTab))
            {
                driver.switchTo().window(tab);
            }
        }

        WebElement addressKeywordElement = driver.findElement(By.cssSelector("#keyword"));
        addressKeywordElement.sendKeys("미사강변동로 20");

        WebElement searchButtonElement = driver.findElement(By.cssSelector("input[type=button]"));
        searchButtonElement.click();

        WebElement firstAddressElement = new WebDriverWait(driver, Duration.ofMillis(2000)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".data-col tbody tr:first-child a")));
        firstAddressElement.click();

        WebElement submitButtonElement = new WebDriverWait(driver, Duration.ofMillis(2000)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btns-submit a")));
        submitButtonElement.click();
        driver.switchTo().window(formTab);
        /* END: Address */

        //WebElement zipElement = driver.findElement(By.cssSelector("#zip"));
        //zipElement.sendKeys("12917");

        //WebElement addressElement = driver.findElement(By.cssSelector("#adres"));
        //addressElement.sendKeys("경기도 하남시 미사강변대로 55-1");

        WebElement documentElement = driver.findElement(By.cssSelector("#THUMB"));
        documentElement.sendKeys(documentPath);

        WebElement privacyCheckElement = driver.findElement(By.cssSelector("label[for=privacy]"));
        privacyCheckElement.click();

        WebElement submitFormElement = driver.findElement(By.cssSelector(".p-wrap .margin_t_50 input[type=submit]"));
        submitFormElement.sendKeys("");

        if (enableSubmit)
        {
            MacroUtility.sleepUntil(submitTime);
            submitFormElement.click();
        }

        return true;
    }

    public void initialize(String port)
    {
        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("debuggerAddress", "127.0.0.1:" + port);

        driver = new ChromeDriver(chromeOptions);
    }
}
