package ko.fxlogviewer2.readers.inter;

import java.util.ArrayList;

public interface LogReader {

	public ArrayList<String[]> getData() throws Exception;

	public ArrayList<String> getHeaderColumns() throws Exception;

}
