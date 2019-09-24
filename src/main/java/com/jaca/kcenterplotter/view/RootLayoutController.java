
package com.jaca.kcenterplotter.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaca.kcenterplotter.solution.Center;
import com.jaca.kcenterplotter.solution.KCSolution;
import com.jaca.kcenterplotter.util.FileUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {

    private String instancePath;
    private final int paddingPane = 30;
    private final float radiusNode = 7;
    private List<double[]> coordinates;
    private List<int[]> coordsNorm;
    private Stage stage;
    private Canvas canvas;
    private final Color[] colors = {Color.GREEN, Color.BLUE, Color.CADETBLUE, Color.DARKGRAY, Color.PURPLE, Color.GOLD,
            Color.BLACK, Color.YELLOWGREEN, Color.TOMATO, Color.PINK};
    private KCSolution kcSolution;

    @FXML
    private Label lInstance;

    @FXML
    private Pane pane;

    @FXML
    private TextField tfK;

    @FXML
    private TextField tfOutliers;

    @FXML
    private GridPane gpDetails;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void initCanvas() {
        canvas = new Canvas(pane.getWidth(), pane.getHeight());
        pane.getChildren().add(canvas);
    }

    @FXML
    public void onActionbLoadInstance() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open KCenter Solution File");
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("JSON", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(this.stage);
        lInstance.setText(selectedFile.getName());
        instancePath = selectedFile.getPath();

        Gson gson = new Gson();
        BufferedReader br = new BufferedReader(new FileReader(instancePath));

        kcSolution = gson.fromJson(br, new TypeToken<KCSolution>() {
        }.getType());

        coordinates = FileUtil.readNodes(kcSolution.getInstance());

        double xMin = coordinates.get(0)[0];
        double xMax = coordinates.get(0)[0];
        double yMin = coordinates.get(0)[1];
        double yMax = coordinates.get(0)[1];
        for (double[] coordinate : coordinates) {
            if (coordinate[0] < xMin) {
                xMin = coordinate[0];
            }
            if (coordinate[0] > xMax) {
                xMax = coordinate[0];
            }
            if (coordinate[1] < yMin) {
                yMin = coordinate[1];
            }
            if (coordinate[1] > yMax) {
                yMax = coordinate[1];
            }
        }
        coordsNorm = FileUtil.normNodes(coordinates, xMin, xMax, yMin, yMax,
                (int) pane.getWidth() - paddingPane * 2, (int) pane.getHeight() - paddingPane * 2);

        int h = (int) pane.getHeight() - paddingPane;
        for (int[] coordinate : coordsNorm) {
            coordinate[0] += paddingPane;
            coordinate[1] = h - coordinate[1];
        }
        drawNodes();
    }

    @FXML
    public void onActionbLoadSolution() {
        tfOutliers.setText(Integer.toString(kcSolution.getOutliers().length));
        drawKCSolution();
        drawTags();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    boolean canvasLoaded = false;

    private void drawNodes() {
        if (!canvasLoaded) {
            initCanvas();
            canvasLoaded = true;
        } else {
            clearCanvas();
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        for (int[] coordinate : coordsNorm) {
            gc.fillOval(coordinate[0], coordinate[1], radiusNode, radiusNode);
        }

    }

    private void drawKCSolution() {
        if (!canvasLoaded) {
            initCanvas();
            canvasLoaded = true;
        } else {
            clearCanvas();
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();

        int i = 0;
        for (Center center : kcSolution.getCenters()) {
            gc.setFill(colors[i]);
            double[] x = {coordsNorm.get(center.getCenter())[0] - radiusNode, coordsNorm.get(center.getCenter())[0], coordsNorm.get(center.getCenter())[0] + radiusNode};

            double[] y = {coordsNorm.get(center.getCenter())[1] + radiusNode, coordsNorm.get(center.getCenter())[1] - radiusNode, coordsNorm.get(center.getCenter())[1] + radiusNode};

            gc.fillPolygon(x, y, 3);
//            gc.fillOval(coordsNorm.get(center.getCenter())[0], coordsNorm.get(center.getCenter())[1], radiusNode, radiusNode);
            for (int node : center.getNodes()) {
                if (node != center.getCenter()) {
                    gc.fillOval(coordsNorm.get(node)[0], coordsNorm.get(node)[1], radiusNode, radiusNode);
                }
            }
            i++;
        }

        gc.setFill(Color.RED);
        for (int outlier : kcSolution.getOutliers()) {
            gc.fillOval(coordsNorm.get(outlier)[0], coordsNorm.get(outlier)[1], radiusNode, radiusNode);

        }
    }

    private void clearCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getWidth());
    }


    private void drawTags() {
        if (gpDetails.getRowConstraints().size() > 1) {
            int rowsCount = gpDetails.getRowConstraints().size();
            for (int i = 0; i < rowsCount - 1; i++) {
                gpDetails.getRowConstraints().remove(1);
            }
        }
        for (int i = 0; i < kcSolution.getCenters().length; i++) {
            Rectangle rectangle = new Rectangle(30, 20);
            rectangle.setFill(colors[i]);

            Label label = new Label(Integer.toString(kcSolution.getCenters()[i].getNodes().length));
            gpDetails.add(rectangle, 0, i + 1);
            gpDetails.add(label, 1, i + 1);
            gpDetails.getRowConstraints().add(new RowConstraints(40));
        }
    }
}
