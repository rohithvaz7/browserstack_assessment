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
        List<String> articlesData = new ArrayList<>();

        // Navigate to Opinion Section
        try {
            WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='opinion']")));
            opinionLink.click();
        } catch (TimeoutException e) {
            System.out.println("⚠️ Opinion section not found.");
            return articlesData;
        }

        for (int i = 0; i < 5; i++) {  // Process up to 5 articles
            try {
                // **Re-locate article elements to avoid stale references**
                List<WebElement> articles = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".c h2 a")));

                if (i >= articles.size()) break;  // Stop if there are fewer than 5 articles

                WebElement article = articles.get(i);
                String title = article.getText();
                String articleUrl = article.getAttribute("href");

                // Open article page
                driver.get(articleUrl);

                // **Extract Article Content**
                List<WebElement> paragraphs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article p")));
                StringBuilder content = new StringBuilder();

                for (WebElement paragraph : paragraphs) {
                    content.append(paragraph.getText()).append("\n");
                }

                // Save title & content
                articlesData.add("Title: " + title + "\nContent:\n" + content.toString());
                System.out.println("\n📰 Article " + (i + 1) + ": " + title);
                System.out.println("📜 Content: " + (content.length() > 200 ? content.substring(0, 200) + "..." : content));

                // Download Image
                try {
                    WebElement image = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("figure img")));
                    String imageUrl = image.getAttribute("src");
                    saveImage(imageUrl, "article_" + (i + 1) + ".jpg");
                } catch (TimeoutException | IOException e) {
                    System.out.println("⚠️ No image found for this article.");
                }

                // **Navigate back and wait for elements to reappear**
                driver.navigate().back();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".c h2 a")));

            } catch (StaleElementReferenceException e) {
                System.out.println("🔄 Stale element detected. Retrying...");
                i--; // Retry current article
            } catch (TimeoutException e) {
                System.out.println("⚠️ Timeout while processing article " + (i + 1));
            }
        }
        return articlesData;
    }

    private static void saveImage(String imageUrl, String fileName) throws IOException {
        FileUtils.copyURLToFile(new URL(imageUrl), new File(System.getProperty("user.home") + "/Downloads/" + fileName));
        System.out.println("📸 Saved Image: " + fileName);
    }
}

