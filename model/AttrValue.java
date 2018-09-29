package model;

import java.util.ArrayList;
import java.util.List;

public class AttrValue {

	private List<IAttrValueElement> elements;
	
	public AttrValue() {
		elements = new ArrayList<>();
	}
	
	public void addElement(IAttrValueElement element) {
		this.elements.add(element);
	}
	
	public List<IAttrValueElement> getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(IAttrValueElement e:elements) {
			sb.append(e.toString());
		}
		return sb.toString();
	}

}
