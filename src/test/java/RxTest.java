import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;

public class RxTest {


    @Test
    public void TestSubscribeOnWithObservableJust() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(new SystemOutObserver<>());

        Observable
                .just("test1", "test2", "test3")
                .subscribeOn(Schedulers.io())
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("test1", "test2", "test3");
    }

    @Test
    public void TestSubscribeOnWithSubject() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(new SystemOutObserver<>());

        PublishSubject<String> subject = PublishSubject.create();
        subject
                .subscribeOn(Schedulers.io())
                .subscribe(testSubscriber);
        subject.onNext("test1");
        subject.onNext("test2");
        subject.onNext("test3");
        subject.onCompleted();

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("test1", "test2", "test3");
    }

    @Test
    public void TestSubscribeToSubjectAfterOnCompleted() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(new SystemOutObserver<>());

        AsyncSubject<String> subject = AsyncSubject.create();
        subject.onNext("test1");
        subject.onNext("test2");
        subject.onCompleted();
        subject.subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("test2");
    }

    static class SystemOutObserver<T> implements Observer<T> {

        @Override
        public void onCompleted() {
            System.out.println("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("onError [" + e + "]");
        }

        @Override
        public void onNext(T e) {
            System.out.println("onNext [" + e + "]");
        }
    }

}
