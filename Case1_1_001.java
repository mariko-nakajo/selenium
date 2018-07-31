package jp.co.mariko.selenium.wass.cases;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.co.mariko.selenium.wass.SettingBean;
import jp.co.mariko.selenium.wass.Util;

public class Case1_1_001 extends CaseA {

	public List<File> execute(WebDriver driver, WebDriverWait wait,
			String expected, SettingBean setting) {

		List<File> scrShots = new ArrayList<>();

		// SSO login
		driver.get("http://sso.mariko.co.jp/");
		// SSOuser ID
		WebElement eID = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
		Util.logTitle(driver.getTitle());
		scrShots.add(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));
		eID.sendKeys(setting.getAdminID());
		// SSOuser password
		WebElement ePass = driver.findElement(By.id("password"));
		ePass.sendKeys(setting.getAdminPassSSO());
		// login
		eID.submit();
		// WASS link
		WebElement eWASS = wait.until(ExpectedConditions.elementToBeClickable(By.id("wassLink")));
		Util.logTitle(driver.getTitle());
		scrShots.add(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));
		eWASS.click();

		// WASS menu
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
			try {
				driver.findElement(By.name("contents"));
			} catch (NoSuchElementException e) {
				continue;
			}
			break;
		}
//		driver.switchTo().window("wasswin");
		driver.switchTo().defaultContent();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.name("contents")));
		Util.logTitle(driver.getCurrentUrl());
		scrShots.add(((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE));
		// confirm title
		driver.switchTo().frame("contents");
		List<WebElement> eTitle = driver.findElements(By.className("title"));

		if (eTitle.size() == 0) {
			Util.logResult(getCaseNo(), "NG", "メニュー名", "none", "");
		} else if (eTitle.get(0).getText().equals(expected)) {
			Util.logResult(getCaseNo(), "OK", "メニュー名", eTitle.get(0).getText(), "");
		} else {
			Util.logResult(getCaseNo(), "NG", "メニュー名", eTitle.get(0).getText(), "expected：" + expected);
		}

		return scrShots;

	}
}
