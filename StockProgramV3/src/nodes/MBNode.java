package nodes;

import handlers.GlobalVars.Global_Enums;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;


public class MBNode extends MenuBar{

	private Menu fileBtn, editBtn, newBtn, windowBtn; //new is to go into file
	private MenuItem addDataBtn, exitBtn,
					 findStock, graphBtn;
	private SeparatorMenuItem sp1;
	private MainContainer parent;
	private CheckMenuItem listView;
	private CheckMenuItem treeView;
	
	
	public MBNode(MainContainer parent) {
		this.parent = parent;
		createObjs();
		populate();
		setHandlers();
	}
	
	private void setHandlers(){
		exitBtn.setOnAction(e->{
			parent.notifyObserver(Global_Enums.EXIT);
		});
		addDataBtn.setOnAction(e->{
			parent.notifyObserver(Global_Enums.ADD);
		});
		findStock.setOnAction(e->{
			parent.notifyObserver(Global_Enums.FIND);
		});
		graphBtn.setOnAction(e->{
			parent.notifyObserver(Global_Enums.NEW_GRAPH);
		});
		
		listView.setSelected(true);
		//might need to redraw the BP aswell
		listView.setOnAction(e->{
			if(listView.isSelected() && !parent.bp.getChildren().contains(parent.list)){
				parent.bp.setLeft(parent.list);
			}else{
				parent.bp.getChildren().remove(parent.list);
			}
		});

		treeView.setSelected(true);
		treeView.setOnAction(e->{
			if(treeView.isSelected() && !parent.bp.getChildren().contains(parent.tree)){
				parent.bp.setRight(parent.tree);
			}else{
				parent.bp.getChildren().remove(parent.tree);
			}
		});
	}
	
	private void populate(){
		getMenus().addAll(fileBtn,editBtn,windowBtn);
		fileBtn.getItems().addAll(newBtn,sp1,exitBtn);
		newBtn.getItems().addAll(addDataBtn,graphBtn);
		windowBtn.getItems().addAll(listView,treeView);
		editBtn.getItems().addAll(findStock);
	}
	
	private void createObjs(){
		fileBtn = new Menu("File");
		editBtn = new Menu("Edit");
		newBtn = new Menu("New");
		windowBtn = new Menu("Window");
		sp1 = new SeparatorMenuItem();
		addDataBtn = new MenuItem("Stock...");
		graphBtn = new MenuItem("Graph");
		exitBtn = new MenuItem("Exit");
		
		listView = new CheckMenuItem("List View");
		treeView = new CheckMenuItem("Tree View");
		findStock = new MenuItem("Find...");
	}
}
