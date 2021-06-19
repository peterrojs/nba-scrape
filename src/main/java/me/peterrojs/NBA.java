package me.peterrojs;

import java.util.List;
import java.util.concurrent.Callable;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

@Command(name = "nba-scrape", mixinStandardHelpOptions = true, version = "1.0.0")
public class NBA implements Callable<Integer> {

  WebDriver driver;

  @Parameters(paramLabel = "player", arity = "1...")
  String[] player;

  public static void main(String[] args) {
    System.exit(new CommandLine(new NBA()).execute(args));
  }

  @Override
  public Integer call() throws InterruptedException {

    WebDriverManager.chromedriver().setup();

    // Instantiate a ChromeDriver class.
    driver = new ChromeDriver();

    // Launch Website
    driver.navigate().to("https://www.nba.com/players");
    handleCookiesPopup("onetrust-accept-btn-handler");

    getToPlayStatsPage(String.join(" ", player));
    extractFromTable();

    driver.close();
    return 0;
  }

  /**
   * Click accept on the cookies popup
   *
   * @param ID The ID of the accept button
   */
  public void handleCookiesPopup(String ID) throws InterruptedException {
    Thread.sleep(1000);
    driver.findElement(By.id(ID)).click();
  }

  /**
   * Get to the player's stats page
   *
   * @param player Name of the player
   */
  public void getToPlayStatsPage(String player) throws InterruptedException {
    Thread.sleep(2000);
    driver.findElement(By.xpath("//*[@id=\"__next\"]/div[2]/div[3]/section/div/div[1]/div/input")).sendKeys(player);
    Thread.sleep(3000);

    // Click on the player to get to his page
    driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[3]/section/div/div[2]/div[" +
      "2]/div/div/div/table/tbody/tr/td[1]/a/div[2]/p[1]")).click();
    Thread.sleep(1000);

    // Open stats
    driver.findElement(By.xpath("/html/body/div[1]/div[2]/div[3]/div/div[1]/div/ul/li[2]/a")).click();
    Thread.sleep(4000);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.scrollBy(0,700)");
    Thread.sleep(10000);
  }

  /**
   * Extract the needed values from the table
   */
  public void extractFromTable() {
    List<?> rows = driver.findElements(By.cssSelector("nba-stat-table.stats-table-next:nth-child(8) > div:nth-child(2)> div:nth-child(1) > " +
    "table:nth-child(1) > tbody:nth-child(2) > tr"));
    for (int i = 0; i < rows.size(); i++) {
      var elementDate = driver.findElement(By.xpath(
        String.format("/html/body/main/div/div/div/div[4]/div/div/div/div/nba-stat-table[1]/div[2]/div[2]/table/tbody/tr[%d]/td", i + 1))).getText();
      var elementPA = driver.findElement(By.xpath(
        String.format("/html/body/main/div/div/div/div[4]/div/div/div/div/nba-stat-table[1]/div[2]/div[1]/table/tbody/tr[%d]/td[10]", i + 1))).getText();
      System.out.println(elementDate + " - " + elementPA);
    }
  }

  /**
   * Return the passed player
   *
   * @return The player which was passed to the command as the main parameter
   */
  public String[] getPlayer() {
    return player;
  }
}
