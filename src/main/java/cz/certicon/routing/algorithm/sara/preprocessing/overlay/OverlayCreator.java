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
import cz.certicon.routing.model.basic.IdSupplier;
import cz.certicon.routing.model.graph.Graph;
import cz.certicon.routing.model.graph.SaraGraph;
import cz.certicon.routing.utils.RandomUtils;
import java.io.InputStream;
import java.util.Properties;
import lombok.Getter;
import lombok.Setter;

/**
 * Data loader, creates instance of the OverlayBuilder.
 *
 * @author Blahoslav Potoček <potocek@merica.cz>
 */
public class OverlayCreator {

    private final OverlayCreatorSetup setup;

    public OverlayCreator(OverlayCreatorSetup setup) {
        this.setup = setup;
    }

    public SaraGraph getSaraGraph() {
        try {

            Properties properties = setup.getDaoProperties();

            if (properties == null) {
                properties = new Properties();
                InputStream in = getClass().getClassLoader().getResourceAsStream("spatialite.properties");
                properties.load(in);
                in.close();
            }

            String url = properties.getProperty("url");
            System.out.println(url);

            GraphDAO graphDAO = new SqliteGraphDAO(properties);
            SaraGraph sara = null;

            IdSupplier cellId = new IdSupplier(0);

            PreprocessingInput input = setup.getPreprocessingInput();

            if (input == null) {

                sara = graphDAO.loadSaraGraph();

            } else {
                if (this.setup.randomSeed > 0) {
                    RandomUtils.setSeed(this.setup.randomSeed);
                }
                Graph graph = graphDAO.loadGraph();
                BottomUpPreprocessor bottomUp = new BottomUpPreprocessor();
                sara = bottomUp.preprocess(graph, input, cellId);
                graphDAO.saveGraph(sara);
            }

            return sara;

        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public OverlayBuilder createBuilder() {
        SaraGraph sara = this.getSaraGraph();
        OverlayBuilder builder = new OverlayBuilder(sara, this.setup.getBuilderSetup());
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
