/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.sara.preprocessing.BottomUpPreprocessor;
import cz.certicon.routing.algorithm.sara.preprocessing.PreprocessingInput;
import cz.certicon.routing.data.GraphDAO;
import cz.certicon.routing.data.SqliteGraphDAO;
import cz.certicon.routing.model.basic.MaxIdContainer;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.utils.RandomUtils;
import java.io.InputStream;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayCreator {

    @Getter
    @Setter
    public class SaraSetup {

        long randomSeed = -1;
        int maxCellSize = 500;
        double cellRatio = 1;
        double coreRatioInverse = 10;
        double lowIntervalProbability = 0.03;
        double lowerIntervalLimit = 0.6;
        int numberOfAssemblyRuns = 100; //100-1000?
        boolean runPunch = false;
        int layerCount = 1;
    }

    @Getter
    SaraSetup setup;

    public OverlayCreator() {
        setup = new SaraSetup();
    }

    public OverlayCreator(SaraSetup setup) {
        this.setup = setup;
    }

    public SaraGraph getSaraGraph() {
        try {

            if (this.setup.randomSeed > 0) {
                RandomUtils.setSeed(this.setup.randomSeed);
            }

            Properties properties = new Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream("spatialite.properties");
            properties.load(in);
            in.close();

            GraphDAO graphDAO = new SqliteGraphDAO(properties);
            SaraGraph sara = null;

            MaxIdContainer cellId = new MaxIdContainer(0);

            if (this.setup.runPunch) {
                Graph graph = graphDAO.loadGraph();

                PreprocessingInput input = new PreprocessingInput(
                        this.setup.maxCellSize,
                        this.setup.cellRatio,
                        this.setup.coreRatioInverse,
                        this.setup.lowIntervalProbability,
                        this.setup.lowerIntervalLimit,
                        this.setup.numberOfAssemblyRuns,
                        this.setup.layerCount
                );
                BottomUpPreprocessor bottomUp = new BottomUpPreprocessor();
                sara = bottomUp.preprocess(graph, input, cellId);
                graphDAO.saveGraph(sara);
            } else {
                sara = graphDAO.loadSaraGraph();
            }

            return sara;

        } catch (Exception ex) {
            return null;
        }
    }

    public OverlayBuilder createBuilder() {
        SaraGraph sara = this.getSaraGraph();
        if (sara == null) {
            return null;
        }
        OverlayBuilder builder = new OverlayBuilder(sara);
        return builder;
    }

    public OverlayBuilder createOverlays() {
        OverlayBuilder builder = this.createBuilder();
        if (builder != null) {
            builder.buildOverlays();
        }
        return builder;
    }
}
