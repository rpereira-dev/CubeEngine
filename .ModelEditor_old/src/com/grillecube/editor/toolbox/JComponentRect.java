package com.grillecube.editor.toolbox;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class JComponentRect extends Component
{
	private static final long serialVersionUID = 1L;
	private Color _color;

	public JComponentRect()
	{
		this._color = new Color(0xFFFFFFFF);
	}
	
	@Override
	public void paint(Graphics g)
	{
		g.setColor(this._color);
		g.fillRect(this.getLocation().x, this.getLocation().y - this.getSize().height,
				this.getSize().width, this.getSize().height);  
	}
	
	public void setColor(Color color)
	{
		this._color = color;
		this.repaint();
	}
}
