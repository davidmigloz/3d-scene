package com.davidmiguel.scene_3d.view;

import com.davidmiguel.scene_3d.engine.Camera;
import com.davidmiguel.scene_3d.engine.Engine;
import com.davidmiguel.scene_3d.meshes.Mesh;
import com.davidmiguel.scene_3d.utils.FileUtils;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.vecmath.Vector3d;
import java.io.File;

/**
 * GuiController.
 *
 * @author davidmigloz
 * @since 18/06/2016
 */
public class GuiController {

    @FXML
    private MenuItem openMesh;
    @FXML
    private MenuItem cube;
    @FXML
    private MenuItem cylinder;
    @FXML
    private MenuItem quit;
    @FXML
    private MenuItem about;
    @FXML
    private Canvas canvas;
    @FXML
    private Label status;
    @FXML
    private Label fps;

    private Engine engine;
    private Mesh[] meshes;
    private Camera camera;
    private static long t0;

    @FXML
    private void initialize() {
        // Initial setup
        status.setText("Starting...");
        fps.setText("0");
        canvas.getGraphicsContext2D().setLineWidth(1);
        engine = new Engine(canvas.getGraphicsContext2D());
        meshes = new Mesh[0];
        camera = new Camera(new Vector3d(0, 0, 10), new Vector3d(0, 0, 0));
        startRenderingLoop();
    }

    @FXML
    private void handleOpenMesh() {
        Stage primaryStage = (Stage) canvas.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON (*.json;*.babylon)", "*.json", "*.babylon"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setTitle("Open mesh...");
        File f = fileChooser.showOpenDialog(primaryStage);
        if (f != null) {
            addMeshesFromFile(f);
        }
    }

    private void addMeshesFromFile(File f) {
        meshes = FileUtils.parseMeshFromJSON(f);
    }

    private void startRenderingLoop() {
        // Rendering loop (50hz)
        Timeline tl = new Timeline();
        tl.setCycleCount(Animation.INDEFINITE);
        t0 = System.currentTimeMillis(); // For computation of fps
        KeyFrame frame = new KeyFrame(Duration.millis(20), event -> {
            // Clear the screen and all associated pixels with white ones
            engine.clear();
            //  Update the various position & rotation values of our meshes
            for (Mesh mesh : meshes) {
                mesh.getRotation().x += 0.01;
                mesh.getRotation().y += 0.01;
            }
            // Render them into the back buffer by doing the required matrix operations
            engine.render(camera, meshes);
            // Display them on screen by flushing the back buffer data into the front buffer
            engine.draw();
            // Update fps info
            long t1 = System.currentTimeMillis();
            fps.setText(Long.toString(Math.round(1 / ((t1 - t0) / 1000.0))));
            t0 = t1;
        });
        tl.getKeyFrames().add(frame);
        tl.play();
    }
}
