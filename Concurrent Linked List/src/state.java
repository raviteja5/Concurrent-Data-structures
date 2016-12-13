
public enum state {

	clean(1),iflag(2),dflag(3),mark(4);
	int value;
	state(int value)
	{
		this.value=value;
	}
	int getVal()
	{
		return value;
	}
	
}
