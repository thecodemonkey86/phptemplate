package model;

public class HtmlBr extends HtmlTag{

	public static final String TAG_NAME = "br";
	
	public HtmlBr() {
		super(TAG_NAME);
	}
	
	@Override
	public void addAttr(HtmlAttr a) {
		throw new RuntimeException("br tags don't have attributes");
	}

	@Override
	public String toString() {
		return String.format("<%s>", TAG_NAME
			);
	}
}
