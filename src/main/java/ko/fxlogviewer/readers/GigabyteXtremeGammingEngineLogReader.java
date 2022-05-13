package ko.fxlogviewer.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import ko.fxlogviewer.readers.inter.LogReader;

public class GigabyteXtremeGammingEngineLogReader implements LogReader {

    final String file;

    public GigabyteXtremeGammingEngineLogReader(String fileName) {
        this.file = fileName;
    }


    public ArrayList<String> getHeaderColumns() throws Exception {

        ArrayList<String> columns = new ArrayList<>();
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        Stream.of(line.split("\\t+")).forEach(e -> columns.add(e.trim()));
        columns.set(0, "");
        reader.close();
        return columns;
    }


    public ArrayList<String[]> getData() {
        ArrayList<String[]> data = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            @SuppressWarnings("UnusedAssignment") String line = reader.readLine();
            line = reader.readLine();//ignore first line
            while (line != null) {
                data.add(Stream.of(line.split("\\t+")).map(String::trim).toArray(String[]::new));
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}

