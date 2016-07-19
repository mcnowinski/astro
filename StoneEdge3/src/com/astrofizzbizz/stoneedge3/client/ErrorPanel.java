package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ErrorPanel extends VerticalPanel
{
	private TextArea textArea = new TextArea();
	public TextArea getTextArea() {return textArea;}
	public ErrorPanel()
	{
		super();
		textArea.setSize("500px", "500px");
		add(textArea);
	}
	public void writeStackTrace(Throwable caught)
	{
		Object[] stackTrace = caught.getStackTrace();
		StringBuilder output = new StringBuilder();
		output.append(caught.getMessage() + "\n");
		if (stackTrace != null) 
		{
			for (Object line : stackTrace) 
			{
				output.append(line);
		        output.append("\nFinished Strack Trace");
			}
			textArea.setText(output.toString());
		} 
		else 
		{
			textArea.setText("[stack unavailable]");
		}		
	}
	public void writeStringBuffer(String[] buffer)
	{
		String text = "";
		for (int ii = 0; ii < buffer.length; ++ii)
		{
			text = text + buffer[ii] + "\n";
		}
		textArea.setText(text);
	}

}
