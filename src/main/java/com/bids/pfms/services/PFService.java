package com.bids.pfms.services;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PFService {
    private int employeeID;
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

    public void getPF(Model model, int empID){
        try {
            this.employeeID = empID;
            File file = new File("F:\\Java workspace\\BIDS\\bids-pfms\\other resources\\provident fund 2024-25.xlsx");
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
            } else {
                System.out.println("No brackets found.");
            }

            // Iterating over rows using iterator
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if( row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC && row.getCell(1).getNumericCellValue() == employeeID){

                    //employeeID = (int) row.getCell(1).getNumericCellValue();
                    String[] employeeNameAndDesignation = row.getCell(2).getStringCellValue().split(", ", 2);
                    employeeName  = employeeNameAndDesignation[0];
                    designation = employeeNameAndDesignation[1];
                    openingSubscription = row.getCell(3).getNumericCellValue();
                    subscriptionForCurrentFY = row.getCell(5).getNumericCellValue();
                    openingInterest = row.getCell(6).getNumericCellValue();
                    interestForPreviousFY = row.getCell(7).getNumericCellValue();
                    //totalBalance = row.getCell(8).getNumericCellValue();
                    totalBalance = Math.round(row.getCell(8).getNumericCellValue() * 100.0) / 100.0;
                    loanBalance = row.getCell(9).getNumericCellValue();
                    //netBalance = row.getCell(10).getNumericCellValue();
                    netBalance = Math.round(row.getCell(10).getNumericCellValue() * 100.0) / 100.0;

                    //printAllData();

                    model.addAttribute("empID", employeeID);
                    model.addAttribute("employeeName", employeeName);
                    model.addAttribute("designation", designation);
                    model.addAttribute("period", period);
                    model.addAttribute("GPFAccountNo", "");
                    model.addAttribute("t1", "Opening subscription 'as on " +startingDateofPeriod + "'");
                    model.addAttribute("t11", "" +openingSubscription);
                    model.addAttribute("t2", "Subscription for the FY " +startingYearofPeriod+ "-" + endingYearofPeriod);
                    model.addAttribute("t21", "" +subscriptionForCurrentFY);
                    model.addAttribute("t3", "Opening interest 'as on " +startingDateofPeriod + "'");
                    model.addAttribute("t31", "" +openingInterest);
                    model.addAttribute("t4", "Interest received FY " +startingYearofPeriod+ "-" +endingYearofPeriod+ " (acccrued)");
                    model.addAttribute("t41", "" +interestForPreviousFY);
                    model.addAttribute("t5", "Total member's balance " +endingDateofPeriod);
                    model.addAttribute("t51", "" +totalBalance);
                    model.addAttribute("t6", "Loan balance 'as on " +endingDateofPeriod + "'");
                    model.addAttribute("t61", ""+loanBalance);
                    model.addAttribute("t7", "Net member's balance 'as on " +endingDateofPeriod + "'");
                    model.addAttribute("t71", ""+netBalance);
                    model.addAttribute("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    break;
                }
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("eight", "last data");
    }

    private void printAllData(){
        System.out.println("Employee ID: " +employeeID);
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
    }

}
