package com.vlkevheniy.carmanagement.util;

import com.vlkevheniy.carmanagement.data.Car;
import com.vlkevheniy.carmanagement.exception.ReportGenerationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVGenerator {


    public static ByteArrayResource generateCsvReport(List<Car> cars) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new PrintWriter(out, true, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.withHeader("Id", "Model", "Year", "Brand", "Price").withRecordSeparator("\r\n"))) {
            for (Car car : cars) {
                printer.printRecord(car.getId(), car.getModel(), car.getYear(), car.getBrand().getName(), car.getPrice());
            }
            printer.flush();
            return new ByteArrayResource(out.toByteArray());
        } catch (IOException e) {
            throw new ReportGenerationException("Failed to generate CSV report", e);
        }
    }
}
