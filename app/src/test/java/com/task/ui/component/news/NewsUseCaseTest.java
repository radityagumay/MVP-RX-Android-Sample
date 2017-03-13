package com.task.ui.component.news;

import com.task.data.DataRepository;
import com.task.data.remote.dto.NewsItem;
import com.task.data.remote.dto.NewsModel;
import com.task.usecase.NewsUseCase;
import com.task.usecase.NewsUseCase.Callback;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.observers.BlockingObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ahmedeltaher on 3/8/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class NewsUseCaseTest {

//    @Rule
//    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();

    @Mock
    DataRepository dataRepository;
    @Mock
    Callback callback;


    NewsUseCase newsUseCase;
    CompositeDisposable compositeDisposable;
    Observable<NewsModel> newsModelObservable;
    TestSubscriber<NewsModel> newsModelTestSubscriber;
    TestModelsGenerator testModelsGenerator;
    NewsModel newsModel;

    @Before
    public void setUp() throws Exception {
        testModelsGenerator = new TestModelsGenerator();
        newsModelTestSubscriber = new TestSubscriber<>();
        compositeDisposable = new CompositeDisposable();
    }

    @Test
    public void testGetNewsSuccessful() {
        newsModel = (NewsModel) testModelsGenerator.getNewsSuccessfulModel().getData();
        newsModelObservable = Observable.just(newsModel);
        when(dataRepository.requestNews()).thenReturn(newsModelObservable);
        when(dataRepository.requestNews().observeOn(AndroidSchedulers.mainThread()))
                .thenReturn(newsModelObservable.observeOn(Schedulers.computation()));
        newsUseCase = new NewsUseCase(dataRepository, compositeDisposable);
        newsUseCase.getNews(callback);
        verify(callback, times(1)).onSuccess(any(NewsModel.class));
        verify(callback, never()).onFail();
    }

    @Test
    public void testGetNewsFail() {
        NewsModel newsModel = (NewsModel) testModelsGenerator.getNewsErrorModel().getData();
        newsModelObservable = Observable.just(newsModel);
        when(dataRepository.requestNews()).thenReturn(newsModelObservable);
        newsUseCase = new NewsUseCase(dataRepository, compositeDisposable);
        newsUseCase.getNews(callback);
        verify(callback, never()).onSuccess(any(NewsModel.class));
        verify(callback, times(1)).onFail();
    }

    @Test
    public void searchByTitleSuccess() {
        String stup = "this is news Title";
        newsUseCase = new NewsUseCase(dataRepository, compositeDisposable);
        NewsItem newsItem = newsUseCase.searchByTitle(testModelsGenerator.generateNewsModel(stup).getNewsItems(), stup);
        assertNotNull(newsItem);
        assertEquals(newsItem.getTitle(), stup);
    }

    @Test
    public void searchByTitleFail() {
        String stupTitle = "this is news Title";
        String stupSearch = "search title";
        newsUseCase = new NewsUseCase(dataRepository, compositeDisposable);
        NewsItem newsItem = newsUseCase.searchByTitle(testModelsGenerator.generateNewsModel(stupTitle).getNewsItems(), stupSearch);
        assertEquals(newsItem, null);
    }
}
