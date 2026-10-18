package com.vetopia.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.*;


public class DataDrogasLoader {

    // Primera fila del Excel: encabezados (NOMBRE, PRECIO COMPRA, ...)
    private static final int FILA_ENCABEZADO = 0;

    private String[][] matrizDatos;

    // Lee el archivo de excel (en classpath) y lo convierte en una matriz de Strings
    public void poblarMatrizDesdeExcel(String recurso) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(recurso)) {
            if (is == null) {
                throw new IllegalStateException("No se encontró el recurso en el classpath: " + recurso);
            }

            try (Workbook workbook = WorkbookFactory.create(is)) {
                Sheet sheet = workbook.getSheetAt(0);

                //dimensiones (excluye la fila de encabezado)
                int totalFilas = sheet.getLastRowNum() + 1;
                int filasDatos = Math.max(0, totalFilas - FILA_ENCABEZADO - 1);
                int maxColumnas = 0;
                for (Row row : sheet) {
                    if (row != null && row.getLastCellNum() > maxColumnas) {
                        maxColumnas = row.getLastCellNum();
                    }
                }

                //Inicializar la matriz con el tamaño exacto encontrado
                this.matrizDatos = new String[filasDatos][maxColumnas];

                // Herramienta de POI para formatear cualquier celda
                DataFormatter formatter = new DataFormatter();

                // 3. Poblar la matriz recorriendo solo las filas de datos
                for (int i = 0; i < filasDatos; i++) {
                    Row row = sheet.getRow(FILA_ENCABEZADO + 1 + i);

                    for (int j = 0; j < maxColumnas; j++) {
                        if (row == null) {
                            this.matrizDatos[i][j] = "";
                            continue;
                        }

                        Cell cell = row.getCell(j);
                        this.matrizDatos[i][j] = leerCelda(cell, formatter);
                    }
                }

                System.out.println("Matriz poblada con éxito. Tamaño: " + filasDatos + "x" + maxColumnas);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Error al leer el archivo Excel: " + recurso, e);
        }
    }

    // Devuelve el valor crudo de la celda: las numéricas se leen sin formato
    // de moneda (evita "$60.520" y permite parsearlas como número)
    private String leerCelda(Cell cell, DataFormatter formatter) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC && !DateUtil.isCellDateFormatted(cell)) {
            double valor = cell.getNumericCellValue();
            if (valor == Math.rint(valor)) {
                return Long.toString((long) valor);
            }
            return Double.toString(valor);
        }
        return formatter.formatCellValue(cell).trim();
    }

    public String[][] getMatrizDatos() {
        return this.matrizDatos;
    }


    public void imprimirMatriz() {
        for (String[] fila : matrizDatos) {
            for (String celda : fila) {
                System.out.print("[" + celda + "]\t");
            }
            System.out.println();
        }
    }
    
}
