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

    private ConnectableFlowable<Path> hotObservableFiles;
    private Disposable subscribe;

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
        Flowable.just(getPath())
                .map(Utils::readFiles)
                .subscribe((files) -> {
                    // create observable and make it hot!
                    hotObservableFiles = Flowable.fromArray(files.toArray(Path[]::new)).publish();
                    // connect to it
                    hotObservableFiles.connect();
                    subscribe = hotObservableFiles.observeOn(Schedulers.computation())
                            .map(Utils::countLines)
                            .subscribe((processedFile) -> {
                                this.getFileProcessedHandler().apply(processedFile);
                            });
                }).dispose();
    }

    @Override
    public void stopAnalyzing() {
        // dispose the subscription and reset the observable
        subscribe.dispose();
        hotObservableFiles.blockingSubscribe();
        hotObservableFiles.reset();
    }
}
