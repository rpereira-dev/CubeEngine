package com.grillecube.client.renderer.model;

import java.util.ArrayList;

public class Model
{
	/** model name */
	private String	_name;
	
	/** model parts */
	private ArrayList<ModelPart> _parts;

	public Model(String name)
	{
		this._name = name;
	}
	
	/** return model parts */
	public ArrayList<ModelPart>	getParts()
	{
		return (this._parts);
	}
	
	/** return number of model parts */
	public int	getPartsCount()
	{
		return (this._parts.size());
	}
	
	/** set model name */
	public void	setName(String str)
	{
		this._name = str;
	}
	
	/** set model name */
	public void	setParts(ArrayList<ModelPart> parts)
	{
		this._parts = parts;
	}

	public ModelPart getPartAt(int i)
	{
		if (i < 0 || i >= this._parts.size())
		{
			return (null);
		}
		return (this._parts.get(i));
	}

	public String	getName()
	{
		return (this._name);
	}
}
