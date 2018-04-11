package com.example.administrator.rxjavademo;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            println("JG  InterruptedException");

            e.printStackTrace();
        }
    }

    public void println(Object obj) {
        System.out.println(obj);
    }


    public void test() {


        //Future
        Future<String> futrue = Executors.newSingleThreadExecutor().submit(new Callable<String>() {

            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "maplejaw";
            }
        });

        Observable.fromFuture(futrue)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {

                    }
                });


        Observable<String> defer = Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                return Observable.just("");
            }
        });


        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {

            }
        }, BackpressureStrategy.ERROR).doOnCancel(new Action() {
            @Override
            public void run() throws Exception {

            }
        })
                .unsubscribeOn(Schedulers.newThread())
                .doOnCancel(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * RxJava的基础调用方式，这里的监听者使用Observer
     */
    public void commonEmitter_observer(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                //只能使用一次onError
//                e.onError(new Throwable("test data"));
//                e.onError(new Throwable("test data"));
//                e.onComplete();
                e.onNext(3);
            }
        }).subscribe(new Observer<Integer>() {
            Disposable disposable;

            @Override
            public void onSubscribe(Disposable d) {
                this.disposable = d;
                Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "onNext: " + value);
                if (value == 2) {
                    disposable.dispose();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        });
    }

    /**
     * RxJava的基础调用方式，这里的监听者使用Consumer
     * 注意点：
     * 1.consumer中不能使用onError，会造成崩溃
     */
    public void commonEmitter_consumer(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);

                //不能使用onError，会造成崩溃
                e.onError(new Throwable("test data"));
//                e.onComplete();
                e.onNext(3);
            }
        }).subscribe(new Consumer<Integer>() {

            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "accept: " + integer);
            }
        });
    }

    /**
     * RxJava的异步使用
     * 注意点：
     * <p>
     * 1.delay方法会将事件放到computationThreadPool中去发送
     * 2.flatmap 会将延迟的事件，无序的插入，并一次性发送
     * 3.concatmap 会将延迟的事件，有序的插入，并且不是一次性发送，而是一次一次，有间隔的发送
     */
    public void common_emitter_asynchronization(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
//                Log.e(TAG, "subscribe: " + " currentThread :" + Thread.currentThread().getName());
            }
        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
//                        Log.e(TAG, "doOnNext accept: " + integer + " currentThread :" + Thread.currentThread().getName());
                    }
                })
//                .observeOn(Schedulers.computation())
//                .map(new Function<Integer, String>() {
//                    @Override
//                    public String apply(Integer integer) throws Exception {
//                        Log.e(TAG, "map apply: " + integer + " currentThread :" + Thread.currentThread().getName());
//                        return String.valueOf(integer * 100);
//                    }
//                })
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer s) throws Exception {
//                return Observable.create(new ObservableOnSubscribe<String>() {
//                    @Override
//                    public void subscribe(ObservableEmitter<String> e) throws Exception {
//                        e.onNext("concatMap subscribe" + "  current thread" + Thread.currentThread().getName());
//                    }
//                });
                        return Observable.just(String.valueOf(s))
                                .delay(500, TimeUnit.MILLISECONDS);
                        //delay方法会将事件放到computationThreadPool中去发送
                        //flatmap 会将延迟的事件，无序的插入，并一次性发送
                        //concatmap 会将延迟的事件，有序的插入，并且不是一次性发送，而是一次一次，有间隔的发送
                    }
                }).subscribe(new Consumer<String>() {

            @Override
            public void accept(String string) throws Exception {
                Log.e(TAG, "subscribe accept: " + string + " currentThread :" + Thread.currentThread().getName());
            }
        });

    }

    /**
     * zip操作符
     * <p>
     * 注意点：
     * 1.合并后的上游线程由合并前的两个上游线程决定，在哪个线程合并的，上游线程就是用当前合并线程。
     */
    public void emitter_zip(View view) {
        Observable<Integer> integerObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException exception) {
//                        exception.printStackTrace();
//                    }
                    e.onNext(i);
                    Log.i(TAG, "subscribe: integer  " + i + getThreadName());
                }
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        Observable<String> stringObservable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                e.onNext("a");
                Log.i(TAG, "subscribe: string a" + getThreadName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                e.onNext("b");
                Log.i(TAG, "subscribe: string b" + getThreadName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                e.onNext("c");
                Log.i(TAG, "subscribe: string c" + getThreadName());
//                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.zip(integerObservable, stringObservable, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer integer, String s) throws Exception {
                String s1 = integer + s;
                Log.i(TAG, "apply: " + s1 + getThreadName());
                return s1;
            }
        }).
                //下面这行代码是无效的，上游的线程由合并的两个上游线程决定，在哪个线程完成合并，这里就是哪个线程
                        subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: " + s + getThreadName());
                    }
                });
    }


    /**
     * 使用flowable替代observable，即使用响应式编码，解决背压问题。
     * <p>
     * flowable的性能要比observable差点
     * 所以对于不存在背压的情况下，优先使用observable
     * <p>
     * 注意点：
     * <p>
     * 背压概念：如果上游发送消息过快或者下游处理消息过慢，这样造成上下游消息处理的不平衡，这种情况可以称作背压（BackPressure）。
     * 背压的解决办法：这个时候需要使用响应式的请求，来解决问题。
     * <p>
     * <p>
     * BackpressureStrategy.ERROR 表示出现异常的时候，进行报错
     * 1.同步情况下的request,只要上游发送的事件个数大于请求个数，就会在onError中抛出lack of request异常。但是上游报错后上游事件继续发送
     * 2.异步情况下的,当上游的事件超过128个事件缓存的时候，在onError中抛出lack of request 异常,上游事件仍然继续发送
     * <p>
     * BackpressureStrategy.BUFFER 表示用无限大的缓存池
     * 这种策略下，无论是同步还是异步，如果上游事件没有被消耗掉，会将所有未消耗掉的事件加入缓存池，并等待下游进行消耗掉
     * <p>
     * BackpressureStrategy.DROP 表示上游未消耗掉的事件丢弃掉
     * 同步情况下，如果没有马上消耗掉，直接丢弃
     * 异步情况下，缓存池满后，
     * (关于缓存池，如果在上游发送事件的时候，下游没有消耗，那么缓冲池是大小是128，如果发送的同时，下游一直消耗，那么是96，原因需要查看源码)
     * <p>
     * BackpressureStrategy.LASTED
     * 同步情况下,如果没有马上消耗掉，会保留最后一个事件，等待下游消耗
     * 异步情况下，缓存池满后（缓存池同DROP策略），丢弃除最后一个事件之外的其他事件，等待下游消耗
     * <p>
     * 怎么获取下游请求个数，来进行相应的处理？
     * FlowableEmitter中的requested方法可以获取下游个数。
     * 在BackpressureStrategy.ERROR策略下，如果上下游在同一个线程，那么获取的数量为下游设置的request数据。如果不在同一个线程，那么获取的数据为上游缓冲池的数量。
     * 其他策略可以按同样的方式理解。
     */
    public void flowable_emitter(View view) {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 10; i++) {
                    Log.i(TAG, "subscribe: " + i);
                    e.onNext(i);
                }
                //获取下游请求的个数，进行相应的响应
