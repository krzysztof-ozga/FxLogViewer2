package ko.fxlogviewer.readers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import ko.fxlogviewer.readers.inter.LogReader;

public class MsiAfterburnerLogReader implements LogReader {

    final String file;

    public MsiAfterburnerLogReader(String fileName) {
        this.file = fileName;
    }

    public ArrayList<String> getHeaderColumns() {

        ArrayList<String> columns = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";

            while (true) {
                line = reader.readLine();
                if (line.startsWith("02,")) {
                    Stream.of(line.split(",")).forEach(e -> columns.add(e.trim()));
                    columns.remove(0);
                    columns.set(0, "");
                    break;
                } else
                    continue;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columns;
    }

    public ArrayList<String[]> getData() {

        ArrayList<String[]> data = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {

                if (line.startsWith("80,")) {
                    data.add(Stream.of(line.substring(3).split(",")).map(string -> (string.equals("N/A")) ? "0" : string)
                            .toArray(String[]::new));
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

}
