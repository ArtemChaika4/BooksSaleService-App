package ua.edu.dnu.booksales.pdf;


import java.io.File;


public interface PDFConvertable {
    void saveToPDF(File directory) throws Exception;
}
