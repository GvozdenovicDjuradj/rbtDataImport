package com.rbt.test.service;

import com.rbt.test.error.RbtException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class CsvParser {
    public <T> List<T> parse(MultipartFile file, Function<String, T> rowMapper, int skipLines) {
        try {
            List<T> list = new ArrayList<>();


            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                int count = 0;
                String line = reader.readLine();
                while (line != null) {
                    if (count < skipLines) {
                        count++;
                        line = reader.readLine();
                        continue;
                    }

                    list.add(rowMapper.apply(line));
                    line = reader.readLine();
                }
            }

            return list;

        } catch (Exception ex) {
            throw new RbtException(400, "failed_to_process_file", ex);
        }
    }
}
