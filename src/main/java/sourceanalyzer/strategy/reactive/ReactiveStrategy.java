package sourceanalyzer.strategy.reactive;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import sourceanalyzer.common.Pair;
import sourceanalyzer.common.Report;
import sourceanalyzer.common.Utils;
import sourceanalyzer.strategy.AbstractAnalyzerStrategy;

import java.nio.file.Path;
import java.util.function.Function;

public class ReactiveStrategy extends AbstractAnalyzerStrategy {

    public ReactiveStrategy(String path, int intervals, int maxLines, int topFilesNumber, Function<Pair<String, Integer>, Void> fileProcessedHandler) {
        super(path, intervals, maxLines, topFilesNumber, fileProcessedHandler);
    }

    @Override
    public Report makeReport() {
        Flowable.just(getPath())
                .map(Utils::readFiles)
                .subscribe((files) -> {
                    Flowable<Path> pathSource = Flowable.fromArray(files.toArray(Path[]::new));
                    pathSource.map(Utils::countLines).subscribe((processedFile) -> {
                        System.out.println(processedFile);
                        this.getProcessedFiles().add(processedFile);
                    }).dispose();
                }).dispose();
        return super.createReport();
    }

    @Override
    public void startAnalyzing() {
        // riempire files leggendo la directory
        Disposable disposable = Flowable.just(getPath())
                .map(Utils::readFiles)
                .subscribe((files) -> {
                    // create observable
                    Flowable<Path> pathSource = Flowable.fromArray(files.toArray(Path[]::new));
                    // make it hot!
                    ConnectableFlowable<Path> hotObservable = pathSource.publish();
                    // connect to it
                    hotObservable.connect();
                    hotObservable.observeOn(Schedulers.computation())
                            .map(Utils::countLines)
                            .subscribe((processedFile) -> {
                                this.getFileProcessedHandler().apply(processedFile);
                                // Thread.sleep(5000);
                            }).dispose();
                });
        disposable.dispose();
    }

    public void test() {

        /*ConnectableObservable<Integer> hotObservable = source.publish();
        hotObservable.connect();*/
    }

    @Override
    public void stopAnalyzing() {

    }
}
