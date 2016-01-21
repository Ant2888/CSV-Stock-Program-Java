package nodes;

import static handlers.GlobalVars.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import data.CSVData;

public class DataViewNode extends AnchorPane{

	private CSVData data;
	private Label title, open, close, high, low,
		volume, adjClose;
	private TextField openTF, closeTF,highTF,
		lowTF, volumeTF, adjCloseTF;
	private Button viewGraphics;
	private MainContainer parent;

	public DataViewNode(MainContainer parent, CSVData data) {
		super();
		this.parent = parent;
		this.data = data;
		createObjs();
		createView();
		fillTitle();
		setHandlers();
	}

	private void setHandlers(){
		viewGraphics.setOnAction(e->{
			parent.notifyObserver(Global_Enums.CREATE_GRAPH);
		});
	}

	private void fillTitle(){
		title.setFont(new Font("Arial", 25));
		if(data != null){
			title.setText(data.toString());
		}else{
			title.setText("No Stock Selected");
		}
	}

	private void createView(){
		setMaxSize(WIDTH, HEIGHT);
		setMinSize(400, HEIGHT);
		HBox box = new HBox(20);
		setTopAnchor(box, 8.0);
		setLeftAnchor(box, 5.0);
		if(data == null){
			box.getChildren().addAll(title);
			getChildren().add(box);
			if(data != null){
				viewGraphics.setStyle("-fx-alignment:center center");
				box.getChildren().add(viewGraphics);
			}
		}
		else{
			viewGraphics.setStyle("-fx-alignment:center center");
			box.getChildren().addAll(title,viewGraphics);
			HBox hb = new HBox(20);	
			VBox vb = new VBox(50);
			vb.setId("-fx-alignment: center center");
			VBox vb2 = new VBox(41);
			vb2.setId("-fx-alignment: center center");
			setTopAnchor(hb, 55.0);
			setLeftAnchor(hb, 25.0);

			vb.getChildren().addAll(open,close,high,low,volume,adjClose);
			vb2.getChildren().addAll(openTF, closeTF,highTF,
					lowTF, volumeTF, adjCloseTF);
			hb.getChildren().addAll(vb,vb2);
			getChildren().addAll(box,hb);
			populateData(openTF, closeTF,highTF,lowTF, volumeTF, adjCloseTF);
		}
	}

	private void populateData(TextField... tfs){
		for(int i=0; i<tfs.length;i++){
			tfs[i].setText(data.getDataAtIndex()[i+1]);
		}
	}

	private void createObjs(){
		title = new Label();
		open = new Label("Open");
		close = new Label("Close");
		high = new Label("High");
		low = new Label("Low");
		volume = new Label("Vol.");
		adjClose = new Label("Adj. Close");
		openTF = new TextField();
		openTF.setEditable(false);
		closeTF = new TextField();
		closeTF.setEditable(false);
		highTF = new TextField();
		highTF.setEditable(false);
		lowTF = new TextField();
		lowTF.setEditable(false);
		volumeTF = new TextField();
		volumeTF.setEditable(false);
		adjCloseTF = new TextField();
		adjCloseTF.setEditable(false);
		viewGraphics = new Button("View Current Year");
	}

	public CSVData getCurrentData(){
		return data;
	}
}
