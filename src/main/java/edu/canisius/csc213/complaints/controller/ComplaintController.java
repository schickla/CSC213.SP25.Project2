package edu.canisius.csc213.complaints.controller;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ComplaintController {

    private final List<Complaint> complaints;
    private final ComplaintSimilarityService similarityService;

    public ComplaintController(List<Complaint> complaints, ComplaintSimilarityService similarityService) {
        this.complaints = complaints;
        this.similarityService = similarityService;
    }

    @GetMapping("/complaint")
public String showComplaint(@RequestParam(name = "index", defaultValue = "0") String indexParam, Model model) {
    int index;
    int max = complaints.size();

    // 1️⃣ Parse the input string safely
    try {
        index = Integer.parseInt(indexParam);
    } catch (NumberFormatException e) {
        model.addAttribute("error", "Invalid input. Please enter a number between 0 and " + (max - 1));
        index = 0; // fallback to 0
    }

    // 2️⃣ Check if index is out of bounds
    if (index < 0 || index >= max) {
        model.addAttribute("error", "Index out of range. Must be between 0 and " + (max - 1));
        index = 0; // fallback to 0
    }

    // 3️⃣ Get complaint and build view
    Complaint current = complaints.get(index);
    List<Complaint> similar = similarityService.findTop3Similar(current);

    model.addAttribute("complaint", current);
    model.addAttribute("similarComplaints", similar);
    model.addAttribute("prevIndex", Math.max(0, index - 1));
    model.addAttribute("nextIndex", Math.min(max - 1, index + 1));

    return "complaint";
    }
    @GetMapping("/search")
    public String searchByCompany(@RequestParam(name = "company", required = false) String company, Model model) {
        if (company == null || company.trim().isEmpty()) {
            model.addAttribute("error", "Please enter a company name.");
            model.addAttribute("results", List.of());
            return "search";
        }

        String query = company.toLowerCase();

        List<Complaint> matches = complaints.stream()
                .filter(c -> c.getCompany() != null && c.getCompany().toLowerCase().contains(query))
                .toList();

        if (matches.isEmpty()) {
            model.addAttribute("error", "No complaints found for that company.");
        }

        model.addAttribute("results", matches);
        model.addAttribute("company", company);
        return "search";
    }

}
