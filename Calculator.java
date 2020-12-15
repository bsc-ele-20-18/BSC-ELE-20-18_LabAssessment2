import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.HashMap;
import java.util.Map;


public class Calculator extends Application {
  
	private static final String[][] template = {
	{ "CE", "c", "Del", "/" },
	{ "7", "8", "9", "*" },
	{ "4", "5", "6", "-" },
	{ "1", "2", "3", "+" },
	{ "", "0", ".", "=" }
};


private final Map<String, Button> accelerators = new HashMap<>();
private DoubleProperty stackValue = new SimpleDoubleProperty();
private DoubleProperty value = new SimpleDoubleProperty();

private enum Op { C, ADD, SUBTRACT, MULTIPLY, DIVIDE }

private Op currentOp = Op.C;

private Op stackOp = Op.C;


public static void main(String[] args) { launch(args); }

@Override 

public void start(Stage primaryStage) {
final TextField screen = screen();
final TilePane buttons = buttons();
primaryStage.setTitle(" LUMBAN'S Calculator");
primaryStage.initStyle(StageStyle.UTILITY);

primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createLayout(screen, buttons)));
primaryStage.show();
}


private VBox createLayout(TextField screen, TilePane buttons) {

final VBox layout = new VBox(35); 

layout.setAlignment(Pos.CENTER);
layout.setStyle(" -fx-padding: 20; -fx-font-size: 25;");
layout.getChildren().setAll(screen, buttons);
handleAccelerators(layout);
screen.prefWidthProperty().bind(buttons.widthProperty());
return layout;
}

private void handleAccelerators(VBox layout) {
layout.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

@Override

public void handle(KeyEvent keyEvent) {
Button activated = accelerators.get(keyEvent.getText());
if (activated != null) {
activated.fire();
}
}
});
}
private TextField screen() {
final TextField screen = new TextField();
screen.setStyle("-fx-background-color: WHITE;");
screen.setEditable(false);
screen.textProperty().bind(Bindings.format("%.0f", value));
return screen;
}

private TilePane buttons() {
TilePane buttons = new TilePane();
buttons.setVgap(10);
buttons.setHgap(10);
buttons.setPrefColumns(template[0].length);
for (String[] r: template) {
for (String string: r) {

buttons.getChildren().add(createButton(string)); 
}
}
return buttons;
}
private Button createButton(final String string) {
Button button = makeStandardButton(string);
if (string.matches("[0-4]")) { 
makeNumericButton(string, button);
} else {
final ObjectProperty<Op> startOp = determineOperand(string);
if (startOp.get() != Op.C) {
makeOperandButton(button, startOp);
} else if ("c".equals(string)) { 
makeClearButton(button);
} else if ("=".equals(string)) { 
makeEqualsButton(button);
}
}
return button;
}
private ObjectProperty<Op> determineOperand(String string) {
final ObjectProperty<Op> startOp = new SimpleObjectProperty<>(Op.C);
switch (string) {
case "+": startOp.set(Op.ADD); break;
case "-": startOp.set(Op.SUBTRACT); break;
case "*": startOp.set(Op.MULTIPLY); break;
case "/": startOp.set(Op.DIVIDE); break;
}
return startOp;
}
private void makeOperandButton(Button button, final ObjectProperty<Op> startOp) {
button.setStyle("-fx-base: grey;");
button.setOnAction(new EventHandler<ActionEvent>() {

@Override

public void handle(ActionEvent e) {
currentOp = startOp.get();
}
});
}
private Button makeStandardButton(String string) {
Button button = new Button(string);
button.setStyle("-fx-base: blue;");
accelerators.put(string, button);
button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
return button;
}
private void makeNumericButton(final String string, Button button) {
button.setOnAction(new EventHandler<ActionEvent>() {
@Override
public void handle(ActionEvent e) {
if (currentOp == Op.C) {
value.set(value.get() * 10 + Integer.parseInt(string));
} else {
stackValue.set(value.get());
value.set(Integer.parseInt(string));
stackOp = currentOp;
currentOp = Op.C;
}
}
});
}

private void makeClearButton(Button button) {
button.setOnAction(new EventHandler<ActionEvent>() { 

@Override
public void handle(ActionEvent e) {
value.set(0);
}
});
}
private void makeEqualsButton(Button button) {
button.setOnAction(new EventHandler<ActionEvent>() {

@Override
public void handle(ActionEvent e) {
switch (stackOp) {
case ADD: value.set(stackValue.get() + value.get()); break;
case SUBTRACT: value.set(stackValue.get() - value.get()); break;
case MULTIPLY: value.set(stackValue.get() * value.get()); break;
case DIVIDE: value.set(stackValue.get() / value.get()); break;
}
}
});
}
}