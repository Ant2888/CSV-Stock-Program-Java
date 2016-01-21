package nodes;

import static handlers.GlobalVars.HEIGHT;
import static handlers.GlobalVars.WIDTH;
import handlers.Observer;
import handlers.Subject;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import data.CSVData;
import data.CSVObj;

public class MainContainer extends Scene implements Subject{

	protected BorderPane bp;
	protected MBNode menu;
	protected ListNode list;
	protected Stage stage;
	protected DataViewNode dataView;
	protected TreeNode tree;
	protected ArrayList<Observer> obsAr;
	
	public MainContainer(Stage stage) {
		super(new BorderPane(), WIDTH,HEIGHT);
		bp = (BorderPane)getRoot();
		this.stage = stage;
		init();
	}
	
	private void init(){
		createObjs();
		bp.setTop(menu);
		bp.setLeft(list);
		bp.setCenter(dataView);
		bp.setRight(tree);
		fillStage();//last
	}
	
	private void createObjs(){
		obsAr = new ArrayList<>();
		menu = new MBNode(this);
		list = new ListNode(this);
		dataView = new DataViewNode(this,null);
		tree = new TreeNode(this);
	}
	
	private void fillStage(){
		stage.setOnCloseRequest(e->{
			Platform.exit();
			System.exit(0);
		});
		stage.setTitle("Stock Program");
		stage.setScene(this);
	}

	@Override
	public void addObserver(Observer o) {
		obsAr.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		obsAr.remove(o);
	}

	@Override
	public void notifyObserver(Object args) {
		for(Observer o: obsAr){
			o.updates(args);
		}
	}
	
	public void startView(){
		stage.show();
	}
	
	public void addCSVOBj(CSVObj obj){
		list.addCSVObj(obj);
		tree.addItems(obj);
	}
	
	public void removeCSVObj(CSVObj obj){
		list.removeCSVObj(obj);
		tree.removeItems(obj);
	}
	
	//the optimizations should be/are left to the controller or model (i.e-if data == null etc etc)
	public void placeDataInView(CSVData data){
		bp.getChildren().remove(dataView);
		dataView = new DataViewNode(this,data);
		bp.setCenter(dataView);
	}
	
	public Stage getStage(){
		return stage;
	}
	
	public CSVObj getParentInNode(){
		return dataView.getCurrentData().getParent();
	}
	
	public CSVData getDataInNode(){
		return dataView.getCurrentData();
	}
	
	public String getTreeSelectedData(){
		return tree.getCurrentModel();
	}
	
	public String getTreeSelectedRoot(){
		return tree.getModelStockName();
	}
}
