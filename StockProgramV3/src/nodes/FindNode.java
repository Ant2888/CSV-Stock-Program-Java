package nodes;

import handlers.GlobalVars;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FindNode {

	private Stage stage;
	private Scene scene;
	private AnchorPane mainPane;
	private TreeNode currentList;
	private MainContainer parent;

	private TreeItem<String> stockTree;
	private TreeItem<String> yearTree;

	private ComboBox<String> stockBox;
	private ComboBox<String> yearBox;
	private ComboBox<String> monthBox;
	private ComboBox<String> dayBox;
	private Button find;

	public FindNode(MainContainer parent) {
		if(parent != null){
			this.parent = parent;
			currentList = parent.tree;
			if(currentList != null){
				init();
				fill();
				show();
			}
		}
	}

	//layout is practically the same as the interval
	//see that for more details
	private void fill(){
		//using my setAnchor method here gives an error for whatever reason
		//maybe since there is no defined anchor that they are added to??
		Label header = new Label("Select A Stock");
		find = new Button("Find");

		ObservableList<String> stockList = FXCollections.observableArrayList();
		for(TreeItem<String> stocks: currentList.getRoot().getChildren()){
			stockList.add(stocks.getValue());
		}

		stockBox = new ComboBox<>(stockList);
		stockBox.setPrefSize(121, 21);
		AnchorPane.setLeftAnchor(header, 113.0);
		AnchorPane.setTopAnchor(header, 14.0);
		AnchorPane.setLeftAnchor(find, 129.0);
		AnchorPane.setTopAnchor(find, 80.0);
		AnchorPane.setLeftAnchor(stockBox, 90.0);
		AnchorPane.setTopAnchor(stockBox, 43.0);
		mainPane.getChildren().addAll(header,find,stockBox);

		stockBox.setOnAction(e->{
			if(stockBox.getSelectionModel().getSelectedItem() != null
					&& stockBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillYearPane(stockBox.getSelectionModel().getSelectedItem());
			}
		});

		find.setOnAction(e->{
			parent.notifyObserver(this);
			stage.close();
		});
	}

	private void fillYearPane(String stock){
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(300, 200);
		Label yearLabel = new Label("Year");

		ObservableList<String> yearList = FXCollections.observableArrayList();
		for(TreeItem<String> match: currentList.getRoot().getChildren()){
			if(match.getValue().compareTo(stock)==0){
				for(TreeItem<String> years: match.getChildren()){
					yearList.add(years.getValue());
				}
				stockTree = match;
				break;
			}
		}

		yearBox = new ComboBox<>(yearList);
		yearBox.setPrefSize(121, 21);

		setAnchor(pane, 0, 107);
		setAnchor(yearLabel, 65, 19);
		setAnchor(yearBox, 90, 16);

		pane.getChildren().addAll(yearLabel, yearBox);
		mainPane.getChildren().add(pane);

		yearBox.setOnAction(e->{
			if(yearBox.getSelectionModel().getSelectedItem() != null
					&& yearBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillMonthPane(pane, yearBox.getSelectionModel().getSelectedItem());
			}
		});
	}

	private void fillMonthPane(AnchorPane parent, String selectedItem) {
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(300, 150);
		Label monthLabel = new Label("Month");

		ObservableList<String> monthList = FXCollections.observableArrayList();
		for(TreeItem<String> match: stockTree.getChildren()){
			if(match.getValue().compareTo(selectedItem)==0){
				for(TreeItem<String> months: match.getChildren()){
					monthList.add(months.getValue());
				}
				yearTree = match;
				break;
			}
		}

		monthBox = new ComboBox<>(monthList);
		monthBox.setPrefSize(121, 21);

		setAnchor(pane, 0, 43);
		setAnchor(monthLabel, 53, 16);
		setAnchor(monthBox, 90, 14);

		pane.getChildren().addAll(monthLabel, monthBox);
		parent.getChildren().add(pane);

		monthBox.setOnAction(e->{
			if(monthBox.getSelectionModel().getSelectedItem() != null
					&& monthBox.getSelectionModel().getSelectedItem().compareTo("") != 0){
				fillDayPane(pane, monthBox.getSelectionModel().getSelectedItem());
			}
		});		
	}

	private void fillDayPane(AnchorPane parent, String selectedItem) {
		AnchorPane pane = new AnchorPane();
		pane.setPrefSize(300, 102);
		Label dayLabel = new Label("Day");

		ObservableList<String> dayList = FXCollections.observableArrayList();
		for(TreeItem<String> match: yearTree.getChildren()){
			if(match.getValue().compareTo(selectedItem)==0){
				for(TreeItem<String> days: match.getChildren()){
					dayList.add(days.getValue().split(" ")[2]);
				}
				break;
			}
		}

		dayBox = new ComboBox<>(dayList);
		dayBox.setPrefSize(121, 21);

		setAnchor(pane, 0, 48);
		setAnchor(dayLabel, 68, 11);
		setAnchor(dayBox, 89, 8);

		pane.getChildren().addAll(dayLabel, dayBox);
		parent.getChildren().add(pane);
	}

	private void show(){
		stage.show();
	}

	private void init(){
		stage = new Stage();
		stage.setTitle("Find A Day");
		stage.setResizable(false);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.getIcons().add(GlobalVars.PRGMLOGO);

		mainPane = new AnchorPane();
		scene = new Scene(mainPane,300,300);
		stage.setScene(scene);
	}

	private void setAnchor(Node toAnchor, double x, double y){
		AnchorPane.setLeftAnchor(toAnchor, x);
		AnchorPane.setTopAnchor(toAnchor, y);
	}

	public String getSelectedObj(){
		return stockBox.getValue();
	}

	public String getDate(){
		String toReturn = "";
		if(yearBox != null){
			if(yearBox.getSelectionModel().getSelectedItem() != null){
				toReturn += yearBox.getValue();
			}	
		}
		if(monthBox != null){
			if(monthBox.getSelectionModel().getSelectedItem() != null){
				toReturn += " "+monthBox.getValue();
			}
		}
		if(dayBox != null){
			if(dayBox.getSelectionModel().getSelectedItem() != null){
				toReturn += " "+dayBox.getValue();
			}
		}
		return toReturn;
	}
}
