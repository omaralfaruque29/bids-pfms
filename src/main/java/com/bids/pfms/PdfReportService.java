package com.bids.pfms;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfReportService {
    private int empID;
    private String employeeName;
    private String designation;
    private String period;
    private int startingYearofPeriod;
    private int endingYearofPeriod;
    private String startingDateofPeriod;
    private String endingDateofPeriod;
    private int gpfAccountNo;

    private double openingSubscription;
    private double subscriptionForCurrentFY;
    private double openingInterest;
    private double interestForPreviousFY;
    private double totalBalance;
    private double loanBalance;
    private double netBalance;

    public void getProvidentFund(int employeeID){
        try {
            File file = new File("F:\\Bids\\pfms\\other resources\\provident fund 2024-25.xlsx");
            FileInputStream fileInputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            String periodString = sheet.getRow(1).getCell(3).getStringCellValue();
            Pattern pattern = Pattern.compile("\\((.*?)\\)");
            Matcher matcher = pattern.matcher(periodString);
            if (matcher.find()) {
                period = matcher.group(1); // content inside brackets ()
                String[] dates = period.split(" to ", 2);
                startingDateofPeriod = dates[0];
                endingDateofPeriod = dates[1];
                startingYearofPeriod = Integer.parseInt(startingDateofPeriod.substring(startingDateofPeriod.lastIndexOf(".")+ 1, startingDateofPeriod.length()));
                endingYearofPeriod = Integer.parseInt(endingDateofPeriod.substring(endingDateofPeriod.lastIndexOf(".")+ 1));
//                System.out.println("period: " + period);
//                System.out.println("startingDateofPeriod: " +startingDateofPeriod);
//                System.out.println("endingDateofPeriod: " +endingDateofPeriod);
//                System.out.println("starting year: " + startingYearofPeriod);
//                System.out.println("ending year: " + endingYearofPeriod);
            } else {
                System.out.println("No brackets found.");
            }

            // Iterating over rows using iterator
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if( row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC && row.getCell(1).getNumericCellValue() == employeeID){
                    empID = employeeID;
                    //employeeID = (int) row.getCell(1).getNumericCellValue();
                    String[] employeeNameAndDesignation = row.getCell(2).getStringCellValue().split(", ", 2);
                    employeeName  = employeeNameAndDesignation[0];
                    designation = employeeNameAndDesignation[1];
                    openingSubscription = row.getCell(3).getNumericCellValue();
                    subscriptionForCurrentFY = row.getCell(5).getNumericCellValue();
                    openingInterest = row.getCell(6).getNumericCellValue();
                    interestForPreviousFY = row.getCell(7).getNumericCellValue();
                    totalBalance = Math.round(row.getCell(8).getNumericCellValue() * 100.0) / 100.0;
                    loanBalance = row.getCell(9).getNumericCellValue();
                    netBalance = Math.round(row.getCell(10).getNumericCellValue() * 100.0) / 100.0;

                    System.out.println("Employee ID: " +empID);
                    System.out.println("Employee Name: " +employeeName);
                    System.out.println("Designation: " +designation);
                    System.out.println("GPF A/C No: ");
                    System.out.println("Opening subscription 'as on " +startingDateofPeriod+ "': " + openingSubscription);
                    System.out.println("Subscription for the FY " +startingYearofPeriod+ "-" + endingYearofPeriod + ": " + subscriptionForCurrentFY);
                    System.out.println("Opening interest 'as on " +startingDateofPeriod+ "': " + openingInterest);
                    System.out.println("Interest received FY " +startingYearofPeriod+ "-" +endingYearofPeriod+ " (acccrued): "+interestForPreviousFY);
                    System.out.println("Total member's balance " +endingDateofPeriod + ": " + totalBalance);
                    System.out.println("Loan balance 'as on " +endingDateofPeriod + "': " +loanBalance);
                    System.out.println("Net member's balance 'as on " +endingDateofPeriod + "': " +netBalance);

                    break;
                }
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] createPdfReport(){
        getProvidentFund(4107);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.setMargins(0, 36, 36, 36);
            // Header
            Image img = new Image(ImageDataFactory.create("F:\\Bids\\pfms\\other resources\\hdr.jpg"));
            img.setWidth(UnitValue.createPercentValue(100));
            document.add(img);
            document.add(new Paragraph()
                    .addTabStops(new TabStop(520, TabAlignment.RIGHT))  // 520pt = ~right edge of A4
                    .add("Ref No: 462278")   // Left-aligned by default
                    .add(new Tab())          // Insert tab to jump to right tab stop
                    .add("Date: " +LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));      // Right-aligned);
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Statement of Provident Fund")
                    .setUnderline(1f, -2f)
                    .setBold().setFontSize(12)                    .setTextAlignment(TextAlignment.CENTER));

            // Personal details
            float[] columnWidths1 = {1, 0.2f, 6}; // Label, colon, value
            Table infoTable = new Table(UnitValue.createPercentArray(columnWidths1)).useAllAvailableWidth();

            infoTable.addCell(new Cell().add(new Paragraph("Employee ID")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(""+empID)).setBorder(Border.NO_BORDER).setPadding(0));

            infoTable.addCell(new Cell().add(new Paragraph("Name")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(employeeName)).setBorder(Border.NO_BORDER).setPadding(0));

            infoTable.addCell(new Cell().add(new Paragraph("Designation")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(designation)).setBorder(Border.NO_BORDER).setPadding(0));

            infoTable.addCell(new Cell().add(new Paragraph("Period")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(period)).setBorder(Border.NO_BORDER).setPadding(0));

            infoTable.addCell(new Cell().add(new Paragraph("GPF A/C No")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(":")).setBorder(Border.NO_BORDER).setPadding(0));
            infoTable.addCell(new Cell().add(new Paragraph(""+gpfAccountNo)).setBorder(Border.NO_BORDER).setPadding(0));

            document.add(infoTable);

            // Table
            float[] columnWidths = {1, 4, 3};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            // Header row (all center-aligned)
            table.addHeaderCell(new Cell().add(new Paragraph("Serial No.")).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Description")).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount (TK)")).setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY));

            // Helper: center-aligned cell inline
            java.util.function.Function<String, Cell> createCenterCell = (text) ->
                    new Cell().add(new Paragraph(text)).setTextAlignment(TextAlignment.CENTER);

            // Table rows (all center-aligned)
            table.addCell(createCenterCell.apply("1"));
            table.addCell(createCenterCell.apply("Opening Subscription as on " + startingDateofPeriod));
            table.addCell(createCenterCell.apply(""+openingSubscription));

            table.addCell(createCenterCell.apply("2"));
            table.addCell(createCenterCell.apply("Subscription for FY " +startingYearofPeriod + "-" +endingYearofPeriod));
            table.addCell(createCenterCell.apply(""+subscriptionForCurrentFY));

            table.addCell(createCenterCell.apply("3"));
            table.addCell(createCenterCell.apply("Opening Interest as on " + startingDateofPeriod));
            table.addCell(createCenterCell.apply(""+openingInterest));

            table.addCell(createCenterCell.apply("4"));
            table.addCell(createCenterCell.apply("Interest Received FY " +startingYearofPeriod + "-" +endingYearofPeriod + "(accrued)"));
            table.addCell(createCenterCell.apply("-"));

            table.addCell(createCenterCell.apply("5=sum(1:4)"));
            table.addCell(createCenterCell.apply("Total Member’s Balance on "+endingDateofPeriod));
            table.addCell(createCenterCell.apply(""+totalBalance).setBold());

            table.addCell(createCenterCell.apply("6"));
            table.addCell(createCenterCell.apply("Loan Balance as on " + endingDateofPeriod));
            table.addCell(createCenterCell.apply(""+loanBalance));

            table.addCell(createCenterCell.apply("7=(5-6)"));
            table.addCell(createCenterCell.apply("Net Member’s Balance as on "+endingDateofPeriod));
            table.addCell(createCenterCell.apply(""+netBalance).setBold());

            document.add(table);
            // Footer note
            String words = CurrencyAmountToWordsConverter.convertAmountToWords(netBalance);
            document.add(new Paragraph(words)
                    .setFontSize(10)
                    .setFont(PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_OBLIQUE)));
            document.add(new Paragraph("\n\n\n"));
            document.add(new Paragraph()
                    .addTabStops(new TabStop(520, TabAlignment.RIGHT))  // 520pt = ~right edge of A4
                    .add("---------------------")   // Left-aligned by default
                    .add(new Tab())          // Insert tab to jump to right tab stop
                    .add("----------------\n")
                    .addTabStops(new TabStop(520, TabAlignment.RIGHT))  // 520pt = ~right edge of A4
                    .add("Accounts Asst.")   // Left-aligned by default
                    .add(new Tab())          // Insert tab to jump to right tab stop
                    .add("Accountant"));      // Right-aligned);

            document.close();
            System.out.println("Invoice PDF created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public void sendPfReportToEmail(){
        final String username = "raj.cse0929@gmail.com";        // Sender Gmail
        final String password = "wxswqtilusprkjai";           // Use App Password, NOT Gmail password // omar@bids.org.bd:gheffbuwybnfupza //raj.cse0929@gmail.com: wxswqtilusprkjai
        final String toEmail = "raj.cse0929@gmail.com";          // Recipient Email

        String subject = "Provident Fund (PF) Report";
        String bodyText = "Dear member,\nPlease find your provident fund (PF) report in the email attachment.\n\nRegards,\nOmar Al Faruque \nProgrammer, BIDS\nEmail: omar@bids.org.bd\nCell: 01738404500\nTelephone: +88-02-58160474\nPABX: 258";
        String pdfFilePath = "C://Users//CLOUDSLIP//Downloads//PF Report.pdf";
        // Gmail SMTP configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Text part
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(bodyText);

            // PDF attachment part
            MimeBodyPart attachmentPart = new MimeBodyPart();
            File file = new File(pdfFilePath);
            attachmentPart.attachFile(file);

            // Combine parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            // Send email
            Transport.send(message);
            System.out.println("Email sent successfully with PDF attachment!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
