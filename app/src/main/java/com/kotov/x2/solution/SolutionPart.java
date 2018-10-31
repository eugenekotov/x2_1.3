package com.kotov.x2.solution;

import java.util.ArrayList;
import java.util.List;


public class SolutionPart implements ISolutionPart {
	
	private List<ISolutionLine> items = new ArrayList<ISolutionLine>();
	
	public SolutionPart(ISolutionLine line) {
		items.add(line);
	}
	
	public SolutionPart(ISolutionItem item) {
		items.add((ISolutionLine) item);
	}

	@Override
	public List<ISolutionLine> getSolutionLines(int textSize) {
		return items;
	}

}
