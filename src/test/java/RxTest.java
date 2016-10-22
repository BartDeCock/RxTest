import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;

public class RxTest {


    @Test
    public void TestSubscribeOnWithObservableJust() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(SystemOutObserver.create());

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
        TestSubscriber<String> testSubscriber = TestSubscriber.create(SystemOutObserver.create());

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

    @Test
    public void TestOnCompletedDownStream() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(SystemOutObserver.create());
        PublishSubject<String> stream1 = PublishSubject.create();
        PublishSubject<String> stream2 = PublishSubject.create();
        Observable<String> mergedStream = stream1.mergeWith(stream2);
        mergedStream.subscribe(testSubscriber);

        stream1.onNext("testStream1_value1");
        stream2.onNext("testStream2_value1");
        stream1.onCompleted();
        stream1.onNext("testStream1_value2");
        stream2.onNext("testStream2_value2");
        stream2.onCompleted();
        stream1.onNext("testStream1_value3");
        stream2.onNext("testStream2_value3");

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("testStream1_value1", "testStream2_value1", "testStream2_value2");
    }

    @Test
    public void TestChainedStream() throws Exception {
        TestSubscriber<String> testSubscriber = TestSubscriber.create(SystemOutObserver.create());
        PublishSubject<String> stream1 = PublishSubject.create();
        PublishSubject<String> stream2 = PublishSubject.create();
        Observable<String> mergedStream = stream1.mergeWith(stream2);
        mergedStream.subscribe(testSubscriber);

        stream1.onNext("testStream1_value1");
        stream2.onNext("testStream2_value1");
        stream1.onCompleted();
        stream1.onNext("testStream1_value2");
        stream2.onNext("testStream2_value2");
        stream2.onCompleted();
        stream1.onNext("testStream1_value3");
        stream2.onNext("testStream2_value3");

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertCompleted();
        testSubscriber.assertNoErrors();
        testSubscriber.assertValues("testStream1_value1", "testStream2_value1", "testStream2_value2");
    }


    static class SystemOutObserver<T> implements Observer<T> {

        static <K> SystemOutObserver<K> create() {
            return new SystemOutObserver<>();
        }

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
