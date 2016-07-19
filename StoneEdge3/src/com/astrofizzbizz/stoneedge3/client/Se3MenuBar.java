package com.astrofizzbizz.stoneedge3.client;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class Se3MenuBar extends MenuBar
{
	private String menuText[];
	private String itemText[][];
	private MenuItem[][] menuItem;
    public Se3MenuBar(String menuText[], String itemText[][], StoneEdge3 stoneEdge3)
    {
    	super();
    	this.menuText = menuText;
    	this.itemText = itemText;
    	menuItem = new MenuItem[menuText.length][];
    	for (int ii = 0; ii < menuText.length; ++ ii)
    	{
			boolean verticalMenu = true;
			MenuBar subMenu = new MenuBar(verticalMenu);
    		menuItem[ii] = new MenuItem[itemText[ii].length];
        	for (int ij = 0; ij < itemText[ii].length; ++ ij)
        	{
        		menuItem[ii][ij] = new MenuItem(itemText[ii][ij], new MainMenuActionListeners(stoneEdge3, menuText[ii] + "." + itemText[ii][ij]));
        		subMenu.addItem(menuItem[ii][ij]);
        	}
			addItem(menuText[ii], subMenu);
    	}
    }
    private int getSubMenu(String mtext)
    {
    	for (int ii = 0; ii < menuText.length; ++ ii)
    	{
    		if (menuText[ii].equals(mtext)) return ii;
    	}
    	return -1;
    }
    private MenuItem getMenuItem(String mtext, String itext)
    {
    	int imenu =  getSubMenu(mtext);
    	if ( imenu >= 0)
    	{
        	for (int ij = 0; ij < itemText[imenu].length; ++ ij)
        	{
        		if (itemText[imenu][ij].equals(itext)) return menuItem[imenu][ij];
        	}
    	}
    	return null;
    }
    public void setEnabled(String mtext, String itext, boolean enabled)
    {
    	if (getMenuItem(mtext, itext) != null)
    		getMenuItem(mtext, itext).setEnabled(enabled);
    }
    public void setEnabled(String mtext, boolean enabled)
    {
    	int imenu =  getSubMenu(mtext);
    	if ( imenu >= 0)
    	{
        	for (int ij = 0; ij < itemText[imenu].length; ++ ij)
        	{
        		menuItem[imenu][ij].setEnabled(enabled);
        	}
    	}
    }

}
