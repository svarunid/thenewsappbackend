package com.thenewsapp.news;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.thenewsapp.user.User;
import com.thenewsapp.user.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;

@Data
class NewsData{
    private String username;
    private News news;
}

@RestController
@CrossOrigin
public class NewsController {
    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${news-api.key}")
    private String apiKey;

    private NewsApiClient newsApiClient;
    @PostConstruct
    void init(){
        newsApiClient = new NewsApiClient(apiKey);
    }

    @GetMapping(path = "/news")
    public DeferredResult<List<Article>> getEverything(@RequestParam String query,
                                                       @RequestParam(defaultValue = "relevancy") String sortBy,
                                                       @RequestParam(defaultValue = "1") String page){
        final DeferredResult<List<Article>> articles = new DeferredResult<>();

        newsApiClient.getEverything(
                new EverythingRequest
                        .Builder()
                        .q(query)
                        .sortBy(sortBy)
                        .pageSize(20)
                        .page(Integer.parseInt(page))
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse articleResponse) {
                        articles.setResult(articleResponse.getArticles());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                articles.setErrorResult(throwable.getMessage());
            }
            });
        return articles;
    }

    @GetMapping(path = "/feed")
    public DeferredResult<List<Article>> getLocationBasedHeadlines(@RequestParam(required = false) String category,
                                                                   @RequestParam(defaultValue = "1") String page,
                                                                   @RequestParam String country){
        final DeferredResult<List<Article>> articles = new DeferredResult<>();

        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest
                        .Builder()
                        .country(country)
                        .category(category)
                        .page(Integer.parseInt(page))
                        .build(),
                new NewsApiClient.ArticlesResponseCallback() {
                    @Override
                    public void onSuccess(ArticleResponse articleResponse) {
                        articles.setResult(articleResponse.getArticles());
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        articles.setErrorResult(throwable.getMessage());
                    }
                });
        return articles;
    }

    @PutMapping("/feed/save")
    public ResponseEntity<String> saveArticle(@RequestBody NewsData newsData){
        News news  = newsRepository.findByUrl(newsData.getNews().getUrl());
        if(news == null){
            newsRepository.save(newsData.getNews());
            news = newsRepository.findByUrl(newsData.getNews().getUrl());
        }
        Set<User> users = news.getUsers() == null ? new HashSet<>() : news.getUsers();
        users.add(userRepository.findByUsername(newsData.getUsername()).get());
        news.setUsers(users);
        newsRepository.save(news);
        return ok("Saved Successfully");
    }
}
