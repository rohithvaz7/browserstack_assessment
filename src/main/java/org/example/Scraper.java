package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    public static List<String> scrapeArticles(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<String> titles = new ArrayList<>();

        // Navigate to Opinion Section
        WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='opinion']")));
        opinionLink.click();

        // Scrape Articles
        List<WebElement> articles = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".c h2 a")));

        for (int i = 0; i < Math.min(5, articles.size()); i++) {
            WebElement article = articles.get(i);
            String title = article.getText();
            String articleUrl = article.getAttribute("href");
            titles.add(title);

            System.out.println("\nArticle " + (i + 1) + ": " + title);
            System.out.println("URL: " + articleUrl);

            // Open article
            driver.get(articleUrl);

            // Download Image
            try {
                WebElement image = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("figure img")));
                String imageUrl = image.getAttribute("src");
                saveImage(imageUrl, "article_" + (i + 1) + ".jpg");
            } catch (Exception e) {
                System.out.println("No image found.");
            }

            driver.navigate().back();
        }
        return titles;
    }

    private static void saveImage(String imageUrl, String fileName) throws IOException {
        FileUtils.copyURLToFile(new URL(imageUrl), new File(System.getProperty("user.home") + "/Downloads/" + fileName));
        System.out.println("Saved Image: " + fileName);
    }
}
