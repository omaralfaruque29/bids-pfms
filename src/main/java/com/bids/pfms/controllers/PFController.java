package com.bids.pfms.controllers;

import com.bids.pfms.services.PFService;
import com.bids.pfms.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Controller
public class PFController {

    @Autowired
    private ReportService reportService;
    @Autowired
    private PFService pfService;

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

    @GetMapping("api/pf/{empID}")
    public String redirectToPF(@PathVariable String empID){
        String encodedEmpID = Base64.getUrlEncoder().withoutPadding().encodeToString(empID.getBytes());
        return "redirect:/api/pf/pf-page/" + encodedEmpID;
    }
    @GetMapping("api/pf/pf-page/{encodedEmpID}")
    public String PF(Model model, @PathVariable String encodedEmpID){
        int employeeID = Integer.parseInt(new String(Base64.getUrlDecoder().decode(encodedEmpID), StandardCharsets.UTF_8));
        pfService.getPF(model, employeeID);
        return "pf-page";
    }

//    @GetMapping("api/pf/redirect-to-report/{empID}")
//    public String redirectToDownloadReport(@PathVariable String empID){
//        String encodedEmpID = Base64.getUrlEncoder().withoutPadding().encodeToString(empID.getBytes());
//        return "redirect:/api/pf/report/download/" + encodedEmpID;
//    }
    @GetMapping("api/pf/report/download/{empID}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable String empID) throws Exception {
        //int employeeID = Integer.parseInt(new String(Base64.getUrlDecoder().decode(encodedEmpID), StandardCharsets.UTF_8));
        byte[] pdfBytes = reportService.createPFReportAsPdf(Integer.parseInt(empID));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=PF Report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/send-email")
    @ResponseBody
    public ResponseEntity<String> sendReportToEmail() {
        try {
            reportService.sendPfReportToEmail();
            return ResponseEntity.ok("Email sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email!");
        }
    }


}