//                long requested = e.requested();
                e.onComplete();
            }
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError: " + t);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete");
                    }
                });
    }

    public Subscription subscription;

    public void request(View view) {
        if (subscription == null) {
            return;
        }
        subscription.request(1000);
    }

    public String getThreadName() {
        return " current thread" + Thread.currentThread().getName();
    }


    /**
     * 实例，读取一个文件，边读边用
     * <p>
     * 注意点：
     * 1.使用new BufferedReader(new InputStreamReader(open)) 可以将字节流转换成文本流
     * 2.e.onComplete只有在缓存池中的所有事件都发送完之后，才会触发下游的onComplete
     * <p>
     * <p>
     * 疑问点：assets目录下对于txt文件，在打包成apk的时候，会进行压缩，无法读取，报
     * java.io.FileNotFoundException This file can not be opened as a file descriptor; it is probably compressed
     * 有两种解决办法：
     * 第一种是需要转换成png、MP3
     * 第二种是是修改gradle
     * aaptOptions {
     * noCompress "txt"
     * }
     * 但是无论哪种方法，通过new FileReader(assetFileDescriptor.getFileDescriptor())获取的文本流，存在乱码。待研究！
     */
    public void request_cache_demo(View view) {
        Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> e) throws Exception {
                //
//                AssetFileDescriptor assetFileDescriptor = getAssets().openFd("test.png");
//                FileReader fileReader = new FileReader(assetFileDescriptor.getFileDescriptor());
//                BufferedReader bufferedReader = new BufferedReader(fileReader);

                InputStream open = getAssets().open("test.txt");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
                String line;
                while ((line = bufferedReader.readLine()) != null && !e.isCancelled()) {
                    while (true) {
                        if (e.requested() > 0) {
                            break;
                        }
                    }
                    if (!e.isCancelled()) {
                        e.onNext(line);
                    }
                }
                bufferedReader.close();
//                fileReader.close();
//                assetFileDescriptor.close();
                open.close();
                //只有当缓存池中的数据全部取完之后，才会执行onCoplete
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<String>() {

                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(String s) {
                        try {
                            Thread.sleep(1000);
                            subscription.request(1);
                            Log.i(TAG, s);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.i(TAG, "onError" + t);

                    }

                    @Override
                    public void onComplete() {
                        //只有当缓存池中的数据全部取完之后，才会执行onCoplete
                        Log.i(TAG, "onComplete");
                    }
                });
    }

    /**
     * 注意点：
     * 1. cancel ：Request the Publisher to stop sending data and clean up resources。说明这里会释放资源，防止内存泄漏
     * 2. cancel方法会唤醒下游线程，如果线程处于阻塞状态，那么会抛出异常。
     */
    public void cancel_emitter(View view) {
        Log.i(TAG, "cancel_emitter:");

        subscription.cancel();
    }

    /**
     * 注意点：
     * 1.取消之后 如果再次调用request，也不会触发新的事件。因为cancel已经清空了缓冲池。
     */
    public void cancel_emitter_request(View view) {
        subscription.request(1);
        Log.i(TAG, "cancel_emitter_request");
    }


    public void retryWhen(View view) {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onError(new Throwable("123"));
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                //为什么这么返回，会进行重试呢？
//                return throwableObservable;
                //目前只能是这种形式
                return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        //返回error、complete表示不重试，其他任何值表示重试
//                        return Observable.error(throwable);
//                        return Observable.just(true);
                        //设置闹钟，每个指定时间，进行重试
                        return Observable.timer(2000,TimeUnit.MILLISECONDS);
                    }
                });
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                Log.i(TAG, value + "");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e + "");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
