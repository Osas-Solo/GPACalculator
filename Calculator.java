import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class Calculator {

    //  main container
    static Stage window;
    Scene calculatorScene;
    ScrollPane scroller;
    static BorderPane windowContent;

    //  centre content
    VBox centreContent;
    ImageView logo;
    GridPane gradeCollector;
    TilePane[] gradesLayout;
    Label[] courseCodeLabels;
    TextField[] courseCodes;
    Label[] creditUnitLabels;
    TextField[] creditUnits;
    Label[] gradeLabels;
    ChoiceBox<String> grades[];
    Button calculateButton;

    Alert errorAlert;
    static String[] courseCodeTexts;
    String[] creditUnitTexts;
    static int[] cu;
    static String[] g;
    int[] ratings;
    int[] cp;
    static double tnu;
    static double tcp;
    static double gpa;

    Calculator() {
        //  main container
        window = new Stage();
        windowContent = new BorderPane();
        scroller = new ScrollPane(windowContent);
        scroller.setFitToWidth(true);
        scroller.setFitToHeight(true);

        //  top content
        windowContent.setTop(Home.bar);

        //  centre content
        centreContent = new VBox(10);
        centreContent.setPadding(new Insets(10, 10, 10,10));
        centreContent.setAlignment(Pos.CENTER);
        logo = new ImageView("FUPRE_LOGO.png");

        //  initialise gradeCollector
        gradeCollector = new GridPane();
        gradeCollector.setPadding(new Insets(10, 10, 10, 10));
        gradeCollector.setHgap(10);
        gradeCollector.setVgap(10);
        gradesLayout = new TilePane[Home.courseNumber];
        courseCodeLabels = new Label[Home.courseNumber];
        courseCodes = new TextField[Home.courseNumber];
        creditUnitLabels = new Label[Home.courseNumber];
        creditUnits = new TextField[Home.courseNumber];
        gradeLabels = new Label[Home.courseNumber];
        grades = new ChoiceBox[Home.courseNumber];

        for (int i = 0; i < Home.courseNumber; i++) {  //  initialise each gradeCollector array
            gradesLayout[i] = new TilePane();
            gradesLayout[i].setAlignment(Pos.CENTER);
            gradesLayout[i].setBorder(new Border(new BorderStroke(Color.web("#000033"),
                                                BorderStrokeStyle.SOLID,
                                                CornerRadii.EMPTY,
                                                new BorderWidths(3))));
            gradesLayout[i].setPadding(new Insets(10));
            gradesLayout[i].setOrientation(Orientation.HORIZONTAL);
            gradesLayout[i].setPrefColumns(2);
            courseCodeLabels[i] = new Label("Enter course code:");
            courseCodes[i] = new TextField();
            courseCodes[i].setPromptText("Course code");
            creditUnitLabels[i] = new Label("Enter credit unit:");
            creditUnits[i] = new TextField();
            creditUnits[i].setPromptText("Credit unit");
            gradeLabels[i] = new Label("Select your grade for this course:");
            String[] g = {"A", "B", "C", "D", "F"};
            grades[i] = new ChoiceBox<String>();
            grades[i].getItems().addAll(g);
            grades[i].setValue("A");
            gradesLayout[i].getChildren().addAll(
                    courseCodeLabels[i], courseCodes[i],
                    creditUnitLabels[i], creditUnits[i],
                    gradeLabels[i], grades[i] );
        }

        int c = 0;

        if (Home.courseNumber % 5 == 0) {  //  GridPane constraints for 5-multiple number of courses
            for (int row = 0; row < (Home.courseNumber / 5); row++) {
                for (int col = 0; col < 5; col++) {
                    GridPane.setConstraints(gradesLayout[c], col, row);
                    c++;
                }
            }
        }

        else if (Home.courseNumber % 4 == 0) {  //  GridPane constraints for 4-multiple number of courses
            for (int row = 0; row < (Home.courseNumber / 4); row++) {
                for (int col = 0; col < 4; col++) {
                    GridPane.setConstraints(gradesLayout[c], col, row);
                    c++;
                }
            }
        }

        else if (Home.courseNumber % 3 == 0) {  //  GridPane constraints for 3-multiple number of courses
            for (int row = 0; row < (Home.courseNumber / 3); row++) {
                for (int col = 0; col < 3; col++) {
                    GridPane.setConstraints(gradesLayout[c], col, row);
                    c++;
                }
            }
        }


        else if (Home.courseNumber % 2 == 0) {  //  GridPane constraints for even-number of courses
            for (int row = 0; row < (Home.courseNumber / 2); row++) {
                for (int col = 0; col < 2; col++) {
                    GridPane.setConstraints(gradesLayout[c], col, row);
                    c++;
                }
            }
        }

        else {  //  GridPane constraints for any other number of courses
            for (int row = 0; row <= (Home.courseNumber / 3); row++) {
                for (int col = 0; col < 3; col++) {
                    GridPane.setConstraints(gradesLayout[c], col, row);
                    c++;
                    if (c == Home.courseNumber) {  // check if every gradesLayout has been positioned
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < Home.courseNumber; i++) {
            gradeCollector.getChildren().add(gradesLayout[i]);
        }

        calculateButton = new Button("Calculate");
        centreContent.getChildren().addAll(logo, gradeCollector, calculateButton);
        windowContent.setCenter(centreContent);

        //  calculatorScene
        calculatorScene = new Scene(scroller);
        calculatorScene.getStylesheets().add("LightStyle.css");

        //  create window
        window.setTitle("GPA Result - CALCULATOR");
        window.setScene(calculatorScene);
        window.setMaximized(true);
        window.show();

        //  Error dialog
        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Credit Unit Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText("Please enter valid integer(s) (integers > 0) in the credit unit text field(s)");

        Home.previousWindow = window;

        //  Action events

        Home.homeButton.setOnAction(e -> {
            Home.goHome(window);
        });

        Home.helpButton.setOnAction(e -> {
            Home.goHelp(window);
        });

        Home.aboutButton.setOnAction(e -> {
            Home.goAbout(window);
        });

        calculateButton.setOnAction(e -> {
            try {
                calculateGpa();
                new Result();
                window.hide();
            } catch (Exception e1) {
                errorAlert.show();
            }
        });

    }  // end of constructor

    public void calculateGpa() throws Exception {
        //  initialise arrays
        courseCodeTexts = new String[Home.courseNumber];
        creditUnitTexts = new String[Home.courseNumber];
        cu = new int[Home.courseNumber];
        g = new String[Home.courseNumber];
        ratings = new int[Home.courseNumber];
        cp = new int[Home.courseNumber];
        tnu = 0;
        tcp = 0;

        for (int i = 0; i < Home.courseNumber; i++) {  //  get entered inputs
            courseCodeTexts[i] = courseCodes[i].getText();
            creditUnitTexts[i] = creditUnits[i].getText();
            cu[i] = Integer.parseInt(creditUnitTexts[i]);
            if (cu[i] < 0) {
                throw new Exception();
            }
            g[i] = grades[i].getValue();

            //  convert grade to ratings
            if (g[i].equals("A")) {
                ratings[i] = 5;
            }

            if (g[i].equals("B")) {
                ratings[i] = 4;
            }

            if (g[i].equals("C")) {
                ratings[i] = 3;
            }

            if (g[i].equals("D")) {
                ratings[i] = 2;
            }

            if (g[i].equals("F")) {
                ratings[i] = 0;
            }

            cp[i] = cu[i] * ratings[i];

            tnu += cu[i];
            tcp += cp[i];
        }

        gpa = tcp / tnu;
        gpa *= 100;
        gpa = Math.round(gpa);
        gpa /= 100;

    }  //  end of calculateGPA

}  //  end of class
