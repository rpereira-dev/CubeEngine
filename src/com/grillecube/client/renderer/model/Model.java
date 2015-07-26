package com.grillecube.client.renderer.model;

public class Model
{
	/** model name */
	private String	_name;
	
	/** model parts */
	private ModelPart[]	_parts;

	public Model(String name)
	{
		this._name = name;
	}
	
	/** return model parts */
	public ModelPart[]	getParts()
	{
		return (this._parts);
	}
	
	/** return number of model parts */
	public int	getPartsCount()
	{
		return (this._parts.length);
	}
	
	/** set model name */
	public void	setName(String str)
	{
		this._name = str;
	}
	
	/** set model name */
	public void	setParts(ModelPart[] parts)
	{
		this._parts = parts;
	}

	public ModelPart getPartAt(int i)
	{
		if (i < 0 || i >= this._parts.length)
		{
			return (null);
		}
		return (this._parts[i]);
	}

	public String	getName()
	{
		return (this._name);
	}
}
