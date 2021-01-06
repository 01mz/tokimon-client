package ca.cmpt213.a5client.ui;

import ca.cmpt213.a5client.model.ClientModel;
import ca.cmpt213.a5client.model.Tokimon;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.function.UnaryOperator;

/**
 * The TokimonClientUI class is responsible for managing the JavaFx UI
 *
 */
public class TokimonClientUI extends Application {
    private static final int NUM_ROWS = 4;
    private static final int NUM_COLS = 4;
    private ClientModel model = new ClientModel();
    private List<Tokimon> tokimonList = model.getTokimonList();

    // UI elements
    private final GridPane gridpane = new GridPane();
    private final Label labelStatus = new Label();
    private final Button buttonAddTokimon = new Button("Add Tokimon");
    private final int CELL_SIZE = 150;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        labelStatus.getStyleClass().add("label-status");

        // Set up size, rows, cols of grid
        setupGridPane();

        // Populate grid with clickable panes
        populateGrid();

        setupAddTokimonButton();
        ScrollPane scrollPane = new ScrollPane(gridpane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        VBox vBox = new VBox(scrollPane, labelStatus, buttonAddTokimon);
        vBox.setSpacing(20);
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().add("vbox");


        primaryStage.setTitle("Tokimon");
        Scene scene = new Scene(vBox, 800, 600);
        scene.getStylesheets().add("res/css/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setupGridPane() {
        gridpane.getColumnConstraints().clear();
        gridpane.getRowConstraints().clear();

        gridpane.setMaxWidth(CELL_SIZE * NUM_COLS);
        gridpane.setAlignment(Pos.CENTER);
        gridpane.getStyleClass().add("grid");

        // Set rows and columns with size CELL_SIZE
        // Help from: https://stackoverflow.com/questions/32892646/adding-borders-to-gridpane-javafx
        // and https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/GridPane.html
        for(int i = 0; i < NUM_ROWS; i++) {
            RowConstraints row = new RowConstraints(CELL_SIZE);
            gridpane.getRowConstraints().add(row);
        }
        for(int i = 0; i < NUM_COLS; i++) {
            ColumnConstraints column = new ColumnConstraints(CELL_SIZE);
            gridpane.getColumnConstraints().add(column);
        }
    }

    private void populateGrid() {
        int row = 0;
        int col = 0;

        for (Tokimon tokimon : tokimonList) {

            VBox pane = new VBox();
            pane.getStyleClass().add("grid-cell");

            String color = tokimon.getColor();
            if(color.matches("0x([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})")){
                pane.setStyle("-fx-background-color: #" + tokimon.getColor().substring(2));
            }

            Text text = new Text(tokimon.getName());
            ImageView imageView = getTokiImageView(tokimon);
            pane.getChildren().addAll(imageView, text);
            pane.setAlignment(Pos.CENTER);
            pane.setSpacing(20);


            pane.setOnMouseClicked(mouseEvent -> {
                final Stage addTokimonStage = new Stage();
                addTokimonStage.initModality(Modality.APPLICATION_MODAL);
                addTokimonStage.initOwner(primaryStage);

                Text sceneTitle = new Text("Edit Tokimon");

                Label nameLabel = new Label("Name");
                TextField nameField = new TextField(tokimon.getName());

                Label weightLabel = new Label("Weight");
                TextField weightField = new TextField(tokimon.getWeight().toString());
                restrictToNumericInput(weightField);

                Label heightLabel = new Label("Height");
                TextField heightField = new TextField(tokimon.getHeight().toString());
                restrictToNumericInput(heightField);

                Label abilityLabel = new Label("Ability");
                TextField abilityField = new TextField(tokimon.getAbility());

                Label strengthLabel = new Label("Strength");
                TextField strengthField = new TextField(tokimon.getStrength().toString());
                restrictToNumericInput(strengthField);

                Label colorLabel = new Label("Color");
                ColorPicker colorPicker = new ColorPicker(Color.valueOf(tokimon.getColor()));
                colorPicker.setValue(Color.CORAL);

                colorPicker.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent actionEvent) {
                        System.out.println(colorPicker.getValue());
                    }
                });

                Button addButton = new Button("Make Changes");
                addButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        Tokimon newTokimon = new Tokimon(nameField.getText(),
                                Double.parseDouble(weightField.getText()),
                                Double.parseDouble(heightField.getText()),
                                abilityField.getText(),
                                Integer.parseInt(strengthField.getText()),
                                colorPicker.getValue().toString()
                        );
                        newTokimon.setId(tokimon.getId());

                        model.alterTokimon(newTokimon);
                        refreshGrid();
                        addTokimonStage.close();

                    }
                });

                Button deleteButton = new Button("Delete Tokimon");
                deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {

                        model.deleteTokimon(tokimon.getId());
                        refreshGrid();
                        addTokimonStage.close();

                    }
                });

                VBox dialogVbox = new VBox(20);
                dialogVbox.getChildren().addAll(sceneTitle,
                        nameLabel, nameField,
                        weightLabel, weightField,
                        heightLabel, heightField,
                        abilityLabel, abilityField,
                        strengthLabel, strengthField,
                        colorLabel, colorPicker,
                        addButton, deleteButton);
                Scene dialogScene = new Scene(dialogVbox, 600, 600);
                addTokimonStage.setScene(dialogScene);
                addTokimonStage.show();


            });




            gridpane.add(pane, col, row);

            col++;
            if(col >= NUM_COLS) {
                col = 0;
                row++;
            }
        }
    }


    // Help from: https://stackoverflow.com/questions/22166610/how-to-create-a-popup-windows-in-javafx
    private void setupAddTokimonButton() {
        buttonAddTokimon.setOnAction(actionEvent -> {

            final Stage addTokimonStage = new Stage();
            addTokimonStage.initModality(Modality.APPLICATION_MODAL);
            addTokimonStage.initOwner(primaryStage);

            Text sceneTitle = new Text("Add a new tokimon");

            Label nameLabel = new Label("Name");
            TextField nameField = new TextField();

            Label weightLabel = new Label("Weight");
            TextField weightField = new TextField();
            restrictToNumericInput(weightField);

            Label heightLabel = new Label("Height");
            TextField heightField = new TextField();
            restrictToNumericInput(heightField);

            Label abilityLabel = new Label("Ability");
            TextField abilityField = new TextField();

            Label strengthLabel = new Label("Strength");
            TextField strengthField = new TextField();
            restrictToNumericInput(strengthField);

            Label colorLabel = new Label("Color");
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(Color.CORAL);
            Text text = new Text("Choose a tokimon color.");

            colorPicker.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent actionEvent) {
                    System.out.println(colorPicker.getValue());
                }
            });

            Button addButton = new Button("Add");
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    Tokimon tokimon = new Tokimon(nameField.getText(),
                            Double.parseDouble(weightField.getText()),
                            Double.parseDouble(heightField.getText()),
                            abilityField.getText(),
                            Integer.parseInt(strengthField.getText()),
                            colorPicker.getValue().toString()
                            );

                    model.addTokimon(tokimon);
                    addTokimonStage.close();

                    refreshGrid();
                }
            });

            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().addAll(sceneTitle,
                    nameLabel, nameField,
                    weightLabel, weightField,
                    heightLabel, heightField,
                    abilityLabel, abilityField,
                    strengthLabel, strengthField,
                    colorLabel, colorPicker,
                    addButton);
            Scene dialogScene = new Scene(dialogVbox, 600, 600);
            addTokimonStage.setScene(dialogScene);
            addTokimonStage.show();
        });
        buttonAddTokimon.getStyleClass().add("button");
    }

    private void refreshGrid() {
        tokimonList = model.getTokimonList();
        gridpane.getChildren().clear();
        setupGridPane();
        populateGrid();
    }

    // Source: https://stackoverflow.com/a/40472822/8930125
    private void restrictToNumericInput(TextField textField) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)?|0")) {
                return change;
            }
            return null;
        };

        textField.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
    }


    private ImageView getTokiImageView(Tokimon tokimon) {
        final Image IMAGE_CIRCLE = new Image("res/images/empty-circle.png");
        ImageView imageView = new ImageView(IMAGE_CIRCLE);

        double height = tokimon.getHeight();
        double width = tokimon.getWeight();
        if(height <= 5) {
            height = 5;
        } else if (height > 100) {
            height = 100;
        }
        if(width <= 5) {
            width = 5;
        } else if(width > 100) {
            width = 100;
        }
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);


        return imageView;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
