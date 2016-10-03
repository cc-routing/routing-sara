/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.certicon.routing.algorithm.sara.preprocessing.overlay;

import cz.certicon.routing.algorithm.sara.preprocessing.assembly.Assembler;
import cz.certicon.routing.algorithm.sara.preprocessing.assembly.GreedyAssembler;
import cz.certicon.routing.algorithm.sara.preprocessing.filtering.Filter;
import cz.certicon.routing.algorithm.sara.preprocessing.filtering.NaturalCutsFilter;
import cz.certicon.routing.data.GraphDAO;
import cz.certicon.routing.data.SqliteGraphDAO;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.model.graph.preprocessing.ContractGraph;
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
        boolean runPunch = false;
    }

    @Getter
    SaraSetup setup;

    public OverlayCreator() {
        setup = new SaraSetup();
    }

    public OverlayCreator(SaraSetup setup) {
        this.setup = setup;
    }

    public SaraGraph GetSaraGraph() {
        try {

            if (this.setup.randomSeed > 0) {
                RandomUtils.setSeed(this.setup.randomSeed);
            }

            Properties properties = new Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream("spatialite.properties");
            properties.load(in);
            in.close();

            int maxCellSize = this.setup.maxCellSize;
            double cellRatio = this.setup.cellRatio;
            double coreRatioInverse = this.setup.coreRatioInverse;
            double lowIntervalProbability = this.setup.lowIntervalProbability;
            double lowerIntervalLimit = this.setup.lowerIntervalLimit;
            GraphDAO graphDAO = new SqliteGraphDAO(properties);
            SaraGraph sara = null;

            if (this.setup.runPunch) {
                Graph graph = graphDAO.loadGraph();

                Filter filter = new NaturalCutsFilter(cellRatio, coreRatioInverse, maxCellSize);
                ContractGraph filteredGraph = filter.filter(graph);
                Assembler assembler = new GreedyAssembler(lowIntervalProbability, lowerIntervalLimit, maxCellSize);
                sara = assembler.assemble(graph, filteredGraph);
                graphDAO.saveGraph(sara);
            } else {
                sara = graphDAO.loadSaraGraph();
            }

            return sara;

        } catch (Exception ex) {
            return null;
        }
    }

    public OverlayBuilder CreateBuilder() {
        SaraGraph sara = this.GetSaraGraph();
        if (sara == null) {
            return null;
        }
        OverlayBuilder builder = new OverlayBuilder(sara);
        return builder;
    }

    public OverlayBuilder CreateOverlays() {
        OverlayBuilder builder = this.CreateBuilder();
        if (builder != null) {
            builder.buildOverlays();
        }
        return builder;
    }
}
