package boot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import model.Model;
import model.MyModel;
import view.MyView;
import view.View;
import controller.Controller;
import controller.MyController;


public class Run {

	public static void main(String[] args) {

		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		PrintWriter out = new PrintWriter(System.out);
				
		View view = new MyView(in, out);
		Model model = new MyModel();
		
		Controller controller = new MyController(view, model);
		view.setController(controller);
		model.setController(controller);
		
		view.start();

	}

}
