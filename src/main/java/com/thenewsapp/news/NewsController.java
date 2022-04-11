package com.thenewsapp.news;

import com.kwabenaberko.newsapilib.NewsApiClient;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.request.EverythingRequest;
import com.kwabenaberko.newsapilib.models.request.TopHeadlinesRequest;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;


@RestController
public class NewsController {
    NewsApiClient newsApiClient = new NewsApiClient("C6f09e34ea6e46ba9417e56cec6198df");

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
                                                                   @RequestParam(defaultValue = "1") String page){
        final DeferredResult<List<Article>> articles = new DeferredResult<>();

        newsApiClient.getTopHeadlines(
                new TopHeadlinesRequest
                        .Builder()
                        .country("in")
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

}
