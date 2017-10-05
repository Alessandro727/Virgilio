package model;

public class MacroCategory {
	
	private int id;
	private String macro_category_fq;
	private int mrt;	// mean residence time (tempo medio di permanenza)
	private double[] weights;
	
	public MacroCategory() {
		
	}
	
	public MacroCategory(String macro_category_fq, int mrt) {
		this.macro_category_fq=macro_category_fq;
		this.mrt=mrt;
		
	}
	
	public int getId() {
		return id;
	}

	
	public void setId(int id) {
		this.id = id;
		initializeWeights(id);
	}	
	
	
	public void initializeWeights(int id) {
		switch(id) {
											//		1		2		3		4		5		6		7		8		9		10
			case 1: this.setWeights(new double[]{0,	1,		0.4,	0.5,	0.5,	0.1,	0.3,	0.8,	0.3,	0.8,	0});	break;
			case 2: this.setWeights(new double[]{0,	0.4,	1,		0.2,	0.5,	0.1,	0.6,	0.7,	0.1,	0,		0.5});	break;
			case 3: this.setWeights(new double[]{0,	0.5,	0.1,	1,		0.7,	0,		0,		0.6,	0,		0.4,	0});	break;
			case 4: this.setWeights(new double[]{0,	0.6,	0.3,	0.6,	1,		0,		0.1,	0.8,	0.1,	0.8,	0});	break;
			case 5: this.setWeights(new double[]{0,	0.1,	0.1,	0.1,	0.1,	1,		0.5,	0.1,	0.1,	0.1,	0.1});	break;
			case 6: this.setWeights(new double[]{0,	0.2,	0.1,	0.1,	0.1,	0.5,	1,		0.4,	0.1,	0,		0.2});	break;
			case 7: this.setWeights(new double[]{0,	0.6,	0.6,	0.8,	0.8,	0.4,	0.4,	1,		0.3,	0.2,	0.3});	break;
			case 8: this.setWeights(new double[]{0,	0.2,	0.6,	0,		0,		0.3,	0.1,	0.5,	1,		0,		0.2});	break;
			case 9: this.setWeights(new double[]{0,	0.7,	0.2,	0.7,	0.9,	0,		0.1,	0.4,	0,		1,		0.1});	break;
			case 10: this.setWeights(new double[]{0,	0.4,	0.4,	0.2,	0.2,	0.2,	0.2,	0.5,	0.2,	0.2,	1});		break;			
			default: this.setWeights(new double[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0}); break;
		}
	}

		
	
	public String getMacro_category_fq() {
		return macro_category_fq;
	}


	public void setMacro_category_fq(String macro_category_fq) {
		this.macro_category_fq = macro_category_fq;
	}


	public int getMrt() {
		return mrt;
	}

	
	public void setMrt(int mrt) {
		this.mrt = mrt;
	}


	public double[] getWeights() {
		return weights;
	}
	
	
	public double getWeight(int i) {
		if (i<this.weights.length)
			return this.weights[i];
		else return 0;
	}


	public void setWeights(double[] weights) {
		this.weights = weights;
	}

}
