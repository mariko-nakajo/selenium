package jp.co.mariko.selenium.wass;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import jp.co.niandc.selenium.wass.cases.CaseA;

public class WassTestMain {

	public static void main(String[] args) {

		SettingBean setting = new SettingBean();
		Map<String, String> expectedMap = new TreeMap<>();

		try {
			List<String> lines = FileUtils.readLines(new File("setting.txt"), "Windows-31j");
			List<String[]> sets = lines.stream()
					.filter(line->!line.startsWith("#"))
					.map(line->line.split("="))
					.collect(Collectors.toList()
			);
			for (String[] set : sets) {
				if (set[0].matches("\\d{1,2}_\\d{1,2}_\\d{3}")) {
					if (set.length < 2) {
						set = new String[]{set[0], ""};
					}
					expectedMap.put(set[0], set[1]);
				} else if (set.length == 2) {

					Method method;
					method = setting.getClass().getMethod("set" + set[0], new Class[]{String.class});
					method.invoke(setting, set[1]);

				} else {
//					Util.log("WARN", "setting.txtに不要な行があります：" + set[0]);
				}
			}
		} catch(Exception e) {
			Util.log("ERROR", "設定ファイルの読み込みに失敗しました: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// WebDriver(IE)
		WebDriver driver = new InternetExplorerDriver();
		WebDriverWait wait = new WebDriverWait(driver, 10);

		// screenShotFiles
		Map<String, List<File>> evidences = new HashMap<>();

		for (String caseNo: expectedMap.keySet()) {
			try {
				Class<?> c = Class.forName("jp.co.niandc.selenium.wass.cases.Case" + caseNo);
				// インスタンスの生成
				CaseA caseObj = (CaseA)c.newInstance();
				caseObj.setCaseNo(caseNo);
				Util.log("INFO", "テスト実行：" + caseNo);
				evidences.put(caseNo, caseObj.execute(driver, wait, expectedMap.get(caseNo), setting));
			} catch (Exception e) {
				Util.log("ERROR", "test caseの呼び出しでエラー:caseNo[" + caseNo + "]" + e.getMessage());
				e.printStackTrace();
				break;
			}
		}
//      //Close the browser
//      driver.quit();

		try {
			File folder = new File(setting.getScreenShotFolder());
			int inc= 1;
			while (folder.exists()) {
				folder = new File(setting.getScreenShotFolder() + inc++);
			}
			folder.mkdir();
			for (String caseNo: evidences.keySet()) {
				List<File> files = evidences.get(caseNo);
				for (int i=0; i<files.size(); i++) {
					FileUtils.copyFile(files.get(i),
							new File(folder.getPath() + "/" + caseNo + "-" + i + ".png"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
