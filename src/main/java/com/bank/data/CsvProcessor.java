package com.bank.data;

import com.bank.model.Account;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvProcessor {

    public void processBatchFile(String filePath) {
        System.out.println("Début du traitement du fichier : " + filePath);

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {

            lines
                    .parallel()
                    .skip(1)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> {
                        parseAndExecute(line);
                    });

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }


    private void parseAndExecute(String line) {
        try {
            String[] parts = line.split(";");
            if (parts.length < 3) return;

            String sourceAcc = parts[0];
            String destAcc = parts[1];
            double amount = Double.parseDouble(parts[2]);
            String reason = parts.length > 3 ? parts[3] : "Virement";

            System.out.println(String.format(
                    "[Thread: %s] Traitement : %s -> %s : %.2f (%s)",
                    Thread.currentThread().getName(), sourceAcc, destAcc, amount, reason
            ));

            // TODO: Appeler ici bankService.transfer(sourceAcc, destAcc, amount);

        } catch (NumberFormatException e) {
            System.err.println("Erreur de format de nombre dans la ligne : " + line);
        } catch (Exception e) {
            System.err.println("Erreur inconnue sur la ligne : " + line);
        }
    }
}