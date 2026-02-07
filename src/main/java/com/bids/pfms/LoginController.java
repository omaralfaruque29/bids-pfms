package com.bids.pfms;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class LoginController {

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

    private int employeeID;



    @RequestMapping("/hello")
    @ResponseBody
    public String getHello(){
        return "hello world";
    }

    @GetMapping("login-page")
    public String getLoginPage(){
        System.out.println("login-page called");
        return "login-page";
    }

    @PostMapping("login")
    public String login(Model model){
        System.out.println("login method called");
        ProvidentFund providentFund = new ProvidentFund();
        //providentFund.getProvidentFund(4107);
        employeeID=4107;
        //providentFund.sendPFtoEmail();
        //providentFund.createReport();


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
                    //totalBalance = row.getCell(8).getNumericCellValue();
                    totalBalance = Math.round(row.getCell(8).getNumericCellValue() * 100.0) / 100.0;
                    loanBalance = row.getCell(9).getNumericCellValue();
                    //netBalance = row.getCell(10).getNumericCellValue();
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

                    model.addAttribute("empID", empID);
                    model.addAttribute("employeeName", employeeName);
                    model.addAttribute("designation", designation);
                    model.addAttribute("period", period);
                    model.addAttribute("GPFAccountNo", "");

                    model.addAttribute("t1", "Opening subscription as on " +startingDateofPeriod);
                    model.addAttribute("t11", "" +openingSubscription);
                    model.addAttribute("t2", "Subscription for the FY " +startingYearofPeriod+ "-" + endingYearofPeriod);
                    model.addAttribute("t21", "" +subscriptionForCurrentFY);
                    model.addAttribute("t3", "Opening interest as on " +startingDateofPeriod);
                    model.addAttribute("t31", "" +openingInterest);
                    model.addAttribute("t4", "Interest received FY " +startingYearofPeriod+ "-" +endingYearofPeriod+ " (acccrued)");
                    model.addAttribute("t41", "" +interestForPreviousFY);
                    model.addAttribute("t5", "Total member's balance " +endingDateofPeriod);
                    model.addAttribute("t51", "" +totalBalance);
                    model.addAttribute("t6", "Loan balance 'as on " +endingDateofPeriod);
                    model.addAttribute("t61", ""+loanBalance);
                    model.addAttribute("t7", "Net member's balance 'as on " +endingDateofPeriod);
                    model.addAttribute("t71", ""+netBalance);
                break;
                }
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("eight", "last data");
        return "pf-page";
    }
}
