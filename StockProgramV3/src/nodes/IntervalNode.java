package nodes;

import static handlers.GlobalVars.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class IntervalNode {

	private TreeNode treeList;
	private Stage stage;
	private Scene scene;
	private AnchorPane mainPane;
	private Button createGraph;
	private TreeItem<String> stockItem;
	private TreeItem<String> fromYearsItem;
	private TreeItem<String> toYearsItem;
	private MainContainer parent;

	private ComboBox<String> selectedObj,
	fromYears, toYears, fromMonths, toMonths,
	fromDays, toDays;

	public IntervalNode(MainContainer parent) {
		if(parent != null){
			this.treeList = parent.tree;
			this.parent = parent;
			if(treeList != null){
				init();
				fill();
				show();
			}
		}
	}

	public String getFromDate(){
		String fromDate = "";
		if(fromYears != null){
			if(fromYears.getSelectionModel().getSelectedItem() != null){
				fromDate += fromYears.getSelectionModel().getSelectedItem();
			}
		}
		if(fromMonths != null){
			if(fromMonths.getSelectionModel().getSelectedItem() != null){
				fromDate += " "+fromMonths.getSelectionModel().getSelectedItem();
			}
		}
		if(fromDays != null){
			if(fromDays.getSelectionModel().getSelectedItem() != null){
				fromDate += " "+fromDays.getSelectionModel().getSelectedItem();
			}
		}
		//places nulls in there so we just get rid of them
		return fromDate.replaceAll(" null", "");
	}

	public String getToDate(){
		String toDate = "";
		if(toYears != null){
			if(toYears.getSelectionModel().getSelectedItem() != null){
				toDate += toYears.getSelectionModel().getSelectedItem();
			}
		}
		if(toMonths != null){
			if(toMonths.getSelectionModel().getSelectedItem() != null){
				toDate += " "+toMonths.getSelectionModel().getSelectedItem();
			}
		}
		if(toDays != null){
			if(toDays.getSelectionModel().getSelectedItem() != null){
				toDate += " "+toDays.getSelectionModel().getSelectedItem();
			}
		}
		//places nulls in there so we just get rid of them
		return toDate;
	}

	public String getSelectedObj(){
		return selectedObj.getSelectionModel().getSelectedItem();
	}

	private void show(){
		stage.show();
		stage.requestFocus();
	}

	private void fill(){
		stage.getIcons().add(GRAPHLOGO);
		Label header = new Label("Select A Stock To Graph");
		ComboBox<String> objsBox;
		createGraph = new Button("Create Graph");

		//fill the list with the stock names from the treelist
		//since there's no reason to load it again
		//this ideology is followed throughout
		ObservableList<String> boxList = FXCollections.observableArrayList();
		for(TreeItem<String> stockNames: treeList.getRoot().getChildren()){
			boxList.add(stockNames.getValue());
		}

		objsBox = new ComboBox<>(boxList);
		selectedObj = objsBox;
		objsBox.setVisibleRowCount(10);
		objsBox.setPrefSize(140, 21);
		setAnchor(objsBox, 130, 44);
		setAnchor(createGraph, 289, 44);
		setAnchor(header, 130, 14);

		//add the nodes to the anchor
		mainPane.getChildren().addAll(objsBox,createGraph, header);
		objsBox.setOnAction(e->{
			if(objsBox.getSelectionModel().getSelectedItem() != null &&
					objsBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				//if an object is selected we can start showing the other panes
				fillFromTo(objsBox.getSelectionModel().getSelectedItem());
			}
		});
		createGraph.setOnAction(e->{
			parent.notifyObserver(this);
			stage.close();
		});
	}

	private void fillFromTo(String stockName){
		//if an object is selected this will fill an anchor pane
		//and place it into the main pane
		AnchorPane fromTo = new AnchorPane();
		fromTo.setPrefSize(400, 200);
		Label from = new Label("FROM");
		Label to = new Label("TO");
		Label y1 = new Label("Year");
		Label y2 = new Label("Year");
		ComboBox<String> fromBox;
		ComboBox<String> toBox;

		ObservableList<String> boxList = FXCollections.observableArrayList();
		for(TreeItem<String> match :treeList.getRoot().getChildren()){
			if(match.getValue().compareTo(stockName)==0){
				for(TreeItem<String> years: match.getChildren()){
					boxList.add(years.getValue());
				}
				stockItem = match;
				break;
			}
		}

		fromBox = new ComboBox<>(boxList);
		fromBox.setVisibleRowCount(10);
		fromBox.setPrefSize(71, 21);
		fromYears = fromBox;
		toBox = new ComboBox<>(boxList);
		toBox.setVisibleRowCount(10);
		toBox.setPrefSize(71, 21);
		toYears = toBox;

		setAnchor(fromTo, 0, 100);
		setAnchor(from, 84, 14);
		setAnchor(to, 284, 14);
		setAnchor(y1, 231, 37);
		setAnchor(y2, 39, 37);
		setAnchor(fromBox, 65, 35);
		setAnchor(toBox, 257, 34);

		fromTo.getChildren().addAll(from,to,y1,y2,fromBox,toBox);
		mainPane.getChildren().add(fromTo);
		fromBox.setOnAction(e->{
			if(fromBox.getSelectionModel().getSelectedItem() != null &&
					fromBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillFromMonths(fromBox.getSelectionModel().getSelectedItem(), fromTo);
			}
		});
		toBox.setOnAction(e->{
			if(toBox.getSelectionModel().getSelectedItem() != null &&
					toBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillToMonths(toBox.getSelectionModel().getSelectedItem(), fromTo);
			}
		});
	}

	private void fillFromMonths(String year, AnchorPane parent){
		//fills an anchor pane that will be a child of the fromToPane
		//this will be the months
		AnchorPane pane = new AnchorPane();
		ComboBox<String> monthBox;
		Label months = new Label("Month");

		ObservableList<String> monthsList = FXCollections.observableArrayList();
		for(TreeItem<String> match: stockItem.getChildren()){
			if(match.getValue().compareTo(year)==0){
				for(TreeItem<String> monthTree: match.getChildren()){
					monthsList.add(monthTree.getValue());
				}
				fromYearsItem = match;
				break;
			}
		}

		monthBox = new ComboBox<>(monthsList);
		monthBox.setVisibleRowCount(10);
		monthBox.setPrefSize(80, 25);
		fromMonths = monthBox;

		pane.setPrefSize(187, 145);
		setAnchor(pane, 13, 55);
		setAnchor(monthBox, 51, 20);
		setAnchor(months, 15, 23);
		pane.getChildren().addAll(monthBox,months);
		parent.getChildren().add(pane);

		monthBox.setOnAction(e->{
			if(monthBox.getSelectionModel().getSelectedItem() != null &&
					monthBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillFromDays(monthBox.getSelectionModel().getSelectedItem(), pane);
			}
		});
	}

	private void fillToMonths(String years, AnchorPane parent){
		//fills an anchor pane that will be a child of the fromToPane
		//this will be the months
		AnchorPane pane = new AnchorPane();
		ComboBox<String> monthBox;
		Label months = new Label("Month");

		ObservableList<String> monthsList = FXCollections.observableArrayList();
		for(TreeItem<String> match: stockItem.getChildren()){
			if(match.getValue().compareTo(years)==0){
				for(TreeItem<String> monthTree: match.getChildren()){
					monthsList.add(monthTree.getValue());
				}
				toYearsItem = match;
				break;
			}
		}

		monthBox = new ComboBox<>(monthsList);
		monthBox.setVisibleRowCount(10);
		monthBox.setPrefSize(80, 25);
		toMonths = monthBox;

		pane.setPrefSize(186, 145);
		setAnchor(pane, 200, 55);
		setAnchor(monthBox, 58, 20);
		setAnchor(months, 22, 23);
		pane.getChildren().addAll(monthBox,months);
		parent.getChildren().add(pane);

		monthBox.setOnAction(e->{
			if(monthBox.getSelectionModel().getSelectedItem() != null &&
					monthBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillToDays(monthBox.getSelectionModel().getSelectedItem(), pane);
			}
		});
	}

	private void fillFromDays(String month, AnchorPane parent){
		//fills an anchor pane that will be a child of the monthsPane
		//this will be the days
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(187, 90);
		ComboBox<String> daysBox;
		Label days = new Label("Day");

		ObservableList<String> daysList = FXCollections.observableArrayList();
		for(TreeItem<String> match: fromYearsItem.getChildren()){
			if(match.getValue().compareTo(month)==0){
				for(TreeItem<String> dayTree: match.getChildren()){
					daysList.add(dayTree.getValue().split(" ")[2]);
				}
				break;
			}
		}

		daysBox = new ComboBox<>(daysList);
		daysBox.setPrefSize(80, 25);
		fromDays = daysBox;

		setAnchor(daysBox, 51, 20);
		setAnchor(pane, 0, 41);
		setAnchor(days, 30, 23);

		pane.getChildren().addAll(daysBox, days);
		parent.getChildren().add(pane);
	}

	private void fillToDays(String month, AnchorPane parent){
		//fills an anchor pane that will be a child of the monthsPane
		//this will be the days
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(186, 98);
		ComboBox<String> daysBox;
		Label days = new Label("Day");

		ObservableList<String> daysList = FXCollections.observableArrayList();
		for(TreeItem<String> match: toYearsItem.getChildren()){
			if(match.getValue().compareTo(month)==0){
				for(TreeItem<String> dayTree: match.getChildren()){
					daysList.add(dayTree.getValue().split(" ")[2]);
				}
				break;
			}
		}

		daysBox = new ComboBox<>(daysList);
		daysBox.setPrefSize(80, 25);
		toDays = daysBox;

		setAnchor(daysBox, 58, 20);
		setAnchor(pane, 0, 41);
		setAnchor(days, 37, 23);

		pane.getChildren().addAll(daysBox, days);
		parent.getChildren().add(pane);
	}

	private void init(){
		stage = new Stage();
		stage.setTitle("Select An Interval");
		stage.setResizable(false);

		mainPane = new AnchorPane();
		scene = new Scene(mainPane, 400, 300);
		stage.setScene(scene);
	}

	private void setAnchor(Node toAnchor, double x, double y){
		AnchorPane.setLeftAnchor(toAnchor, x);
		AnchorPane.setTopAnchor(toAnchor, y);
	}
}
