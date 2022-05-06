package ko.fxlogviewer.readers.inter;

import java.util.ArrayList;

public interface LogReader {

	ArrayList<String[]> getData();

	ArrayList<String> getHeaderColumns() throws Exception;
}
