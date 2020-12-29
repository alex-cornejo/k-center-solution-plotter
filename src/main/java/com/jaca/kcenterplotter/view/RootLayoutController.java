
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    private Set<RowConstraints> rows = new HashSet<>();
    private boolean canvasLoaded = false;

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

    @FXML
    private TextField tfFitness;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void initCanvas() {
        canvas = new Canvas(pane.getWidth(), pane.getHeight());
        pane.getChildren().add(canvas);
    }

    private void validSolution() {
        int n = coordinates.size();
        int[] repetitions = new int[n];
        for (Center c : kcSolution.getCenters()) {
            for (int i : c.getNodes()) {
                repetitions[i]++;
            }
        }
        for (int i = 0; i < n; i++) {
            if (repetitions[i] != 1) {
                System.out.printf("vertex %d is assigned %d times.%n", i, repetitions[i]);
            }
        }
    }

    @FXML
    public void onActionbLoadInstance() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open KCenter Solution File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(this.stage);
        if (selectedFile != null) {
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
            clearGridPane();
            tfFitness.setText("");
        }
    }

    @FXML
    public void onActionbLoadSolution() {
        tfOutliers.setText(Integer.toString(kcSolution.getOutliers().length));
        tfK.setText(Integer.toString(kcSolution.getCenters().length));
        drawKCSolution();
        drawTags();
    }

    @FXML
    public void onActionbLoadAssigments() {
        double fitness = kcSolution.computeFitness(FileUtil.getAdjacencyMatrix(coordinates));
        System.out.printf("Fitness is: %f\n", fitness);
        tfFitness.setText(String.format("%.2f", fitness));
        validSolution();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int i = 0;
        for (Center center : kcSolution.getCenters()) {
            gc.setStroke(colors[i % colors.length]);
            for (int node : center.getNodes()) {
                if (node != center.getCenter()) {
                    gc.strokeLine(
                            //center
                            coordsNorm.get(center.getCenter())[0],
                            coordsNorm.get(center.getCenter())[1],

                            // node
                            coordsNorm.get(node)[0] + radiusNode / 2,
                            coordsNorm.get(node)[1] + radiusNode / 2);
                }
            }
            i++;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

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
            gc.setFill(colors[i % colors.length]);

            double x = coordsNorm.get(center.getCenter())[0];
            double y = coordsNorm.get(center.getCenter())[1];
            double[] coordX = {x - radiusNode, x, x + radiusNode};
            double[] coordY = {y + radiusNode, y - radiusNode, y + radiusNode};

            gc.fillPolygon(coordX, coordY, 3);    // print center
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

    private void clearGridPane() {
        if (gpDetails.getRowConstraints().size() > 1) {
            gpDetails.getRowConstraints().removeAll(rows);
            rows.clear();
            gpDetails.getChildren().removeIf(node -> GridPane.getRowIndex(node) != null);
        }
    }

    private void drawTags() {
        clearGridPane();
        int rowsCount = kcSolution.getCenters().length;
        for (int i = 0; i < rowsCount; i++) {
            Rectangle rectangle = new Rectangle(30, 20);
            rectangle.setFill(colors[i % colors.length]);

            Label label = new Label(Integer.toString(kcSolution.getCenters()[i].getNodes().length));
            gpDetails.add(rectangle, 0, i + 1);
            gpDetails.add(label, 1, i + 1);
            RowConstraints row = new RowConstraints(40);
            rows.add(row);
            gpDetails.getRowConstraints().add(row);
        }
    }
}
