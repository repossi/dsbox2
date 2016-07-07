package es.uvigo.esei.dsbox.gui.views.panels;

import es.uvigo.esei.dsbox.virtualbox.execution.VirtualBoxDriverSpec;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class VBoxDriverSpecEditorPanel extends ElementPanel {

    private Label vboxVersion;

    private TextField simulationsBasePath;

    private TextField imagesBasePath;

    private ComboBox<String> bridgedInterface;

    private TextField webServerHost;

    private TextField webServerPort;

    private TextField webServerUser;

    private TextField webServerPassword;

    private VirtualBoxDriverSpec driverSpec;

    public VBoxDriverSpecEditorPanel(VirtualBoxDriverSpec driverSpec) {
        this.driverSpec = driverSpec;
        initComponents();
    }

    public VirtualBoxDriverSpec getDriverSpec() {
        return driverSpec;
    }

    public void setDriverSpec(VirtualBoxDriverSpec driverSpec) {
        this.driverSpec = driverSpec;
        initComponents();
    }

    @Override
    protected void initComponents() {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);
        grid.setPadding(new Insets(8));
        grid.getColumnConstraints().addAll(new ColumnConstraints(180, 200, 250, Priority.ALWAYS, HPos.LEFT, true),
                new ColumnConstraints(200, 350, 400, Priority.SOMETIMES, HPos.LEFT, true));

        vboxVersion = new Label(driverSpec.getVboxVersion());
        addPropertyControl(grid, 0, "VirtualBox version :", vboxVersion);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 1, 2, 1);

        simulationsBasePath = new TextField(driverSpec.getSimulationsBasePath());
        simulationsBasePath.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 2, "Simulations path :", simulationsBasePath);

        imagesBasePath = new TextField(driverSpec.getImagesBasePath());
        imagesBasePath.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 3, "Base images path :", imagesBasePath);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 4, 2, 1);

        
        ObservableList<String> interfaces = FXCollections.observableArrayList(getNetworkInterfaceNames());
        bridgedInterface = new ComboBox<>(interfaces);
        bridgedInterface.setValue(interfaces.get(0));
        bridgedInterface.selectionModelProperty().addListener((observable, oldValue, newValue) -> {
            save.setDisable(false);
        });
        addPropertyControl(grid, 5, "Bridged interface :", bridgedInterface);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 6, 2, 1);
        
        webServerHost = new TextField(driverSpec.getWebServerHost());
        webServerHost.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 7, "VBox webserver host :", webServerHost);

        webServerPort = new TextField(Integer.toString(driverSpec.getWebServerPort()));
        webServerPort.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 8, "VBox webserver port :", webServerPort);
        
        webServerUser = new TextField(driverSpec.getWebServerUser());
        webServerUser.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 9, "VBox webserver user :", webServerUser);

        webServerPassword = new TextField(driverSpec.getWebServerPassword());
        webServerPassword.textProperty().addListener(defaultStringPropertyListener());
        addPropertyControl(grid, 10, "VBox webserver password :", webServerPassword);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, 11, 2, 1);
        
        Parent buttons = createButtons();
        save.setDisable(true);

        VBox panel = new VBox(8, grid, buttons);
        this.getChildren().clear();
        this.getChildren().add(panel);
    }

    @Override
    public void updateModelValues() {
        driverSpec.setImagesBasePath(imagesBasePath.getText());
        driverSpec.setSimulationsBasePath(simulationsBasePath.getText());
        
        driverSpec.setBridgedInterface(bridgedInterface.getValue());
        
        driverSpec.setWebServerHost(webServerHost.getText());
        driverSpec.setWebServerPort(Integer.parseInt(webServerPort.getText()));
        driverSpec.setWebServerUser(webServerUser.getText());
        driverSpec.setWebServerPassword(webServerPassword.getText());
    }

    private List<String> getNetworkInterfaceNames() {
        List<String> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface netIf : Collections.list(nets)) {
                if (!netIf.isLoopback()) {
                    result.add(netIf.getName());
                }
            }
        } catch (SocketException ex) {
            result.add("none");
        }

        return result;
    }
}
